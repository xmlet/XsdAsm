package org.xmlet.xsdasm.classes;

import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;

class XsdSupportingStructure {

    static final String JAVA_OBJECT = "java/lang/Object";
    static final String JAVA_OBJECT_DESC = "Ljava/lang/Object;";
    private static final String JAVA_STRING = "java/lang/String";
    static final String JAVA_STRING_DESC = "Ljava/lang/String;";
    static final String JAVA_LIST = "java/util/List";
    static final String JAVA_LIST_DESC = "Ljava/util/List;";
    static final String CONSTRUCTOR = "<init>";
    static final String STATIC_CONSTRUCTOR = "<clinit>";
    static final String ELEMENT = "Element";
    private static final String ATTRIBUTE = "Attribute";
    private static final String ABSTRACT_ELEMENT = "AbstractElement";
    private static final String BASE_ATTRIBUTE = "BaseAttribute";
    private static final String TEXT_CLASS = "Text";
    private static final String COMMENT_CLASS = "Comment";
    private static final String TEXT_FUNCTION_CLASS = "TextFunction";
    static final String TEXT_GROUP = "TextGroup";
    private static final String RESTRICTION_VIOLATION_EXCEPTION = "RestrictionViolationException";
    private static final String RESTRICTION_VALIDATOR = "RestrictionValidator";
    static final String VISITOR = "ElementVisitor";
    static final String ENUM_INTERFACE = "EnumInterface";
    static final String ATTRIBUTE_PREFIX = "Attr";

    private static String textType;
    static String textTypeDesc;
    private static String commentType;
    static String commentTypeDesc;
    static String textFunctionType;
    static String textFunctionTypeDesc;
    static String abstractElementType;
    static String abstractElementTypeDesc;
    static String baseAttributeType;
    static String elementType;
    static String elementTypeDesc;
    private static String attributeType;
    static String attributeTypeDesc;
    private static String textGroupType;
    static String restrictionValidatorType = "org/xmlet/xsdasm/classes/infrastructure/RestrictionValidator";
    static String elementVisitorType;
    static String elementVisitorTypeDesc;
    static String enumInterfaceType = "org/xmlet/xsdasm/classes/infrastructure/EnumInterface";

    static Map<String, String> infrastructureVars;

    static {
        infrastructureVars = new HashMap<>();

        infrastructureVars.put(RESTRICTION_VALIDATOR, restrictionValidatorType);
        infrastructureVars.put(RESTRICTION_VIOLATION_EXCEPTION, "org/xmlet/xsdasm/classes/infrastructure/RestrictionViolationException");
        infrastructureVars.put(ENUM_INTERFACE, enumInterfaceType);
    }

    private XsdSupportingStructure(){}

    /**
     * Creates the base infrastructure, based in the main three classes:
     * IElement - An interface containing the base operations of all elements.
     * IAttribute - An interface containing the base operations of all attributes.
     * AbstractElement - An abstract class from where all the elements will derive. It implements IElement.
     * Text - A concrete attribute with a different implementation that the other generated attributes.
     */
    static void createSupportingInfrastructure(String apiName) {
        textType = getFullClassTypeName(TEXT_CLASS, apiName);
        textTypeDesc = getFullClassTypeNameDesc(TEXT_CLASS, apiName);
        commentType = getFullClassTypeName(COMMENT_CLASS, apiName);
        commentTypeDesc = getFullClassTypeNameDesc(COMMENT_CLASS, apiName);
        textFunctionType = getFullClassTypeName(TEXT_FUNCTION_CLASS, apiName);
        textFunctionTypeDesc = getFullClassTypeNameDesc(TEXT_FUNCTION_CLASS, apiName);
        abstractElementType = getFullClassTypeName(ABSTRACT_ELEMENT, apiName);
        abstractElementTypeDesc = getFullClassTypeNameDesc(ABSTRACT_ELEMENT, apiName);
        baseAttributeType = getFullClassTypeName(BASE_ATTRIBUTE, apiName);
        elementType = getFullClassTypeName(ELEMENT, apiName);
        elementTypeDesc = getFullClassTypeNameDesc(ELEMENT, apiName);
        attributeType = getFullClassTypeName(ATTRIBUTE, apiName);
        attributeTypeDesc = getFullClassTypeNameDesc(ATTRIBUTE, apiName);
        textGroupType = getFullClassTypeName(TEXT_GROUP, apiName);
        elementVisitorType = getFullClassTypeName(VISITOR, apiName);
        elementVisitorTypeDesc = getFullClassTypeNameDesc(VISITOR, apiName);

        createElementInterface(apiName);
        createAttributeInterface(apiName);

        createTextGroupInterface(apiName);
        createTextElement(apiName);
        createCommentElement(apiName);
        createTextFunctionElement(apiName);

        createAbstractElement(apiName);
        createAttributeBase(apiName);
    }

