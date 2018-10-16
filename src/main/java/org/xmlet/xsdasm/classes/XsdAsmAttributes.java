package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdasm.classes.infrastructure.RestrictionValidator;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdList;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmEnum.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

/**
 * This class is responsible to generate all the code that is attribute related.
 */
class XsdAsmAttributes {

    /**
     * Helper {@link Map} used to apply casts to different data types received in the constructor of the generated
     * attribute classes
     */
    private static Map<String, Consumer<MethodVisitor>> adjustmentsMapper;

    static{
        adjustmentsMapper = new HashMap<>();

        adjustmentsMapper.put("java/lang/Integer", XsdAsmAttributes::intAdjustment);
        adjustmentsMapper.put("java/lang/Long", XsdAsmAttributes::longAdjustment);
        adjustmentsMapper.put("java/lang/Float", XsdAsmAttributes::floatAdjustment);
        adjustmentsMapper.put("java/lang/Short", XsdAsmAttributes::shortAdjustment);
    }

    private XsdAsmAttributes(){}

    /**
     * Generates a method to add a given {@link XsdAttribute}.
     * @param classWriter The {@link ClassWriter} of the class where the method that adds the {@link XsdAttribute} will
     *                    be generated.
     * @param elementAttribute The {@link XsdAttribute} containing the information to create the method.
     * @param returnType The return type of the generated method. This return type is different based on if the class
     *                   where the method is being generated is a class or an interface.
     * @param className The name of the class where the method is being added.
     * @param apiName The name of the generated fluent interface.
     */
    static void generateMethodsForAttribute(ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String className, String apiName) {
        String attributeName = ATTRIBUTE_PREFIX + getCleanName(elementAttribute);
        String camelCaseName = attributeName.toLowerCase().charAt(0) + attributeName.substring(1);
        String attributeClassType = getFullClassTypeName(getAttributeName(elementAttribute), apiName);
        String attributeGroupInterfaceType = getFullClassTypeName(className, apiName);
        boolean isInterfaceMethod = isInterfaceMethod(returnType);
        String attrName = firstToLower(ATTRIBUTE_PREFIX + getCleanName(elementAttribute));
        String javaType = getFullJavaTypeDesc(elementAttribute);
        String signature;

        if (attributeHasEnum(elementAttribute)){
            javaType = getFullClassTypeNameDesc(getEnumName(elementAttribute), apiName);
        }

        if (isInterfaceMethod){
            signature = "(" + javaType + ")TT;";
        } else {
            signature = "(" + javaType + ")" + returnType.substring(0, returnType.length() - 1) + "<TZ;>;";
        }

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, camelCaseName, "(" + javaType + ")" + returnType, signature, null);
        mVisitor.visitLocalVariable(attrName, javaType, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, attributeClassType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, attributeClassType, CONSTRUCTOR, "(" + javaType + ")V", false);

