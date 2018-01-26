package XsdAsm;

import org.objectweb.asm.*;

import static XsdAsm.XsdAsmUtils.*;
import static org.objectweb.asm.Opcodes.*;

class XsdSupportingStructure {

    static final String JAVA_OBJECT = "java/lang/Object";
    static final String JAVA_OBJECT_DESC = "Ljava/lang/Object;";
    private static final String JAVA_STRING = "java/lang/String";
    static final String JAVA_STRING_DESC = "Ljava/lang/String;";
    static final String JAVA_LIST = "java/util/List";
    static final String JAVA_LIST_DESC = "Ljava/util/List;";
    static final String CONSTRUCTOR = "<init>";
    static final String IELEMENT = "IElement";
    private static final String IATTRIBUTE = "IAttribute";
    private static final String ABSTRACT_ELEMENT = "AbstractElement";
    private static final String ABSTRACT_ATTRIBUTE = "AbstractAttribute";
    static final String TEXT_CLASS = "Text";
    static final String ITEXT = "ITextGroup";
    private static final String RESTRICTION_VIOLATION_EXCEPTION = "RestrictionViolationException";
    private static final String RESTRICTION_VALIDATOR = "RestrictionValidator";
    static final String VISITOR = "Visitor";
    static final String ABSTRACT_VISITOR = "AbstractVisitor";
    static final String ENUM_INTERFACE = "EnumInterface";
    static final String IBINDER = "IBinder";

    static String TEXT_TYPE;
    private static String TEXT_TYPE_DESC;
    static String ABSTRACT_ELEMENT_TYPE;
    static String ABSTRACT_ELEMENT_TYPE_DESC;
    static String ABSTRACT_ATTRIBUTE_TYPE;
    static String IELEMENT_TYPE;
    static String IELEMENT_TYPE_DESC;
    private static String IATTRIBUTE_TYPE;
    static String IATTRIBUTE_TYPE_DESC;
    static String ITEXT_TYPE;
    private static String RESTRICTION_VIOLATION_EXCEPTION_TYPE;
    static String RESTRICTION_VALIDATOR_TYPE;
    static String VISITOR_TYPE;
    static String VISITOR_TYPE_DESC;
    static String ABSTRACT_VISITOR_TYPE;
    static String ENUM_INTERFACE_TYPE;

    static final String ATTRIBUTE_PREFIX = "Attr";

    /**
     * Creates the base infrastructure, based in the main three classes:
     * IElement - An interface containing the base operations of all elements.
     * IAttribute - An interface containing the base operations of all attributes.
     * AbstractElement - An abstract class from where all the elements will derive. It implements IElement.
     * Text - A concrete attribute with a different implementation that the other generated attributes.
     */
    static void createSupportingInfrastructure(String apiName) {
        TEXT_TYPE = getFullClassTypeName(TEXT_CLASS, apiName);
        TEXT_TYPE_DESC = getFullClassTypeNameDesc(TEXT_CLASS, apiName);
        ABSTRACT_ELEMENT_TYPE = getFullClassTypeName(ABSTRACT_ELEMENT, apiName);
        ABSTRACT_ELEMENT_TYPE_DESC = getFullClassTypeNameDesc(ABSTRACT_ELEMENT, apiName);
        ABSTRACT_ATTRIBUTE_TYPE = getFullClassTypeName(ABSTRACT_ATTRIBUTE, apiName);
        IELEMENT_TYPE = getFullClassTypeName(IELEMENT, apiName);
        IELEMENT_TYPE_DESC = getFullClassTypeNameDesc(IELEMENT, apiName);
        IATTRIBUTE_TYPE = getFullClassTypeName(IATTRIBUTE, apiName);
        IATTRIBUTE_TYPE_DESC = getFullClassTypeNameDesc(IATTRIBUTE, apiName);
        ITEXT_TYPE = getFullClassTypeName(ITEXT, apiName);
        RESTRICTION_VIOLATION_EXCEPTION_TYPE = getFullClassTypeName(RESTRICTION_VIOLATION_EXCEPTION, apiName);
        RESTRICTION_VALIDATOR_TYPE = getFullClassTypeName(RESTRICTION_VALIDATOR, apiName);
        VISITOR_TYPE = getFullClassTypeName(VISITOR, apiName);
        VISITOR_TYPE_DESC = getFullClassTypeNameDesc(VISITOR, apiName);
        ABSTRACT_VISITOR_TYPE = getFullClassTypeName(ABSTRACT_VISITOR, apiName);
        ENUM_INTERFACE_TYPE = getFullClassTypeName(ENUM_INTERFACE, apiName);

        createElementInterface(apiName);
        createAttributeInterface(apiName);
        createEnumInterface(apiName);

        createTextGroupInterface(apiName);
        createTextElement(apiName);

        createAbstractElement(apiName);
        createAbstractAttribute(apiName);

        createRestrictionValidator(apiName);
        createRestrictionViolationException(apiName);
    }

