package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdList;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmEnum.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

class XsdAsmAttributes {

    private XsdAsmAttributes(){}

    /**
     * Generates a method to add a given attribute.
     * @param classWriter The class where the fields will be added.
     * @param elementAttribute The attribute containing the information to create the method. (Only String fields are being supported)
     */
    static void generateMethodsForAttribute(ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String className, String apiName) {
        String attributeName = ATTRIBUTE_PREFIX + getCleanName(elementAttribute);
        String camelCaseName = attributeName.toLowerCase().charAt(0) + attributeName.substring(1);
        String attributeClassType = getFullClassTypeName(getAttributeName(elementAttribute), apiName);
        String attributeGroupInterfaceType = getFullClassTypeName(className, apiName);
        MethodVisitor mVisitor;
        boolean isInterfaceMethod = isInterfaceMethod(returnType);

        String javaType = getFullJavaType(elementAttribute);

        if (attributeHasEnum(elementAttribute)){
            javaType = getFullClassTypeNameDesc(getEnumName(elementAttribute), apiName);
        }

        if (isInterfaceMethod){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, camelCaseName, "(" + javaType + ")" + returnType, "(" + javaType + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, camelCaseName, "(" + javaType + ")" + returnType, "(" + javaType + ")" + returnType.substring(0, returnType.length() - 1) + "<TZ;>;", null);
        }