        if (isInterfaceMethod){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, attributeGroupInterfaceType, "addAttr", "(" + attributeTypeDesc + ")" + elementTypeDesc, true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, attributeGroupInterfaceType, "addAttr", "(" + attributeTypeDesc + ")" + elementTypeDesc, false);
            mVisitor.visitTypeInsn(CHECKCAST, attributeGroupInterfaceType);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();
    }

    /**
     * Creates a class which represents a {@link XsdAttribute} object.
     * @param attribute The {@link XsdAttribute} type that contains the required information.
     * @param apiName The name of the generated fluent interface.
     */
    static void generateAttribute(XsdAttribute attribute, String apiName){
        String camelAttributeName = getAttributeName(attribute);
        XsdList list = getAttributeList(attribute);
        String javaTypeDesc = list != null ? "L" + JAVA_LIST + "<" + getFullJavaTypeDesc(list.getItemType()) + ">;" : getFullJavaTypeDesc(attribute);
        boolean hasEnum = attributeHasEnum(attribute);

        if (hasEnum){
            getAttributeRestrictions(attribute)
                    .stream()
                    .filter(restriction -> restriction.getEnumeration() != null)
                    .forEach(restriction -> createEnum(attribute, restriction.getEnumeration(), apiName));
        }

        ClassWriter attributeWriter = generateClass(camelAttributeName, baseAttributeType, null, "L" + baseAttributeType + "<" + javaTypeDesc + ">;", ACC_PUBLIC + ACC_SUPER, apiName);

        String constructorArgDesc = getConstructorArgDesc(attribute, hasEnum, list, javaTypeDesc, apiName);

        MethodVisitor mVisitor = attributeWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + constructorArgDesc + ")V",  null, null);
        mVisitor.visitLocalVariable("attrValue", constructorArgDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);

        if (hasEnum){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, enumInterfaceType, "getValue", "()" + JAVA_OBJECT_DESC, true);
        }

        mVisitor.visitLdcInsn(attribute.getRawName());
        mVisitor.visitMethodInsn(INVOKESPECIAL, baseAttributeType, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + JAVA_STRING_DESC + ")V", false);

        if (attributeHasRestrictions(attribute)){
            if (hasEnum){
                String enumName = getEnumName(attribute);
                String enumType = getFullClassTypeName(enumName, apiName);

                mVisitor.visitVarInsn(ALOAD, 1);
                mVisitor.visitMethodInsn(INVOKEVIRTUAL, enumType, "getValue", "()" + getEnumContainingType(attribute), false);
                mVisitor.visitVarInsn(ASTORE, 2);
            }

            getAttributeRestrictions(attribute).forEach(restriction -> loadRestrictionToAttribute(mVisitor, restriction, list != null ? JAVA_LIST_DESC : javaTypeDesc.substring(1, javaTypeDesc.length() - 1), hasEnum ? 2 : 1));
        }

        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(hasEnum ? 3 : 4, hasEnum ? 3 : 2);
        mVisitor.visitEnd();

        writeClassToFile(camelAttributeName, attributeWriter, apiName);
    }

    private static String getConstructorArgDesc(XsdAttribute attribute, boolean hasEnum, XsdList list, String javaTypeDesc, String apiName) {
        if (hasEnum){
            return getFullClassTypeNameDesc(getEnumName(attribute), apiName);
        } else {
            if (list != null) {
                return JAVA_LIST_DESC;
            } else {
                return javaTypeDesc;
            }
        }
    }

    /**
     * Uses the information present in the {@link XsdRestriction} object and hardcodes the restriction values in
     * method calls to the {@link RestrictionValidator} validator methods.
     * @param mVisitor The constructor method visitor.
     * @param restriction The current restriction.
     * @param javaType The java type of the type received in the constructor method.
     * @param index The current index of the stack.
     */
    private static void loadRestrictionToAttribute(MethodVisitor mVisitor, XsdRestriction restriction, String javaType, int index) {
        XsdLength length = restriction.getLength();
        XsdMaxLength maxLength = restriction.getMaxLength();
        XsdMinLength minLength = restriction.getMinLength();
        XsdFractionDigits fractionDigits = restriction.getFractionDigits();
        XsdMaxExclusive maxExclusive = restriction.getMaxExclusive();
        XsdMaxInclusive maxInclusive = restriction.getMaxInclusive();
        XsdMinExclusive minExclusive = restriction.getMinExclusive();
        XsdMinInclusive minInclusive = restriction.getMinInclusive();
        XsdPattern pattern = restriction.getPattern();
        XsdTotalDigits totalDigits = restriction.getTotalDigits();

        if (length != null){
            mVisitor.visitIntInsn(BIPUSH, length.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateLength", "(I" + javaType + ")V", false);
        }

        if (maxLength != null){
            mVisitor.visitLdcInsn(maxLength.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMaxLength", "(I" + javaType + ")V", false);
        }

        if (minLength != null){
            mVisitor.visitLdcInsn(minLength.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMinLength", "(I" + javaType + ")V", false);
        }

        if (maxExclusive != null){
            mVisitor.visitLdcInsn(maxExclusive.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMaxExclusive", "(DD)V", false);
        }

        if (maxInclusive != null){
            mVisitor.visitLdcInsn(maxInclusive.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMaxInclusive", "(DD)V", false);
        }

        if (minExclusive != null){
            mVisitor.visitLdcInsn(minExclusive.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMinExclusive", "(DD)V", false);
        }

        if (minInclusive != null){
            mVisitor.visitLdcInsn(minInclusive.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateMinInclusive", "(DD)V", false);
        }

        if (fractionDigits != null){
            mVisitor.visitIntInsn(BIPUSH, fractionDigits.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateFractionDigits", "(ID)V", false);
        }

        if (totalDigits != null){
            mVisitor.visitIntInsn(BIPUSH, totalDigits.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            numericAdjustment(mVisitor, javaType);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validateTotalDigits", "(ID)V", false);
        }

        if (pattern != null){
            mVisitor.visitLdcInsn(pattern.getValue());
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitMethodInsn(INVOKESTATIC, restrictionValidatorType, "validatePattern", "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", false);
        }
    }

    /**
     * Applies a cast to numeric types, i.e. int, short, long, to double.
     * @param mVisitor The visitor of the attribute constructor.
     * @param javaType The type of the argument received in the constructor.
     */
    private static void numericAdjustment(MethodVisitor mVisitor, String javaType) {
        adjustmentsMapper.getOrDefault(javaType, XsdAsmAttributes::doubleAdjustment).accept(mVisitor);
    }

    private static void intAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "doubleValue", "()D", false);
    }

    private static void floatAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "doubleValue", "()D", false);
    }

    private static void shortAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "doubleValue", "()D", false);
    }

    private static void longAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "doubleValue", "()D", false);
    }

    private static void doubleAdjustment(MethodVisitor mVisitor) {
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
    }

    /**
     * Asserts if the received {@link XsdAttribute} object has restrictions. Enumeration restrictions are discarded
     * since they are already validated by the usage of {@link Enum} classses.
     * @param attribute The {@link XsdAttribute} object.
     * @return Whether the received attribute has restrictions other than {@link XsdEnumeration}.
     */
    private static boolean attributeHasRestrictions(XsdAttribute attribute){
        List<XsdRestriction> restrictions = getAttributeRestrictions(attribute);

        //noinspection SimplifiableIfStatement
        if (restrictions.isEmpty()){
            return false;
        }

        return restrictions.stream().anyMatch(XsdAsmAttributes::hasRestrictionsOtherThanEnumeration);
    }

    /**
     * Checks if any type other than Enumeration is present in the {@link XsdRestriction} object.
     * @param restriction The {@link XsdRestriction} object.
     * @return Whether the received attribute has restrictions other than {@link XsdEnumeration}.
     */
    private static boolean hasRestrictionsOtherThanEnumeration(XsdRestriction restriction){
        return restriction.getMinExclusive() != null || restriction.getMinInclusive() != null ||
                restriction.getMaxExclusive() != null || restriction.getMaxInclusive() != null ||
                restriction.getMaxLength() != null || restriction.getMinLength() != null ||
                restriction.getLength() != null || restriction.getPattern() != null ||
                restriction.getTotalDigits() != null || restriction.getFractionDigits() != null;
    }

    /**
     * Obtains the attribute name.
     * Example:
     *      AttrTypeInteger (Object/String/Integer)
     *      AttrTypeEnumTypeContentType(EnumTypeContentType)
     *      AttrTypeEnumTypeStyle(EnumTypeStyle)
     * @param attribute The {@link XsdAttribute} object.
     * @return The name of the class based on this {@link XsdAttribute} object.
     */
    private static String getAttributeName(XsdAttribute attribute) {
        String name = ATTRIBUTE_PREFIX + getCleanName(attribute);

        if (attributeHasEnum(attribute)) {
            return name + getEnumName(attribute).replaceAll(name, "");
        }

        String javaType = getFullJavaTypeDesc(attribute);

        return name + javaType.substring(javaType.lastIndexOf('/') + 1, javaType.length() - 1);
    }

    /**
     * Returns the {@link XsdList} of the {@link XsdAttribute} object.
     * @param attribute The {@link XsdAttribute} object.
     * @return The contained {@link XsdList} object or null if not present.
     */
    private static XsdList getAttributeList(XsdAttribute attribute) {
        return attribute.getXsdSimpleType() != null ? attribute.getXsdSimpleType().getList() : null;
    }

    /**
     * @param attribute The received {@link XsdAttribute} object.
     * @return The java type of the elements of the {@link Enum} generated based on the possible values for the received
     * {@link XsdAttribute} object.
     */
    private static String getEnumContainingType(XsdAttribute attribute){
        if (attributeHasEnum(attribute)){
            List<XsdRestriction> restrictions = getAttributeRestrictions(attribute);

            return getJavaType(restrictions.get(0).getBase());
        }

        return null;
    }
}