    /**
     * Generates the IElement interface.
     * @param apiName The api this class will belong.
     */
    private static void createElementInterface(String apiName){
        ClassWriter classWriter = generateClass(IELEMENT, JAVA_OBJECT, null, "<T::" + IELEMENT_TYPE_DESC + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("child", IELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "addAttr", "("+ IATTRIBUTE_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("attribute", IATTRIBUTE_TYPE_DESC, null, new Label(), new Label(),0);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "self", "()" + IELEMENT_TYPE_DESC, "()TT;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "id", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "accept", "(" + VISITOR_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", VISITOR_TYPE_DESC, null, new Label(), new Label(),0);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getName", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getChildren", "()" + JAVA_LIST_DESC, "()Ljava/util/List<L" + IELEMENT_TYPE + "<TT;>;>;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getAttributes", "()" + JAVA_LIST_DESC, "()Ljava/util/List<" + IATTRIBUTE_TYPE_DESC + ">;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "$", "()" + IELEMENT_TYPE_DESC, "<P::" + IELEMENT_TYPE_DESC + ">()L" + IELEMENT_TYPE + "<TP;>;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "binder", "(Ljava/util/function/BiConsumer;)" + IELEMENT_TYPE_DESC, "<M:Ljava/lang/Object;>(Ljava/util/function/BiConsumer<TT;TM;>;)TT;", null);
        mVisitor.visitLocalVariable("binderMethod", "(Ljava/util/function/BiConsumer;)" + IELEMENT_TYPE_DESC, "<M:Ljava/lang/Object;>(Ljava/util/function/BiConsumer<TT;TM;>;)TT;", new Label(), new Label(),0);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "isBound", "()Z", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "cloneElem", "()" + IELEMENT_TYPE_DESC, "()L" + IELEMENT_TYPE + "<TT;>;", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "bindTo", "(Ljava/lang/Object;)" + IELEMENT_TYPE_DESC, "(Ljava/lang/Object;)L" + IELEMENT_TYPE + "<TT;>;", null);
        mVisitor.visitLocalVariable("model", "(Ljava/lang/Object;)" + IELEMENT_TYPE_DESC, "(Ljava/lang/Object;)L" + IELEMENT_TYPE + "<TT;>;", new Label(), new Label(),0);
        mVisitor.visitEnd();

        writeClassToFile(IELEMENT, classWriter, apiName);
    }