        String attrName = "attr" + getCleanName(elementAttribute);
        attrName = attrName.substring(0, 1).toLowerCase() + attrName.substring(1);

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
     * Creates a class which represents an attribute.
     * @param attribute The XsdAttribute type that contains the required information.
     * @param apiName The api this class will belong.
     */
    static void generateAttribute(XsdAttribute attribute, String apiName){
        String camelAttributeName = getAttributeName(attribute);
        List<XsdRestriction> restrictions = getAttributeRestrictions(attribute);
        XsdList list = getAttributeList(attribute);
        String javaType = getFullJavaType(attribute);

        if (list != null){
            String fullJavaItemTypeDesc = getFullJavaType(list.getItemType());

            javaType = "L" + JAVA_LIST + "<" + fullJavaItemTypeDesc + ">;";
        }

        ClassWriter attributeWriter = generateClass(camelAttributeName, baseAttributeType, null, "L" + baseAttributeType + "<" + javaType + ">;", ACC_PUBLIC + ACC_SUPER, apiName);

        MethodVisitor mVisitor;

        if (attributeHasEnum(attribute)) {
            String enumName = getEnumName(attribute);
            String enumTypeDesc = getFullClassTypeNameDesc(enumName, apiName);

            mVisitor = attributeWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + enumTypeDesc + ")V",  null, null);
            mVisitor.visitLocalVariable("attrValue", enumTypeDesc, null, new Label(), new Label(),1);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, enumInterfaceType, "getValue", "()" + JAVA_OBJECT_DESC, true);
            mVisitor.visitLdcInsn(attribute.getName().replaceAll("[^a-zA-Z0-9]", ""));
            mVisitor.visitMethodInsn(INVOKESPECIAL, baseAttributeType, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + JAVA_STRING_DESC + ")V", false);
        } else {
            if (list != null){
                mVisitor = attributeWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_LIST_DESC + ")V", null, null);
                mVisitor.visitLocalVariable("attrValue", JAVA_LIST_DESC, null, new Label(), new Label(),1);
                mVisitor.visitCode();
                mVisitor.visitVarInsn(ALOAD, 0);
                mVisitor.visitVarInsn(ALOAD, 1);
            } else {
                mVisitor = attributeWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + javaType + ")V", null, null);
                mVisitor.visitLocalVariable("attrValue", javaType, null, new Label(), new Label(),1);
                mVisitor.visitCode();
                mVisitor.visitVarInsn(ALOAD, 0);
                mVisitor.visitVarInsn(ALOAD, 1);
                mVisitor.visitTypeInsn(CHECKCAST, javaType.substring(1, javaType.length() - 1));
            }

            mVisitor.visitLdcInsn(attribute.getName().replaceAll("[^a-zA-Z0-9]", ""));
            mVisitor.visitMethodInsn(INVOKESPECIAL, baseAttributeType, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + JAVA_STRING_DESC + ")V", false);
        }
        
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        loadRestrictionsToAttribute(attribute, attributeWriter, restrictions, camelAttributeName, apiName);

        writeClassToFile(camelAttributeName, attributeWriter, apiName);
    }

    /**
     * AttrType (Object/String/Integer)
     * AttrTypeContentType(EnumTypeContentType) NAMED
     * AttrTypeStyle(EnumTypeStyle)             NO NAME
     */
    private static String getAttributeName(XsdAttribute attribute) {
        String name = ATTRIBUTE_PREFIX + getCleanName(attribute);

        if (attributeHasEnum(attribute)) {
            return name + getEnumName(attribute).replaceAll(name, "");
        }

        String javaType = getFullJavaType(attribute);

        return name + javaType.substring(javaType.lastIndexOf('/') + 1, javaType.length() - 1);
    }

    /**
     * Loads all the existing restrictions to the attribute class. It inserts entries in a list on the static
     * constructor of the class.
     * @param attributeWriter The class writer of the attribute class.
     * @param restrictions The list of restrictions for the attribute.
     * @param camelAttributeName The attribute class name.
     * @param apiName The api this attribute will belong.
     */
    private static void loadRestrictionsToAttribute(XsdAttribute attribute, ClassWriter attributeWriter, List<XsdRestriction> restrictions, String camelAttributeName, String apiName) {
        String attributeType = getFullClassTypeName(camelAttributeName, apiName);

        MethodVisitor mVisitor = attributeWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mVisitor.visitCode();


        //TODO Ver porque é que se retirar esta criação gera o problema com o float

        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTSTATIC, attributeType, "restrictions", JAVA_LIST_DESC);

        int currIndex = 0;

        for (XsdRestriction restriction : restrictions) {
            currIndex = loadRestrictionToAttribute(attribute, mVisitor, restriction, attributeType, currIndex, apiName);
            currIndex = currIndex + 1;
        }

        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, currIndex);
        mVisitor.visitEnd();
    }

    /**
     * Loads the XsdRestriction object information to the static constructor.
     * @param mVisitor The static constructor method visitor.
     * @param restriction The current restriction to add.
     * @param attributeType The attribute type.
     * @param index The current index of the stack.
     * @return The value of the last stack index used.
     */
    private static int loadRestrictionToAttribute(XsdAttribute attribute, MethodVisitor mVisitor, XsdRestriction restriction, String attributeType, int index, String apiName) {
        mVisitor.visitTypeInsn(NEW, "java/util/HashMap");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ASTORE, index);

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
        XsdWhiteSpace whiteSpace = restriction.getWhiteSpace();
        List<XsdEnumeration> enumerations = restriction.getEnumeration();

        if (attributeHasEnum(attribute)){
            createEnum(attribute, enumerations, apiName);
        }

        if (length != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("Length");
            mVisitor.visitIntInsn(BIPUSH, length.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (maxLength != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MaxLength");
            mVisitor.visitIntInsn(BIPUSH, maxLength.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (minLength != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MinLength");
            mVisitor.visitIntInsn(BIPUSH, minLength.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (maxExclusive != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MaxExclusive");
            mVisitor.visitIntInsn(BIPUSH, maxExclusive.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (maxInclusive != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MaxInclusive");
            mVisitor.visitIntInsn(BIPUSH, maxInclusive.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (minExclusive != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MinExclusive");
            mVisitor.visitIntInsn(BIPUSH, minExclusive.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (minInclusive != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MinInclusive");
            mVisitor.visitIntInsn(BIPUSH, minInclusive.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (fractionDigits != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("FractionDigits");
            mVisitor.visitIntInsn(BIPUSH, fractionDigits.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (totalDigits != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("TotalDigits");
            mVisitor.visitIntInsn(BIPUSH, totalDigits.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
            mVisitor.visitInsn(POP);
        }

        if (pattern != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("Pattern");
            mVisitor.visitLdcInsn(pattern.getValue());
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
        }

        if (whiteSpace != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("WhiteSpace");
            mVisitor.visitLdcInsn(whiteSpace.getValue());
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(" + JAVA_OBJECT_DESC + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
        }

        mVisitor.visitFieldInsn(GETSTATIC, attributeType, "restrictions", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, index);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(" + JAVA_OBJECT_DESC + ")Z", true);
        mVisitor.visitInsn(POP);

        return index;
    }

    private static XsdList getAttributeList(XsdAttribute attribute) {
        if (attribute.getXsdSimpleType() != null){
            return attribute.getXsdSimpleType().getList();
        }

        return null;
    }
}
