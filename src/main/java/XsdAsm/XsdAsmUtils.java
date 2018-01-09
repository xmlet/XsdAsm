package XsdAsm;

import XsdElements.XsdAttribute;
import XsdElements.XsdElement;
import XsdElements.XsdRestriction;
import XsdElements.XsdRestrictionElements.*;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static XsdAsm.XsdAsm.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class XsdAsmUtils {

    public static String PACKAGE_BASE = "XsdToJavaAPI/";
    private static final String INTERFACE_PREFIX = "I";
    private static final HashMap<String, String> xsdTypesToJava;
    private static final HashMap<String, String> xsdFullTypesToJava;

    static {
        xsdTypesToJava = new HashMap<>();
        xsdFullTypesToJava = new HashMap<>();

        xsdTypesToJava.put("xsd:anyURI", "String");
        xsdTypesToJava.put("xsd:boolean", "Boolean");
        //xsdTypesToJava.put("xsd:base64Binary", "[B");
        //xsdTypesToJava.put("xsd:hexBinary", "[B");
        xsdTypesToJava.put("xsd:date", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:dateTime", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:time", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:duration", "Duration");
        xsdTypesToJava.put("xsd:dayTimeDuration", "Duration");
        xsdTypesToJava.put("xsd:yearMonthDuration", "Duration");
        xsdTypesToJava.put("xsd:gDay", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gMonth", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gMonthDay", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gYear", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:gYearMonth", "XMLGregorianCalendar");
        xsdTypesToJava.put("xsd:decimal", "BigDecimal");
        xsdTypesToJava.put("xsd:integer", "BigInteger");
        xsdTypesToJava.put("xsd:nonPositiveInteger", "BigInteger");
        xsdTypesToJava.put("xsd:negativeInteger", "BigInteger");
        xsdTypesToJava.put("xsd:long", "Long");
        xsdTypesToJava.put("xsd:int", "Integer");
        xsdTypesToJava.put("xsd:short", "Short");
        xsdTypesToJava.put("xsd:byte", "Byte");
        xsdTypesToJava.put("xsd:nonNegativeInteger", "BigInteger");
        xsdTypesToJava.put("xsd:unsignedLong", "BigInteger");
        xsdTypesToJava.put("xsd:unsignedInt", "Long");
        xsdTypesToJava.put("xsd:unsignedShort", "Integer");
        xsdTypesToJava.put("xsd:unsignedByte", "Short");
        xsdTypesToJava.put("xsd:positiveInteger", "BigInteger");
        xsdTypesToJava.put("xsd:double", "Double");
        xsdTypesToJava.put("xsd:float", "Float");
        xsdTypesToJava.put("xsd:QName", "QName");
        xsdTypesToJava.put("xsd:NOTATION", "QName");
        xsdTypesToJava.put("xsd:string", "String");
        xsdTypesToJava.put("xsd:normalizedString", "String");
        xsdTypesToJava.put("xsd:token", "String");
        xsdTypesToJava.put("xsd:language", "String");
        xsdTypesToJava.put("xsd:NMTOKEN", "String");
        xsdTypesToJava.put("xsd:Name", "String");
        xsdTypesToJava.put("xsd:NCName", "String");
        xsdTypesToJava.put("xsd:ID", "String");
        xsdTypesToJava.put("xsd:IDREF", "String");
        xsdTypesToJava.put("xsd:ENTITY", "String");
        xsdTypesToJava.put("xsd:untypedAtomic", "String");

        xsdFullTypesToJava.put("xsd:anyURI","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:boolean","Ljava/lang/Boolean;");
        xsdFullTypesToJava.put("xsd:date","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:dateTime","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:time","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:duration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:dayTimeDuration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:yearMonthDuration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:gDay","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gMonth","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gMonthDay","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gYear","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gYearMonth","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:decimal","Ljava/math/BigDecimal;");
        xsdFullTypesToJava.put("xsd:integer","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:nonPositiveInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:negativeInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:long","Ljava/lang/Long;");
        xsdFullTypesToJava.put("xsd:int","Ljava/lang/Integer;");
        xsdFullTypesToJava.put("xsd:short","Ljava/lang/Short;");
        xsdFullTypesToJava.put("xsd:byte","Ljava/lang/Byte;");
        xsdFullTypesToJava.put("xsd:nonNegativeInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:unsignedLong","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:unsignedInt", "java/lang/Long;");
        xsdFullTypesToJava.put("xsd:unsignedShort", "java/lang/Integer;");
        xsdFullTypesToJava.put("xsd:unsignedByte", "java/lang/Short;");
        xsdFullTypesToJava.put("xsd:positiveInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:double","Ljava/lang/Double;");
        xsdFullTypesToJava.put("xsd:float","Ljava/lang/Float;");
        xsdFullTypesToJava.put("xsd:QName","Ljavax/xml/namespace/QName;");
        xsdFullTypesToJava.put("xsd:NOTATION","Ljavax/xml/namespace/QName;");
        xsdFullTypesToJava.put("xsd:string","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:normalizedString","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:token","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:language","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:NMTOKEN","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:Name","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:NCName","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:ID","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:IDREF","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:ENTITY","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:untypedAtomic","Ljava/lang/String;");
    }

    /**
     * @param groupName A group/interface name.
     * @return An interface-like name, e.g. flowContent -> IFlowContent
     */
    static String getInterfaceName(String groupName) {
        return INTERFACE_PREFIX + toCamelCase(groupName);
    }

    static String toCamelCase(String name){
        if (name.length() == 1){
            return name.toUpperCase();
        }

        String firstLetter = name.substring(0, 1).toUpperCase();
        return firstLetter + name.substring(1);
    }

    public static String getPackage(String apiName){
        return PACKAGE_BASE + apiName + "/";
    }

    /**
     * @return The path to the destination folder of all the generated classes.
     */
    public static String getDestinationDirectory(String apiName){
        URL resource = XsdAsm.class.getClassLoader().getResource("");

        if (resource != null){
            return resource.getPath() + "/" + getPackage(apiName);
        }

        throw new RuntimeException("Target folder not found.");
    }

    /**
     * @param className The class name.
     * @return The complete file path to the given class name.
     */
    private static String getFinalPathPart(String className, String apiName){
        return getDestinationDirectory(apiName) + className + ".class";
    }

    /**
     * @param className The class name.
     * @return The full type of the class, e.g. Html -> XsdAsm/ParsedObjects/Html
     */
    static String getFullClassTypeName(String className, String apiName){
        return getPackage(apiName) + className;
    }

    /**
     * @param className The class name.
     * @return The full type descriptor of the class, e.g. Html -> LXsdClassGenerator/ParsedObjects/Html;
     */
    static String getFullClassTypeNameDesc(String className, String apiName){
        return "L" + getPackage(apiName) + className + ";";
    }

    /**
     * Creates the destination directory of the generated files, if not exists.
     */
    static void createGeneratedFilesDirectory(String apiName) {
        File folder = new File(getDestinationDirectory(apiName));

        if (!folder.exists()){
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
    }

    /**
     * Writes a given class to a .class file.
     * @param className The class name, needed to name the file.
     * @param classWriter The classWriter, which contains all the class information.
     */
    static void writeClassToFile(String className, ClassWriter classWriter, String apiName){
        classWriter.visitEnd();

        byte[] constructedClass = classWriter.toByteArray();

        try {
            FileOutputStream os = new FileOutputStream(new File(getFinalPathPart(className, apiName)));
            os.write(constructedClass);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates some class specific methods that all implementations of AbstractElement should have, which are:
     * A constructor with a String parameter, which is it will create a Text attribute in the created element.
     * A constructor with two String parameters, the first being the value of the Text attribute, and the second being a value for its id.
     * An implementation of the self method, which should return this.
     * @param classWriter The class writer on which should be written the methods.
     * @param className The class name.
     */
    static void generateClassSpecificMethods(ClassWriter classWriter, String className, String apiName) {
        String classType = getFullClassTypeName(className, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(className, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "id", JAVA_STRING_DESC);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ELEMENT_TYPE, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitFieldInsn(PUTFIELD, ABSTRACT_ELEMENT_TYPE, "id", JAVA_STRING_DESC);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, TEXT_TYPE);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, TEXT_TYPE, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + classTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "accept", "(" + VISITOR_TYPE_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", VISITOR_TYPE_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, VISITOR_TYPE, "initVisit", "(" + classTypeDesc + ")V", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "getChildren", "()Ljava/util/List;", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("accept", "(" + VISITOR_TYPE_DESC + ")Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKESTATIC, classType, "lambda$accept$0", "(" + VISITOR_TYPE_DESC + IELEMENT_TYPE_DESC + ")V", false), Type.getType("(" + IELEMENT_TYPE_DESC + ")V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, VISITOR_TYPE, "endVisit", "(" + classTypeDesc + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$accept$0", "(" + VISITOR_TYPE_DESC + IELEMENT_TYPE_DESC + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "accept", "(" + VISITOR_TYPE_DESC + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "self", "()" + IELEMENT_TYPE_DESC, null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "self", "()" + classTypeDesc, false);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param child The child of the element which generated the class. Their name represents a method.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, XsdElement child, String classType, String returnType,  String apiName) {
        String childCamelName = toCamelCase(child.getName());
        String childType = getFullClassTypeName(childCamelName, apiName);
        String childTypeDesc = getFullClassTypeNameDesc(childCamelName, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "()" + childTypeDesc, null, null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ASTORE, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();


        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + ")" + returnType, "(" + JAVA_STRING_DESC + ")" + returnType, null);
        }

        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),1);

        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 2);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + IELEMENT_TYPE_DESC, true);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();


        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + IELEMENT_TYPE_DESC, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, child.getName(), "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + returnType, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")" + returnType, null);
        }

        mVisitor.visitLocalVariable("id", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("text", JAVA_STRING_DESC, null, new Label(), new Label(),2);

        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 3);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 3);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + IELEMENT_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + returnType, true);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 4);
        mVisitor.visitEnd();
    }

    /**
     * Generates both the visitor interface and abstract visitor with method for each element from the list.
     * @param elementList The elements list.
     * @param apiName The api this classes will belong to.
     */
    static void generateVisitors(List<XsdElement> elementList, String apiName){
        generateVisitorInterface(elementList, apiName);

        generateAbstractVisitor(elementList, apiName);
    }

    /**
     * Generates the AbstractVisitor class, with methods for every element in the list.
     * @param elementList The element list.
     * @param apiName The api this class will belong to.
     */
    private static void generateAbstractVisitor(List<XsdElement> elementList, String apiName) {
        ClassWriter classWriter = generateClass(ABSTRACT_VISITOR, JAVA_OBJECT, new String[]{VISITOR}, "<R:Ljava/lang/Object;>Ljava/lang/Object;L" + VISITOR_TYPE + "<TR;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_SUPER, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_ABSTRACT + ACC_PUBLIC, "initVisit", "(" + IELEMENT_TYPE_DESC + ")V", "<T::" + IELEMENT_TYPE_DESC + ">(L" + IELEMENT_TYPE + "<TT;>;)V", null);
        mVisitor.visitLocalVariable("elem", IELEMENT_TYPE_DESC, "L" + IELEMENT_TYPE + "<TT;>;", new Label(), new Label(),1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_ABSTRACT + ACC_PUBLIC, "endVisit", "(" + IELEMENT_TYPE_DESC + ")V", "<T::" + IELEMENT_TYPE_DESC + ">(L" + IELEMENT_TYPE + "<TT;>;)V", null);
        mVisitor.visitLocalVariable("elem", IELEMENT_TYPE_DESC, IELEMENT_TYPE + "<T>", new Label(), new Label(),1);
        mVisitor.visitEnd();

        elementList.forEach(element -> addAbstractVisitorMethod(classWriter, element.getName(), null, apiName));

        addAbstractVisitorMethod(classWriter, TEXT_CLASS, "(L" + TEXT_TYPE + "<TR;>;)V", apiName);

        writeClassToFile(ABSTRACT_VISITOR, classWriter, apiName);
    }

    /**
     * Adds methods for a single element in the AbstractVisitor class.
     * @param classWriter The AbstractVisitor class writer.
     * @param elementName The element for which the methods will be generated.
     * @param signature The signature of the methods.
     * @param apiName The api name from the AbstractVisitor class.
     */
    private static void addAbstractVisitorMethod(ClassWriter classWriter, String elementName, String signature, String apiName){
        String elementTypeDesc = getFullClassTypeNameDesc(toCamelCase(elementName), apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, "initVisit", "(" + elementTypeDesc + ")V", signature, null);
        mVisitor.visitLocalVariable(elementName, elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_VISITOR_TYPE, "initVisit", "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "endVisit", "(" + elementTypeDesc + ")V", signature, null);
        mVisitor.visitLocalVariable(elementName, elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_VISITOR_TYPE, "endVisit", "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    }

    /**
     * Generates the visitor class for this api with methods for all elements in the element list.
     * @param elementList The element list.
     * @param apiName The api this class will belong to.
     */
    private static void generateVisitorInterface(List<XsdElement> elementList, String apiName) {
        ClassWriter classWriter = generateClass(VISITOR, JAVA_OBJECT, null, "<R:Ljava/lang/Object;>Ljava/lang/Object;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        elementList.forEach(element -> addVisitorInterfaceMethod(classWriter, element.getName(), null, apiName));

        addVisitorInterfaceMethod(classWriter, TEXT_CLASS, "(L" + TEXT_TYPE + "<TR;>;)V", apiName);

        writeClassToFile(VISITOR, classWriter, apiName);
    }

    /**
     * Adds methods for each element to the visitor interface.
     * @param classWriter The Visitor interface class writer.
     * @param elementName The element for which the methods will be generated.
     * @param signature The signature of the methods to be generated.
     * @param apiName The api name from the Visitor interface.
     */
    private static void addVisitorInterfaceMethod(ClassWriter classWriter, String elementName, String signature, String apiName){
        String elementTypeDesc = getFullClassTypeNameDesc(toCamelCase(elementName), apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "initVisit", "(" + elementTypeDesc + ")V", signature, null);
        mVisitor.visitLocalVariable(elementName, elementTypeDesc, signature, new Label(), new Label(),1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "endVisit", "(" + elementTypeDesc + ")V", signature, null);
        mVisitor.visitLocalVariable(elementName, elementTypeDesc, signature, new Label(), new Label(),1);
        mVisitor.visitEnd();
    }

    /**
     * Generates a method to add a given attribute.
     * @param classWriter The class where the fields will be added.
     * @param elementAttribute The attribute containing the information to create the method. (Only String fields are being supported)
     */
    @SuppressWarnings("DanglingJavadoc")
    static void generateMethodsForAttribute(ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String apiName) {
        String camelCaseName = ATTRIBUTE_PREFIX + toCamelCase(elementAttribute.getName()).replaceAll("\\W+", "");
        String attributeClassType = getFullClassTypeName(camelCaseName, apiName);
        MethodVisitor mVisitor;

        String javaType = getFullJavaType(elementAttribute);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, "add" + camelCaseName, "(" + javaType + ")" + returnType, "(" + javaType + ")TT;", null);
        } else {
            mVisitor = classWriter.visitMethod(ACC_PUBLIC, "add" + camelCaseName, "(" + javaType + ")" + returnType, "(" + javaType + ")" + returnType, null);
        }

        String attrName = elementAttribute.getName();
        attrName = attrName.substring(0, 1).toLowerCase() + attrName.substring(1);

        mVisitor.visitLocalVariable(attrName, javaType, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        /**
         * The cast to AbstractElement is needed while writing bytecode, even though it's not needed in regular written code.
         */

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitTypeInsn(CHECKCAST, IELEMENT_TYPE);
        }

        mVisitor.visitTypeInsn(NEW, attributeClassType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, attributeClassType, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + ")V", false);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE_DESC, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE_DESC, "addAttr", "(" + IATTRIBUTE_TYPE_DESC + ")V", false);
        }

        mVisitor.visitVarInsn(ALOAD, 0);

        if (returnType.equals(IELEMENT_TYPE_DESC)){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "self", "()" + returnType, true);
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
        //https://www.ibm.com/support/knowledgecenter/en/SSAW57_8.5.5/com.ibm.websphere.nd.doc/ae/txml_mapping.html

        String camelAttributeName = ATTRIBUTE_PREFIX + toCamelCase(attribute.getName()).replaceAll("\\W+", "");
        String attributeType = getFullClassTypeName(camelAttributeName, apiName);

        String javaType = getJavaType(attribute);

        ClassWriter attributeWriter = generateClass(camelAttributeName, ABSTRACT_ATTRIBUTE_TYPE, null, "<" + javaType + ":" + JAVA_OBJECT_DESC + ">L" + ABSTRACT_ATTRIBUTE_TYPE + "<T" + javaType + ";>;", ACC_PUBLIC + ACC_SUPER, apiName);

        FieldVisitor fVisitor = attributeWriter.visitField(ACC_PRIVATE + ACC_STATIC, "restrictions", JAVA_LIST_DESC, "L" + JAVA_LIST + "<Ljava/util/Map<" + JAVA_STRING_DESC + JAVA_OBJECT_DESC + ">;>;", null);
        fVisitor.visitEnd();

        MethodVisitor mVisitor = attributeWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + ")V", "(T" + javaType + ";)V", null);
        mVisitor.visitLocalVariable("attributeValue", JAVA_OBJECT_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, ABSTRACT_ATTRIBUTE_TYPE, CONSTRUCTOR, "(" + JAVA_OBJECT_DESC + ")V", false);
        mVisitor.visitFieldInsn(GETSTATIC, getFullClassTypeName(camelAttributeName, apiName), "restrictions", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInvokeDynamicInsn("accept", "(Ljava/lang/Object;)Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKESTATIC, attributeType, "lambda$new$0", "(Ljava/lang/Object;Ljava/util/Map;)V", false), Type.getType("(Ljava/util/Map;)V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = attributeWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$new$0", "(Ljava/lang/Object;Ljava/util/Map;)V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/String");
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
        mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validate", "(Ljava/util/Map;Ljava/lang/String;)V", false);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/Integer");
        Label l1 = new Label();
        mVisitor.visitJumpInsn(IFNE, l1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/Short");
        mVisitor.visitJumpInsn(IFNE, l1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/Float");
        mVisitor.visitJumpInsn(IFNE, l1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(INSTANCEOF, "java/lang/Double");
        Label l2 = new Label();
        mVisitor.visitJumpInsn(IFEQ, l2);
        mVisitor.visitLabel(l1);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(CHECKCAST, "java/lang/Double");
        mVisitor.visitMethodInsn(INVOKESTATIC, RESTRICTION_VALIDATOR_TYPE, "validate", "(Ljava/util/Map;Ljava/lang/Double;)V", false);
        mVisitor.visitLabel(l2);
        mVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        loadRestrictionsToAttribute(attributeWriter, attribute.getAllRestrictions(), camelAttributeName, apiName);

        writeClassToFile(camelAttributeName, attributeWriter, apiName);
    }

    /**
     * Loads all the existing restrictions to the attribute class. It inserts entries in a list on the static
     * constructor of the class.
     * @param attributeWriter The class writer of the attribute class.
     * @param restrictions The list of restrictions for the attribute.
     * @param camelAttributeName The attribute class name.
     * @param apiName The api this attribute will belong.
     */
    private static void loadRestrictionsToAttribute(ClassWriter attributeWriter, List<XsdRestriction> restrictions, String camelAttributeName, String apiName) {
        String attributeType = getFullClassTypeName(camelAttributeName, apiName);

        MethodVisitor mVisitor = attributeWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
        mVisitor.visitFieldInsn(PUTSTATIC, attributeType, "restrictions", JAVA_LIST_DESC);

        int currIndex = 0;

        for (XsdRestriction restriction : restrictions) {
            currIndex = loadRestrictionToAttribute(mVisitor, restriction, attributeType, currIndex);
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
    private static int loadRestrictionToAttribute(MethodVisitor mVisitor, XsdRestriction restriction, String attributeType, int index) {
        mVisitor.visitTypeInsn(NEW, "java/util/HashMap");
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", CONSTRUCTOR, "()V", false);
        mVisitor.visitVarInsn(ASTORE, index);

        String base = restriction.getBase();
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

        if (base != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("Base");
            mVisitor.visitLdcInsn(base);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        }

        if (length != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("Length");
            mVisitor.visitIntInsn(BIPUSH, length.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (maxLength != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MaxLength");
            mVisitor.visitIntInsn(BIPUSH, maxLength.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (minLength != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MinLength");
            mVisitor.visitIntInsn(BIPUSH, minLength.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (maxExclusive != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MaxExclusive");
            mVisitor.visitIntInsn(BIPUSH, maxExclusive.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (maxInclusive != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MaxInclusive");
            mVisitor.visitIntInsn(BIPUSH, maxInclusive.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (minExclusive != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MinExclusive");
            mVisitor.visitIntInsn(BIPUSH, minExclusive.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (minInclusive != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("MinInclusive");
            mVisitor.visitIntInsn(BIPUSH, minInclusive.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (fractionDigits != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("FractionDigits");
            mVisitor.visitIntInsn(BIPUSH, fractionDigits.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (totalDigits != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("TotalDigits");
            mVisitor.visitIntInsn(BIPUSH, totalDigits.getValue());
            mVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        if (pattern != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("Pattern");
            mVisitor.visitLdcInsn(pattern.getValue());
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        }

        if (whiteSpace != null){
            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("WhiteSpace");
            mVisitor.visitLdcInsn(whiteSpace.getValue());
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        }
        int enumerationIndex = 0;

        if (!enumerations.isEmpty()){
            enumerationIndex = index + 1;

            mVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
            mVisitor.visitInsn(DUP);
            mVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", CONSTRUCTOR, "()V", false);
            mVisitor.visitVarInsn(ASTORE, enumerationIndex);

            int finalEnumerationIndex = enumerationIndex;
            enumerations.forEach(enumeration -> {
                mVisitor.visitVarInsn(ALOAD, finalEnumerationIndex);
                mVisitor.visitLdcInsn(enumeration.getValue());
                mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(Ljava/lang/Object;)Z", true);
                mVisitor.visitInsn(POP);
            });

            mVisitor.visitVarInsn(ALOAD, index);
            mVisitor.visitLdcInsn("Enumeration");
            mVisitor.visitVarInsn(ALOAD, finalEnumerationIndex);
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mVisitor.visitInsn(POP);
        }

        mVisitor.visitFieldInsn(GETSTATIC, attributeType, "restrictions", JAVA_LIST_DESC);
        mVisitor.visitVarInsn(ALOAD, index);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "add", "(Ljava/lang/Object;)Z", true);
        mVisitor.visitInsn(POP);

        return enumerationIndex == 0 ? index : enumerationIndex;
    }

    private static String getJavaType(XsdAttribute attribute) {
        return getJavaType(attribute, xsdTypesToJava, "Object");
    }

    private static String getFullJavaType(XsdAttribute attribute) {
        return getJavaType(attribute, xsdFullTypesToJava, JAVA_OBJECT_DESC);
    }

    private static String getJavaType(XsdAttribute attribute, HashMap<String, String> xsdTypes, String defaultType){
        String javaType = xsdTypes.getOrDefault(attribute.getType(), null);

        //List<String> restrictions = new ArrayList<>();

        if (javaType == null){
            Optional<String> firstType = attribute.getAllRestrictions().stream().map(XsdRestriction::getBase).distinct().map(type -> xsdTypes.getOrDefault(type, "Object")).findFirst();

            if (firstType.isPresent()){
                javaType = firstType.get();
            } else {
                javaType = defaultType;
            }
        } //else {
        //restrictions.add(javaType);
        //}

        return javaType;
    }

    /**
     * Generates a default constructor.
     * @param classWriter The class writer from the class where the constructors will be added.
     * @param constructorType The modifiers for the constructor.
     */
    static void generateConstructor(ClassWriter classWriter, String baseClass, int constructorType, String apiName) {
        MethodVisitor defaultConstructor = classWriter.visitMethod(constructorType, CONSTRUCTOR, "()V",null,null);

        defaultConstructor.visitCode();
        defaultConstructor.visitVarInsn(ALOAD, 0);
        defaultConstructor.visitMethodInsn(INVOKESPECIAL, baseClass, CONSTRUCTOR, "()V", false);
        defaultConstructor.visitInsn(RETURN);
        defaultConstructor.visitMaxs(1, 1);

        defaultConstructor.visitEnd();
    }

    /**
     * Generates an empty class.
     * @param className The classes name.
     * @param superName The super object, which the class extends from.
     * @param interfaces The name of the interfaces which this class implements.
     * @param classModifiers The modifiers to the class.
     * @return A class writer that will be used to write the remaining information of the class.
     */
    static ClassWriter generateClass(String className, String superName, String[] interfaces, String signature, int classModifiers, String apiName) {
        ClassWriter classWriter = new ClassWriter(0);

        if (interfaces != null){
            for (int i = 0; i < interfaces.length; i++) {
                interfaces[i] = getFullClassTypeName(interfaces[i], apiName);
            }
        }

        classWriter.visit(V1_8, classModifiers, getFullClassTypeName(className, apiName), signature, superName, interfaces);

        return classWriter;
    }

}