    /**
     * Generates the IAttribute interface.
     * @param apiName The api this class will belong.
     */
    private static void createAttributeInterface(String apiName){
        ClassWriter classWriter = generateClass(IATTRIBUTE, JAVA_OBJECT, null, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getValue", "()" + JAVA_OBJECT_DESC, "()TT;", null);
        mVisitor.visitEnd();

        writeClassToFile(IATTRIBUTE, classWriter, apiName);
    }

    /**
     * Adds a interface with a getValue value in order to extract the value from a enum element.
     * @param apiName The API this class will belong to.
     */
    private static void createEnumInterface(String apiName) {
        ClassWriter classWriter = generateClass(ENUM_INTERFACE, JAVA_OBJECT, null, "<T:Ljava/lang/Object;>Ljava/lang/Object;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getValue", "()Ljava/lang/Object;", "()TT;", null);
        mVisitor.visitEnd();

        writeClassToFile(ENUM_INTERFACE, classWriter, apiName);
    }

    /**
     * Creates the text interface, allowing elements to have a text child node.
     * @param apiName The api this class will belong.
     */
    private static void createTextGroupInterface(String apiName) {
        ClassWriter classWriter = generateClass(ITEXT, JAVA_OBJECT, new String[] { IELEMENT }, "<T::L" + IELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, TEXT_CLASS.toLowerCase(), "(" + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + ")TT;", null);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, TEXT_TYPE);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, TEXT_TYPE, CONSTRUCTOR, "(" + IELEMENT_TYPE_DESC + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, ITEXT_TYPE, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "text", "(Ljava/util/function/Function;)" + IELEMENT_TYPE_DESC, "<R:Ljava/lang/Object;U:Ljava/lang/Object;>(Ljava/util/function/Function<TR;TU;>;)TT;", null);
        mVisitor.visitLocalVariable("textFunction", "Ljava/util/function/Function;", "Ljava/util/function/Function<TR;TU;>;", new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, TEXT_TYPE);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, TEXT_TYPE, CONSTRUCTOR, "(" + IELEMENT_TYPE_DESC + "Ljava/util/function/Function;)V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, ITEXT_TYPE, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, ITEXT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        writeClassToFile(ITEXT, classWriter, apiName);
    }

    /**
     * Creates the Text class.
     * @param apiName The api this class will belong.
     */
    private static void createTextElement(String apiName) {
        ClassWriter classWriter = generateClass(TEXT_CLASS, ABSTRACT_ELEMENT_TYPE, null,  "<R:Ljava/lang/Object;U:Ljava/lang/Object;>L"  + ABSTRACT_ELEMENT_TYPE +"<" + TEXT_TYPE_DESC + ">;",ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE, "text", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PRIVATE, "textFunction", "Ljava/util/function/Function;", "Ljava/util/function/Function<TR;TU;>;", null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + IELEMENT_TYPE_DESC +  JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("parent", IELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, TEXT_TYPE, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + IELEMENT_TYPE_DESC + "Ljava/util/function/Function;)V", "(" + IELEMENT_TYPE_DESC + "Ljava/util/function/Function<TR;TU;>;)V", null);
        mVisitor.visitLocalVariable("parent", IELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("textFunction", "Ljava/util/function/Function;", "Ljava/util/function/Function<TR;TU;>;", new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, TEXT_TYPE, "textFunction", "Ljava/util/function/Function;");
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("attribute", IATTRIBUTE_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(0, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("child", IELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(0, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + TEXT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "accept", "(" + VISITOR_TYPE_DESC + ")V", "<R:Ljava/lang/Object;>(L" + VISITOR_TYPE + "<TR;>;)V", null);
        mVisitor.visitLocalVariable("visitor", VISITOR_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, VISITOR_TYPE, "initVisit", "(" + TEXT_TYPE_DESC + ")V", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, VISITOR_TYPE, "endVisit", "(" + TEXT_TYPE_DESC + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, TEXT_TYPE, "text", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "(" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, "(TR;)TU;", null);
        mVisitor.visitLocalVariable("model", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, TEXT_TYPE, "textFunction", "Ljava/util/function/Function;");
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFNONNULL, l0);
        mVisitor.visitInsn(ACONST_NULL);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, TEXT_TYPE, "textFunction", "Ljava/util/function/Function;");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Function", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, TEXT_TYPE, "self", "()" + TEXT_TYPE_DESC, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        writeClassToFile(TEXT_CLASS, classWriter, apiName);
    }

    /**
     * Generates the AbstractElement class with all the implementations.
     */
    private static void createAbstractElement(String apiName){
        ClassWriter classWriter = generateClass(ABSTRACT_ELEMENT, JAVA_OBJECT, new String[] { IELEMENT }, "<T::" + IELEMENT_TYPE_DESC + ">" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;",ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, apiName);
        FieldVisitor fVisitor;
        MethodVisitor mVisitor;

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

        fVisitor = classWriter.visitField(ACC_PROTECTED, "children", JAVA_LIST_DESC , "L" + JAVA_LIST + "<L" + IELEMENT_TYPE + "<TT;>;>;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "attrs", JAVA_LIST_DESC, "L" + JAVA_LIST + "<" + IATTRIBUTE_TYPE_DESC + ">;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "id", JAVA_STRING_DESC, null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "name", "Ljava/lang/String;", null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "parent", IELEMENT_TYPE_DESC, null, null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PROTECTED, "binderMethod", "Ljava/util/function/BiConsumer;", null, null);
        fVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "children", "Ljava/util/List;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", "Ljava/util/List;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ACONST_NULL);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "parent", IELEMENT_TYPE_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, "setName", "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("parent", IELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "children", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "parent", IELEMENT_TYPE_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, "setName", "()V", false);mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED, "<init>", "(" + IELEMENT_TYPE_DESC + "Ljava/lang/String;)V", null, null);
        mVisitor.visitLocalVariable("parent", IELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "children", "Ljava/util/List;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", "Ljava/util/List;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "parent", IELEMENT_TYPE_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "id", "Ljava/lang/String;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, "setName", "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE, "setName", "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
        mVisitor.visitVarInsn(ASTORE, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "name", "Ljava/lang/String;");
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("child", IELEMENT_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "children", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(" + JAVA_OBJECT_DESC + ")Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("attribute", IATTRIBUTE_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(" + JAVA_OBJECT_DESC + ")Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "id", "()" + JAVA_STRING_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "id", JAVA_STRING_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "name", "Ljava/lang/String;");
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "$", "()" + IELEMENT_TYPE_DESC, "<P::" + IELEMENT_TYPE_DESC + ">()TP;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "parent", IELEMENT_TYPE_DESC);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "child", "(" + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "<R::" + IELEMENT_TYPE_DESC + ">(" + JAVA_STRING_DESC + ")TR;", null);
        mVisitor.visitLocalVariable("childId", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE_DESC, "children", JAVA_LIST_DESC);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "stream", "()Ljava/util/stream/Stream;", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("test", "(" + JAVA_STRING_DESC + ")Ljava/util/function/Predicate;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(" + JAVA_OBJECT_DESC + ")Z"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$0", "(" + JAVA_STRING_DESC + IELEMENT_TYPE_DESC + ")Z", false), Type.getType("(" + IELEMENT_TYPE_DESC + ")Z"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", true);
        mVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;" + JAVA_STRING_DESC + "Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$1", "(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, false), Type.getType("(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "findFirst", "()Ljava/util/Optional;", true);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Optional", "isPresent", "()Z", false);
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l0);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Optional", "get", "()" + JAVA_OBJECT_DESC, false);
        mVisitor.visitTypeInsn(CHECKCAST, IELEMENT_TYPE);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/util/Optional"}, 0, null);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "children", "Ljava/util/List;");
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "stream", "()Ljava/util/stream/Stream;", true);
        mVisitor.visitInvokeDynamicInsn("test", "()Ljava/util/function/Predicate;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)Z"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$2", "(" + IELEMENT_TYPE_DESC + ")Z", false), Type.getType("(" + IELEMENT_TYPE_DESC + ")Z"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", true);
        mVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$3", "(" + IELEMENT_TYPE_DESC + ")" + ABSTRACT_ELEMENT_TYPE_DESC, false), Type.getType("(" + IELEMENT_TYPE_DESC + ")" + ABSTRACT_ELEMENT_TYPE_DESC));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("test", "(Ljava/lang/String;)Ljava/util/function/Predicate;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)Z"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$4", "(Ljava/lang/String;" + ABSTRACT_ELEMENT_TYPE_DESC + ")Z", false), Type.getType("(" + ABSTRACT_ELEMENT_TYPE_DESC + ")Z"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("apply", "(Ljava/lang/String;)Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, ABSTRACT_ELEMENT_TYPE, "lambda$child$5", "(Ljava/lang/String;" + ABSTRACT_ELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, false), Type.getType("(" + ABSTRACT_ELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;", true);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/stream/Stream", "findFirst", "()Ljava/util/Optional;", true);
        mVisitor.visitInsn(ACONST_NULL);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Optional", "orElse", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        mVisitor.visitTypeInsn(CHECKCAST, IELEMENT_TYPE);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$5", "(Ljava/lang/String;" + ABSTRACT_ELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "child", "(Ljava/lang/String;)" + IELEMENT_TYPE_DESC, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$4", "(Ljava/lang/String;" + ABSTRACT_ELEMENT_TYPE_DESC + ")Z", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "child", "(Ljava/lang/String;)" + IELEMENT_TYPE_DESC, false);
        Label l1 = new Label();
        mVisitor.visitJumpInsn(IFNULL, l1);
        mVisitor.visitInsn(ICONST_1);
        Label l2 = new Label();
        mVisitor.visitJumpInsn(GOTO, l2);
        mVisitor.visitLabel(l1);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitLabel(l2);
        mVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
        mVisitor.visitInsn(IRETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$3", "(" + IELEMENT_TYPE_DESC + ")" + ABSTRACT_ELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(CHECKCAST, ABSTRACT_ELEMENT_TYPE);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$2", "(" + IELEMENT_TYPE_DESC + ")Z", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(INSTANCEOF, ABSTRACT_ELEMENT_TYPE);
        mVisitor.visitInsn(IRETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$1", "(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$child$0", "(Ljava/lang/String;" + IELEMENT_TYPE_DESC + ")Z", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "id", "()Ljava/lang/String;", true);
        Label l3 = new Label();
        mVisitor.visitJumpInsn(IFNULL, l3);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "id", "()Ljava/lang/String;", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
        mVisitor.visitJumpInsn(IFEQ, l3);
        mVisitor.visitInsn(ICONST_1);
        Label l4 = new Label();
        mVisitor.visitJumpInsn(GOTO, l4);
        mVisitor.visitLabel(l3);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitLabel(l4);
        mVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
        mVisitor.visitInsn(IRETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getChildren", "()" + JAVA_LIST_DESC, "()Ljava/util/List<L" + IELEMENT_TYPE + "<TT;>;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "children", JAVA_LIST_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getAttributes", "()" + JAVA_LIST_DESC, "()Ljava/util/List<" + IATTRIBUTE_TYPE_DESC + ">;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", JAVA_LIST_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "binder", "(Ljava/util/function/BiConsumer;)" + IELEMENT_TYPE_DESC, "<M:Ljava/lang/Object;>(Ljava/util/function/BiConsumer<TT;TM;>;)TT;" , null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "binderMethod", "Ljava/util/function/BiConsumer;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "isBound", "()Z", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "binderMethod", "Ljava/util/function/BiConsumer;");
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

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "bindTo", "(Ljava/lang/Object;)" + IELEMENT_TYPE_DESC, "(Ljava/lang/Object;)L" + IELEMENT_TYPE + ">TT;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "isBound", "()Z", false);
        Label l7 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l7);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "binderMethod", "Ljava/util/function/BiConsumer;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/function/BiConsumer", "accept", "(Ljava/lang/Object;Ljava/lang/Object;)V", true);
        mVisitor.visitLabel(l7);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, new Object[] {IELEMENT_TYPE}, 0, null);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PROTECTED, "clone", "(" + ABSTRACT_ELEMENT_TYPE_DESC + ")" + ABSTRACT_ELEMENT_TYPE_DESC, "<X:" + ABSTRACT_ELEMENT_TYPE_DESC + ">(TX;)TX;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "children", "Ljava/util/List;");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "children", "Ljava/util/List;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "children", "Ljava/util/List;");
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", "Ljava/util/List;");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", "Ljava/util/List;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "attrs", "Ljava/util/List;");
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "addAll", "(Ljava/util/Collection;)Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "id", "Ljava/lang/String;");
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "id", "Ljava/lang/String;");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "name", "Ljava/lang/String;");
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "name", "Ljava/lang/String;");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "parent", IELEMENT_TYPE_DESC);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "parent", IELEMENT_TYPE_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ELEMENT_TYPE, "binderMethod", "Ljava/util/function/BiConsumer;");
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "binderMethod", "Ljava/util/function/BiConsumer;");
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        writeClassToFile(ABSTRACT_ELEMENT, classWriter, apiName);
    }

    /**
     * Creates a abstract class for all concrete attributes, containing it's value.
     * @param apiName The api this class will belong.
     */
    private static void createAbstractAttribute(String apiName) {
        ClassWriter classWriter = generateClass(ABSTRACT_ATTRIBUTE, JAVA_OBJECT, new String[] { IATTRIBUTE }, "<T:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC + "L" + IATTRIBUTE_TYPE + "<TT;>;", ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = classWriter.visitField(ACC_PRIVATE, "value", JAVA_OBJECT_DESC, "TT;", null);
        fVisitor.visitEnd();

        fVisitor = classWriter.visitField(ACC_PRIVATE, "name", "Ljava/lang/String;", null, null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = classWriter.visitMethod(0, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + ")V", "(TT;)V", null);
        mVisitor.visitLocalVariable("attributeValue", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ATTRIBUTE_TYPE, "value", "Ljava/lang/Object;");
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
        mVisitor.visitLdcInsn("Attr");
        mVisitor.visitLdcInsn("");
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false);
        mVisitor.visitInsn(ICONST_0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitInsn(ICONST_1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ATTRIBUTE_TYPE, "name", "Ljava/lang/String;");
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getValue", "()" + JAVA_OBJECT_DESC, "()TT;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ATTRIBUTE_TYPE, "value", JAVA_OBJECT_DESC);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitFieldInsn(GETFIELD, ABSTRACT_ATTRIBUTE_TYPE, "name", "Ljava/lang/String;");
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        writeClassToFile(ABSTRACT_ATTRIBUTE, classWriter, apiName);
    }

    /**
     * Creates a static class with method that validate all the XsdRestrictions.
     * @param apiName The api this class will belong.
     */
    private static void createRestrictionValidator(String apiName) {
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mVisitor;

        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, RESTRICTION_VALIDATOR_TYPE, null, "java/lang/Object", null);

        {
            mVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(1, 1);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_STATIC, "validate", "(Ljava/util/Map;Ljava/lang/Object;)V", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Ljava/lang/Object;)V", null);
            mVisitor.visitCode();
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(0, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_STATIC, "validate", "(Ljava/util/Map;Ljava/util/List;)V", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Ljava/util/List;)V", null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("Length");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateLength", "(ILjava/util/List;)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("MinLength");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateMinLength", "(ILjava/util/List;)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("MaxLength");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateMaxLength", "(ILjava/util/List;)V", false);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(3, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_STATIC, "validate", "(Ljava/util/Map;Ljava/lang/Double;)V", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Ljava/lang/Double;)V", null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("MaxExclusive");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateMaxExclusive", "(ID)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("MaxInclusive");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateMaxInclusive", "(ID)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("MinExclusive");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateMinExclusive", "(ID)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("MinInclusive");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateMinInclusive", "(ID)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("FractionDigits");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateFractionDigits", "(ID)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("TotalDigits");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateTotalDigits", "(ID)V", false);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(3, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_STATIC, "validate", "(Ljava/util/Map;Ljava/lang/String;)V", "(Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;Ljava/lang/String;)V", null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("Enumeration");
            mVisitor.visitInsn(ACONST_NULL);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/util/List");
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateEnumeration", "(Ljava/util/List;Ljava/lang/String;)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("Length");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateLength", "(ILjava/lang/String;)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("MinLength");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateMinLength", "(ILjava/lang/String;)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("MaxLength");
            mVisitor.visitInsn(ICONST_M1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateMaxLength", "(ILjava/lang/String;)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("Pattern");
            mVisitor.visitInsn(ACONST_NULL);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validatePattern", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("WhiteSpace");
            mVisitor.visitInsn(ACONST_NULL);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "getOrDefault", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validateWhiteSpace", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(3, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateEnumeration", "(Ljava/util/List;Ljava/lang/String;)V", "(Ljava/util/List<Ljava/lang/String;>;Ljava/lang/String;)V", null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IFNONNULL, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "contains", "(Ljava/lang/Object;)Z", true);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IFNE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitLdcInsn("Violation of enumeration restriction, value not acceptable for the current type.");
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(3, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateFractionDigits", "(ID)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitInsn(D2I);
            mVisitor.visitInsn(I2D);
            mVisitor.visitInsn(DCMPL);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IFEQ, l1);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
            mVisitor.visitVarInsn(ASTORE, 3);
            mVisitor.visitVarInsn(ALOAD, 3);
            mVisitor.visitVarInsn(ALOAD, 3);
            mVisitor.visitLdcInsn(",");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "indexOf", "(Ljava/lang/String;)I", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(I)Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
            mVisitor.visitVarInsn(ISTORE, 4);
            mVisitor.visitVarInsn(ILOAD, 4);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitJumpInsn(IF_ICMPLE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of fractionDigits restriction, value should have a maximum of ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitLdcInsn(" decimal places.");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 5);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateLength", "(ILjava/lang/String;)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPEQ, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of length restriction, string should have exactly ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitLdcInsn(" characters.");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateLength", "(ILjava/util/List;)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
            mVisitor.visitVarInsn(ILOAD, 0);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPEQ, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of length restriction, list should have exactly ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitLdcInsn(" elements.");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMaxExclusive", "(ID)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(I2D);
            mVisitor.visitInsn(DCMPL);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IFLT, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of maxExclusive restriction, value should be lesser than ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 3);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMaxInclusive", "(ID)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(I2D);
            mVisitor.visitInsn(DCMPL);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IFLE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of maxInclusive restriction, value should be lesser or equal to ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 3);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMaxLength", "(ILjava/lang/String;)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPLE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of maxLength restriction, string should have a max number of characters of ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMaxLength", "(ILjava/util/List;)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
            mVisitor.visitVarInsn(ILOAD, 0);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPLE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of maxLength restriction, list should have a max number of items of ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMinExclusive", "(ID)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(I2D);
            mVisitor.visitInsn(DCMPG);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IFGT, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of minExclusive restriction, value should be greater than ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 3);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMinInclusive", "(ID)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(I2D);
            mVisitor.visitInsn(DCMPG);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IFGE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of minInclusive restriction, value should be greater or equal to ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 3);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMinLength", "(ILjava/lang/String;)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPGE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of minLength restriction, string should have a minimum number of characters of ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateMinLength", "(ILjava/util/List;)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I", true);
            mVisitor.visitVarInsn(ILOAD, 0);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPGE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of minLength restriction, list should have a minimum number of items of ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validatePattern", "(Ljava/lang/String;Ljava/lang/String;)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IFNONNULL, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitLdcInsn("");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replaceAll", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false);
            mVisitor.visitVarInsn(ALOAD, 1);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IFNE, l1);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of pattern restriction, the string doesn't math the acceptable pattern, which is ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ALOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 2);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateTotalDigits", "(ID)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitInsn(ICONST_M1);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPNE, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
            mVisitor.visitVarInsn(ASTORE, 3);
            mVisitor.visitInsn(ICONST_0);
            mVisitor.visitVarInsn(ISTORE, 4);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitVarInsn(DLOAD, 1);
            mVisitor.visitInsn(D2I);
            mVisitor.visitInsn(I2D);
            mVisitor.visitInsn(DCMPL);
            Label l1 = new Label();
            mVisitor.visitJumpInsn(IFEQ, l1);
            mVisitor.visitVarInsn(ALOAD, 3);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
            mVisitor.visitInsn(ICONST_1);
            mVisitor.visitInsn(ISUB);
            mVisitor.visitVarInsn(ISTORE, 4);
            Label l2 = new Label();
            mVisitor.visitJumpInsn(GOTO, l2);
            mVisitor.visitLabel(l1);
            mVisitor.visitFrame(Opcodes.F_APPEND,2, new Object[] {"java/lang/String", Opcodes.INTEGER}, 0, null);
            mVisitor.visitVarInsn(ALOAD, 3);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
            mVisitor.visitVarInsn(ISTORE, 4);
            mVisitor.visitLabel(l2);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitVarInsn(ILOAD, 4);
            mVisitor.visitVarInsn(ILOAD, 0);
            Label l3 = new Label();
            mVisitor.visitJumpInsn(IF_ICMPEQ, l3);
            mVisitor.visitTypeInsn(NEW, RESTRICTION_VIOLATION_EXCEPTION_TYPE);
            mVisitor.visitInsn(DUP);
            mVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mVisitor.visitLdcInsn("Violation of fractionDigits restriction, value should have a exactly ");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitVarInsn(ILOAD, 0);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mVisitor.visitLdcInsn(" decimal places.");
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mVisitor.visitMethodInsn(INVOKESPECIAL, RESTRICTION_VIOLATION_EXCEPTION_TYPE, "<init>", "(Ljava/lang/String;)V", false);
            mVisitor.visitInsn(ATHROW);
            mVisitor.visitLabel(l3);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(4, 5);
            mVisitor.visitEnd();
        }
        {
            mVisitor = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "validateWhiteSpace", "(Ljava/lang/String;Ljava/lang/String;)V", null, null);
            mVisitor.visitCode();
            mVisitor.visitVarInsn(ALOAD, 0);
            Label l0 = new Label();
            mVisitor.visitJumpInsn(IFNONNULL, l0);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitLabel(l0);
            mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mVisitor.visitInsn(RETURN);
            mVisitor.visitMaxs(1, 2);
            mVisitor.visitEnd();
        }

        writeClassToFile(RESTRICTION_VALIDATOR, cw, apiName);
    }

    /**
     * Creates the exception class that will be thrown if any restriction is violated.
     * @param apiName The api this class will belong.
     */
    private static void createRestrictionViolationException(String apiName) {
        ClassWriter classWriter = generateClass(RESTRICTION_VIOLATION_EXCEPTION, "java/lang/RuntimeException", null, null, ACC_PUBLIC + ACC_SUPER, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        writeClassToFile(RESTRICTION_VIOLATION_EXCEPTION, classWriter, apiName);
    }
}