    /**
     * Generates the Element interface.
     * @param apiName The api this class will belong.
     */
    private static void createElementInterface(String apiName){
        ClassWriter classWriter = generateClass(ELEMENT, JAVA_OBJECT, null, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, "<R::" + elementTypeDesc + ">(TR;)TR;", null);
        mVisitor.visitLocalVariable("child", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "addAttr", "("+ attributeTypeDesc + ")" + elementTypeDesc, "(" + attributeTypeDesc + ")TT;", null);
        mVisitor.visitLocalVariable("attribute", attributeTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "self", "()" + elementTypeDesc, "()TT;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "accept", "(" + elementVisitorTypeDesc + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", elementVisitorTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getChildren", "()" + JAVA_LIST_DESC, "()L" + JAVA_LIST + "<" + elementTypeDesc + ">;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getAttributes", "()" + JAVA_LIST_DESC, "()L" + JAVA_LIST + "<" + attributeTypeDesc + ">;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "find", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", "<R::" + elementTypeDesc + ">(Ljava/util/function/Predicate<" + elementTypeDesc + ">;)Ljava/util/stream/Stream<TR;>;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "º", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "binder", "(Ljava/util/function/BiConsumer;)" + elementTypeDesc, "<M:" + JAVA_OBJECT_DESC + ">(Ljava/util/function/BiConsumer<TT;TM;>;)TT;", null);
        mVisitor.visitLocalVariable("binderMethod", "(Ljava/util/function/BiConsumer;)" + elementTypeDesc, "<M:" + JAVA_OBJECT_DESC + ">(Ljava/util/function/BiConsumer<TT;TM;>;)TT;", new Label(), new Label(),1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "isBound", "()Z", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "cloneElem", "()" + elementTypeDesc, "()L" + elementType + "<TT;TZ;>;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "bindTo", "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, "(" + JAVA_OBJECT_DESC + ")L" + elementType + "<TT;TZ;>;", null);
        mVisitor.visitLocalVariable("model", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitEnd();

        writeClassToFile(ELEMENT, classWriter, apiName);
    }

    /**
     * Generates the IAttribute interface.
     * @param apiName The api this class will belong.
     */
    private static void createAttributeInterface(String apiName){
        ClassWriter classWriter = generateClass(ATTRIBUTE, JAVA_OBJECT, null, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getValue", "()" + JAVA_OBJECT_DESC, "()TT;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();

        writeClassToFile(ATTRIBUTE, classWriter, apiName);
    }

    /**
     * Creates the text interface, allowing elements to have a text child node.
     * @param apiName The api this class will belong.
     */
    private static void createTextGroupInterface(String apiName) {
        ClassWriter classWriter = generateClass(TEXT_GROUP, JAVA_OBJECT, new String[] {ELEMENT}, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<TT;TZ;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, TEXT_CLASS.toLowerCase(), "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, "<R:" + JAVA_OBJECT_DESC + ">(TR;)TT;", null);
        mVisitor.visitLocalVariable("text", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, textType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESTATIC, JAVA_STRING, "valueOf", "(" + JAVA_OBJECT_DESC + ")" + JAVA_STRING_DESC, false);
        mVisitor.visitMethodInsn(INVOKESPECIAL, textType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(5, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, COMMENT_CLASS.toLowerCase(), "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, "<R:" + JAVA_OBJECT_DESC + ">(TR;)TT;", null);
        mVisitor.visitLocalVariable("text", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, commentType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESTATIC, JAVA_STRING, "valueOf", "(" + JAVA_OBJECT_DESC + ")" + JAVA_STRING_DESC, false);
        mVisitor.visitMethodInsn(INVOKESPECIAL, commentType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(5, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "text", "(Ljava/util/function/Function;)" + elementTypeDesc, "<R:" + JAVA_OBJECT_DESC + "U:" + JAVA_OBJECT_DESC + ">(Ljava/util/function/Function<TR;TU;>;)TT;", null);
        mVisitor.visitLocalVariable("textFunction", "Ljava/util/function/Function;", "Ljava/util/function/Function<TR;TU;>;", new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, textFunctionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, textFunctionType, CONSTRUCTOR, "(" + elementTypeDesc + "Ljava/util/function/Function;)V", false);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, textGroupType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(5, 2);
        mVisitor.visitEnd();

        writeClassToFile(TEXT_GROUP, classWriter, apiName);
    }

    /**
     * Creates the Text class.
     * @param apiName The api this class will belong.
     */
    private static void createTextElement(String apiName) {
        createTextClass(TEXT_CLASS, textType, textTypeDesc, apiName);
    }

    private static void createCommentElement(String apiName) {
        createTextClass(COMMENT_CLASS, commentType, commentTypeDesc, apiName);
    }

    private static void createTextClass(String className, String classType, String classTypeDesc, String apiName){
        ClassWriter classWriter = generateClass(className, abstractElementType, null,  "<Z::" + elementTypeDesc + ">L"  + abstractElementType +"<L" + classType + "<TZ;>;TZ;>;",ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE, "text", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("text");
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc +  JAVA_STRING_DESC + ")V", "(TZ;" + JAVA_STRING_DESC + ")V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitLdcInsn("text");
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, classType, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "addAttr", "(" + attributeTypeDesc + ")" + elementTypeDesc, "(" + attributeTypeDesc + ")" + elementTypeDesc, null);
        mVisitor.visitLocalVariable("attribute", attributeTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addAttr", "(" + attributeTypeDesc + ")" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addAttr", "(" + attributeTypeDesc + ")" + classTypeDesc, "(" + attributeTypeDesc + ")L" + classType + "<TZ;>;", null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mVisitor.visitInsn(DUP);
        mVisitor.visitLdcInsn("Text element can't contain attributes.");
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mVisitor.visitInsn(DUP);
        mVisitor.visitLdcInsn("Text element can't contain children.");
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + classTypeDesc, "()L" + classType + "<TZ;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "accept", "(" + elementVisitorTypeDesc + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", elementVisitorTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementVisitorType, "visit", "(" + classTypeDesc + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, classType, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "self", "()" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "cloneElem", "()" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "cloneElem", "()" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "cloneElem", "()" + classTypeDesc, "()L" + classType + "<TZ;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, classType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, classType, CONSTRUCTOR, "()V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "clone", "(" + abstractElementTypeDesc + ")" + abstractElementTypeDesc, false);
        mVisitor.visitTypeInsn(CHECKCAST, classType);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 1);
        mVisitor.visitEnd();

        writeClassToFile(className, classWriter, apiName);
    }

    private static void createTextFunctionElement(String apiName) {
        ClassWriter classWriter = generateClass(TEXT_FUNCTION_CLASS, abstractElementType, null,  "<R:" + JAVA_OBJECT_DESC + "U:" + JAVA_OBJECT_DESC + "Z::" + elementTypeDesc + ">L" + abstractElementType + "<L" + textFunctionType + "<TR;TU;TZ;>;TZ;>;",ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE, "textFunction", "Ljava/util/function/Function;", "Ljava/util/function/Function<TR;TU;>;", null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PRIVATE, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn("text");
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc + "Ljava/util/function/Function;)V", "(TZ;Ljava/util/function/Function<TR;TU;>;)V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("textFunction", "Ljava/util/function/Function;", "Ljava/util/function/Function<TR;TU;>;", new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitLdcInsn("text");
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, textFunctionType, "textFunction", "Ljava/util/function/Function;");
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + textFunctionTypeDesc, "()L" + textFunctionType + "<TR;TU;TZ;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "accept", "(" + elementVisitorTypeDesc + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", elementVisitorTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementVisitorType, "visit", "(" + textFunctionTypeDesc + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "(" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, "(TR;)TU;", null);
        mVisitor.visitLocalVariable("model", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, textFunctionType, "textFunction", "Ljava/util/function/Function;");
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFNONNULL, l0);
        mVisitor.visitInsn(ACONST_NULL);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, textFunctionType, "textFunction", "Ljava/util/function/Function;");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC + "", true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, textFunctionType, "self", "()" + textFunctionTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "cloneElem", "()" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, textFunctionType, "cloneElem", "()" + textFunctionTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "cloneElem", "()" + textFunctionTypeDesc, "()L" + textFunctionType + "<TR;TU;TZ;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, textFunctionType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, textFunctionType, CONSTRUCTOR, "()V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, textFunctionType, "clone", "(" + abstractElementTypeDesc + ")" + abstractElementTypeDesc, false);
        mVisitor.visitTypeInsn(CHECKCAST, textFunctionType);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addAttr", "(" + attributeTypeDesc + ")" + textFunctionTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mVisitor.visitInsn(DUP);
        mVisitor.visitLdcInsn("Text element can't contain attributes.");
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "addAttr", "(" + attributeTypeDesc + ")" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, textFunctionType, "addAttr", "(" + attributeTypeDesc + ")" + textFunctionTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addChild", "(" + elementTypeDesc + ")" + textFunctionTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mVisitor.visitInsn(DUP);
        mVisitor.visitLdcInsn("Text element can't contain children.");
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(ATHROW);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, textFunctionType, "addChild", "(" + elementTypeDesc + ")" + textFunctionTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        writeClassToFile(TEXT_FUNCTION_CLASS, classWriter, apiName);
    }

    /**
     * Generates the AbstractElement class with all the implementations.
     */
    private static void createAbstractElement(String apiName){
        ClassWriter classWriter = generateClass(ABSTRACT_ELEMENT, JAVA_OBJECT, new String[] {ELEMENT}, "<T::" + elementTypeDesc + "Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC + "L" + elementType + "<TT;TZ;>;",ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, apiName);
        FieldVisitor fVisitor;
        MethodVisitor mVisitor;

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        fVisitor = classWriter.visitField(ACC_PROTECTED, "children", JAVA_LIST_DESC , "L" + JAVA_LIST + "<" + elementTypeDesc + ">;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "attrs", JAVA_LIST_DESC, "L" + JAVA_LIST + "<" + attributeTypeDesc + ">;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "name", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "parent", elementTypeDesc, "TZ;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "binderMethod", "Ljava/util/function/BiConsumer;", null, null);
        fVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        abstractElementConstructorBase(mVisitor);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED, CONSTRUCTOR, "(" + elementTypeDesc + "" + JAVA_STRING_DESC + ")V", "(TZ;" + JAVA_STRING_DESC + ")V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        abstractElementConstructorBase(mVisitor);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, "<R::" + elementTypeDesc + ">(TR;)TR;", null);
        mVisitor.visitLocalVariable("child", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "children", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(" + JAVA_OBJECT_DESC + ")Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addAttr", "(" + attributeTypeDesc + ")" + elementTypeDesc, "(" + attributeTypeDesc + ")TT;", null);
        mVisitor.visitLocalVariable("attribute", attributeTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "attrs", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(" + JAVA_OBJECT_DESC + ")Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, abstractElementType, "self", "()" + elementTypeDesc , false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "º", "()" + elementTypeDesc, "()TZ;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "find", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", "<R::" + elementTypeDesc + ">(Ljava/util/function/Predicate<" + elementTypeDesc + ">;)Ljava/util/stream/Stream<TR;>;", null);
        mVisitor.visitLocalVariable("predicate", "Ljava/util/function/Predicate;", "Ljava/util/function/Predicate<" + elementTypeDesc + ">;", new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("get", "(" + abstractElementTypeDesc + "Ljava/util/function/Predicate;)Ljava/util/function/Supplier;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("()" + JAVA_OBJECT_DESC + ""), new Handle(Opcodes.H_INVOKESPECIAL, abstractElementType, "lambda$find$1", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", false), Type.getType("()Ljava/util/stream/Stream;"));
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitLocalVariable("streamSupplier", "Ljava/util/function/Supplier;", null, new Label(), new Label(),2);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Supplier", "get", "()" + JAVA_OBJECT_DESC + "", true);
        mVisitor.visitTypeInsn(CHECKCAST, "java/util/stream/Stream");
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "count", "()J", true);
        mVisitor.visitInsn(LCONST_0);
        mVisitor.visitInsn(LCMP);
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Supplier", "get", "()" + JAVA_OBJECT_DESC + "", true);
        mVisitor.visitTypeInsn(CHECKCAST, "java/util/stream/Stream");
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/util/function/Supplier"}, 0, null);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitTypeInsn(ANEWARRAY, "java/util/stream/Stream");
        mVisitor.visitInsn(DUP);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitMethodInsn(INVOKESTATIC, "java/util/stream/Stream", "empty", "()Ljava/util/stream/Stream;", true);
        mVisitor.visitInsn(AASTORE);
        mVisitor.visitVarInsn(ASTORE, 3);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "children", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("accept", "([Ljava/util/stream/Stream;Ljava/util/function/Predicate;)Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(" + JAVA_OBJECT_DESC + ")V"), new Handle(Opcodes.H_INVOKESTATIC, abstractElementType, "lambda$find$2", "([Ljava/util/stream/Stream;Ljava/util/function/Predicate;" + elementTypeDesc + ")V", false), Type.getType("(" + elementTypeDesc + ")V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitVarInsn(ALOAD, 3);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitInsn(AALOAD);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 4);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$find$2", "([Ljava/util/stream/Stream;Ljava/util/function/Predicate;" + elementTypeDesc + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitInsn(AALOAD);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(CHECKCAST, elementType);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "find", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", true);
        mVisitor.visitMethodInsn(INVOKESTATIC, "java/util/stream/Stream", "concat", "(Ljava/util/stream/Stream;Ljava/util/stream/Stream;)Ljava/util/stream/Stream;", true);
        mVisitor.visitInsn(AASTORE);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(5, 3);
        mVisitor.visitEnd();
    
        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_SYNTHETIC, "lambda$find$1", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "children", JAVA_LIST_DESC);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "stream", "()Ljava/util/stream/Stream;", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", true);
        mVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC + ""), new Handle(Opcodes.H_INVOKESTATIC, abstractElementType, "lambda$null$0", "(" + elementTypeDesc + ")" + elementTypeDesc, false), Type.getType("(" + elementTypeDesc + ")" + elementTypeDesc));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    
        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$null$0", "(" + elementTypeDesc + ")" + elementTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "getChildren", "()" + JAVA_LIST_DESC, "()L" + JAVA_LIST + "<" + elementTypeDesc + ">;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "children", JAVA_LIST_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "getAttributes", "()" + JAVA_LIST_DESC, "()L" + JAVA_LIST + "<" + attributeTypeDesc + ">;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "attrs", JAVA_LIST_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "binder", "(Ljava/util/function/BiConsumer;)" + elementTypeDesc, "<M:" + JAVA_OBJECT_DESC + ">(Ljava/util/function/BiConsumer<TT;TM;>;)TT;" , null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "binderMethod", "Ljava/util/function/BiConsumer;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, abstractElementType, "self", "()" + elementTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "isBound", "()Z", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "binderMethod", "Ljava/util/function/BiConsumer;");
        Label l5 = new Label();
        mVisitor.visitJumpInsn(IFNULL, l5);
        mVisitor.visitInsn(ICONST_1);
        Label l6 = new Label();
        mVisitor.visitJumpInsn(GOTO, l6);
        mVisitor.visitLabel(l5);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitLabel(l6);
        mVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
        mVisitor.visitInsn(IRETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "bindTo", "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, "(" + JAVA_OBJECT_DESC + ")L" + elementType + "<TT;TZ;>;", null);
        mVisitor.visitLocalVariable("model", JAVA_OBJECT_DESC , null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, abstractElementType, "isBound", "()Z", false);
        Label l7 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l7);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "binderMethod", "Ljava/util/function/BiConsumer;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, abstractElementType, "self", "()" + elementTypeDesc, false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/BiConsumer", "accept", "(" + JAVA_OBJECT_DESC + "" + JAVA_OBJECT_DESC + ")V", true);
        mVisitor.visitLabel(l7);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, new Object[] {elementType}, 0, null);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, abstractElementType, "self", "()" + elementTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL, "of", "(Ljava/util/function/Consumer;)" + elementTypeDesc, "(Ljava/util/function/Consumer<TT;>;)TT;", null);
        mVisitor.visitLocalVariable("consumer", "Ljava/util/function/Consumer;" , "Ljava/util/function/Consumer<TT;>;", new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, abstractElementType, "self", "()" + elementTypeDesc, false);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(Ljava/lang/Object;)V", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, abstractElementType, "self", "()" + elementTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED + ACC_FINAL, "clone", "(" + abstractElementTypeDesc + ")" + abstractElementTypeDesc, "<X:L" + abstractElementType + "<TT;TZ;>;>(TX;)TX;", null);
        mVisitor.visitLocalVariable("clone", abstractElementTypeDesc , "L" + abstractElementType + "<TT;TZ;>;", new Label(), new Label(),1);
        mVisitor.visitCode();
        cloneList(mVisitor,"children");
        cloneList(mVisitor,"attrs");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "name", JAVA_STRING_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "parent", elementTypeDesc);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, "binderMethod", "Ljava/util/function/BiConsumer;");
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "binderMethod", "Ljava/util/function/BiConsumer;");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        writeClassToFile(ABSTRACT_ELEMENT, classWriter, apiName);
    }

    private static void cloneList(MethodVisitor mVisitor, String listName) {
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, abstractElementType, listName, JAVA_LIST_DESC);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "(Ljava/util/Collection;)V", false);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, listName, JAVA_LIST_DESC);
    }

    private static void abstractElementConstructorBase(MethodVisitor mVisitor){
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "children", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, abstractElementType, "attrs", JAVA_LIST_DESC);
    }

    /**
     * Creates a abstract class for all concrete attributes, containing it's value.
     * @param apiName The api this class will belong.
     */
    private static void createAttributeBase(String apiName) {
        ClassWriter classWriter = generateClass(BASE_ATTRIBUTE, JAVA_OBJECT, new String[] {ATTRIBUTE}, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC + "L" + attributeType + "<TT;>;", ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE, "value", JAVA_OBJECT_DESC, "TT;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PRIVATE, "name", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + JAVA_STRING_DESC + ")V", "(TT;" + JAVA_STRING_DESC + ")V", null);
        mVisitor.visitLocalVariable("value", JAVA_OBJECT_DESC , "TT;", new Label(), new Label(),1);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC , null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, baseAttributeType, "value", JAVA_OBJECT_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, baseAttributeType, "name", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "()" + JAVA_OBJECT_DESC, "()TT;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, baseAttributeType, "value", JAVA_OBJECT_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, baseAttributeType, "name", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        writeClassToFile(BASE_ATTRIBUTE, classWriter, apiName);
    }

}
