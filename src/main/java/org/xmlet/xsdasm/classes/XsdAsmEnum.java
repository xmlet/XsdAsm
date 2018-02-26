package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdRestriction;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdEnumeration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

class XsdAsmEnum {

    @SuppressWarnings("FieldCanBeLocal")
    private static String ENUM_PREFIX = "Enum";

    static void createEnum(XsdAttribute attribute, List<XsdEnumeration> enumerations, String apiName){
        String enumName = getEnumName(attribute);
        String enumType = getFullClassTypeName(enumName, apiName);
        String enumTypeDesc = getFullClassTypeNameDesc(enumName, apiName);

        String fullJavaTypeDesc = getFullJavaType(attribute);
        String fullJavaType = fullJavaTypeDesc.substring(1, fullJavaTypeDesc.length() - 1);

        ClassWriter cw = generateClass(enumName, "java/lang/Enum", new String[]{ENUM_INTERFACE}, "Ljava/lang/Enum<" + enumTypeDesc + ">;L" + ENUM_INTERFACE_TYPE + "<" + fullJavaTypeDesc +">;", ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM, apiName);
        FieldVisitor fVisitor;

        enumerations.forEach(enumElem -> {
            FieldVisitor fieldVisitor = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM, getEnumElementName(enumElem), enumTypeDesc, null, null);
            fieldVisitor.visitEnd();
        });

        fVisitor = cw.visitField(ACC_PRIVATE + ACC_FINAL, "value", fullJavaTypeDesc, null, null);
        fVisitor.visitEnd();

        fVisitor = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC, "$VALUES", "[" + enumTypeDesc, null, null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "values", "()[" + enumTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitFieldInsn(GETSTATIC, enumType, "$VALUES", "[" + enumTypeDesc);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "[" + enumTypeDesc, "clone", "()Ljava/lang/Object;", false);
        mVisitor.visitTypeInsn(CHECKCAST, "[" + enumTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 0);
        mVisitor.visitEnd();

        mVisitor = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "valueOf", "(Ljava/lang/String;)" + enumTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitLdcInsn(Type.getType(enumTypeDesc));
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
        mVisitor.visitTypeInsn(CHECKCAST, enumType);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = cw.visitMethod(ACC_PRIVATE, CONSTRUCTOR, "(Ljava/lang/String;I" + fullJavaTypeDesc + ")V", "(" + fullJavaTypeDesc + ")V", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ILOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", CONSTRUCTOR, "(Ljava/lang/String;I)V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitFieldInsn(PUTFIELD, enumType, "value", fullJavaTypeDesc);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 4);
        mVisitor.visitEnd();

        mVisitor = cw.visitMethod(ACC_PUBLIC, "getValue", "()" + fullJavaTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, enumType, "value", fullJavaTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "getValue", "()Ljava/lang/Object;", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, enumType, "getValue", "()" + fullJavaTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        MethodVisitor staticConstructor = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        staticConstructor.visitCode();

        int iConst = 0;

        for (XsdEnumeration enumElem : enumerations) {
            String elemName = getEnumElementName(enumElem);
            staticConstructor.visitTypeInsn(NEW, enumType);
            staticConstructor.visitInsn(DUP);
            staticConstructor.visitLdcInsn(elemName);
            staticConstructor.visitIntInsn(BIPUSH, iConst);

            Object object = null;

            try {
                object = Class.forName(fullJavaType.replaceAll("/", ".")).getConstructor(String.class).newInstance(enumElem.getValue());
            } catch (ClassNotFoundException e){
                System.err.println("The type " + fullJavaType + " isn't supported as an enum.");
                System.exit(-1);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            staticConstructor.visitLdcInsn(object);
            staticConstructor.visitMethodInsn(INVOKESTATIC, fullJavaType, "valueOf", "(" + JAVA_OBJECT_DESC + ")" + fullJavaTypeDesc, false);
            staticConstructor.visitMethodInsn(INVOKESPECIAL, enumType, CONSTRUCTOR, "(Ljava/lang/String;I" + fullJavaTypeDesc + ")V", false);
            staticConstructor.visitFieldInsn(PUTSTATIC, enumType, elemName, enumTypeDesc);
            iConst += 1;
        }

        staticConstructor.visitIntInsn(BIPUSH, enumerations.size());
        staticConstructor.visitTypeInsn(ANEWARRAY, enumType);

        iConst = 0;

        for (XsdEnumeration enumElem : enumerations){
            staticConstructor.visitInsn(DUP);
            staticConstructor.visitIntInsn(BIPUSH, iConst);
            staticConstructor.visitFieldInsn(GETSTATIC, enumType, getEnumElementName(enumElem), enumTypeDesc);
            staticConstructor.visitInsn(AASTORE);
            iConst += 1;
        }

        staticConstructor.visitFieldInsn(PUTSTATIC, enumType, "$VALUES", "[" + enumTypeDesc);
        staticConstructor.visitInsn(RETURN);
        staticConstructor.visitMaxs(6, 0);
        staticConstructor.visitEnd();

        writeClassToFile(enumName, cw, apiName);
    }

    static boolean attributeHasEnum(XsdAttribute attribute) {
        List<XsdRestriction> restrictions = getAttributeRestrictions(attribute);

        return restrictions != null && restrictions.size() == 1 && restrictions.get(0).getEnumeration() != null && !restrictions.get(0).getEnumeration().isEmpty();
    }

    static String getEnumName(XsdAttribute attribute) {
        return ENUM_PREFIX + attribute.getName();
    }

}
