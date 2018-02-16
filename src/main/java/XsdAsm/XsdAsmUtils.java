package XsdAsm;

import XsdElements.*;
import XsdElements.XsdRestrictionElements.*;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static XsdAsm.XsdAsmAttributes.*;
import static XsdAsm.XsdSupportingStructure.*;
import static org.objectweb.asm.Opcodes.*;

public class XsdAsmUtils {

    @SuppressWarnings("FieldCanBeLocal")
    private static String PACKAGE_BASE = "XsdToJavaAPI/";
    private static final String INTERFACE_PREFIX = "I";
    private static final HashMap<String, String> xsdFullTypesToJava;

    static {
        xsdFullTypesToJava = new HashMap<>();

        xsdFullTypesToJava.put("xsd:anyURI","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:anyURI","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:boolean","Ljava/lang/Boolean;");
        xsdFullTypesToJava.put("xs:boolean","Ljava/lang/Boolean;");
        xsdFullTypesToJava.put("xsd:date","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xs:date","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:dateTime","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xs:dateTime","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:time","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xs:time","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:duration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xs:duration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:dayTimeDuration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xs:dayTimeDuration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:yearMonthDuration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xs:yearMonthDuration","Ljavax/xml/datatype/Duration;");
        xsdFullTypesToJava.put("xsd:gDay","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xs:gDay","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gMonth","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xs:gMonth","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gMonthDay","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xs:gMonthDay","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gYear","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xs:gYear","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:gYearMonth","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xs:gYearMonth","Ljavax/xml/datatype/XMLGregorianCalendar;");
        xsdFullTypesToJava.put("xsd:decimal","Ljava/math/BigDecimal;");
        xsdFullTypesToJava.put("xs:decimal","Ljava/math/BigDecimal;");
        xsdFullTypesToJava.put("xsd:integer","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xs:integer","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:nonPositiveInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xs:nonPositiveInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:negativeInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xs:negativeInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:long","Ljava/lang/Long;");
        xsdFullTypesToJava.put("xs:long","Ljava/lang/Long;");
        xsdFullTypesToJava.put("xsd:int","Ljava/lang/Integer;");
        xsdFullTypesToJava.put("xs:int","Ljava/lang/Integer;");
        xsdFullTypesToJava.put("xsd:short","Ljava/lang/Short;");
        xsdFullTypesToJava.put("xs:short","Ljava/lang/Short;");
        xsdFullTypesToJava.put("xsd:byte","Ljava/lang/Byte;");
        xsdFullTypesToJava.put("xs:byte","Ljava/lang/Byte;");
        xsdFullTypesToJava.put("xsd:nonNegativeInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xs:nonNegativeInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:unsignedLong","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xs:unsignedLong","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:unsignedInt", "java/lang/Long;");
        xsdFullTypesToJava.put("xs:unsignedInt", "java/lang/Long;");
        xsdFullTypesToJava.put("xsd:unsignedShort", "java/lang/Integer;");
        xsdFullTypesToJava.put("xs:unsignedShort", "java/lang/Integer;");
        xsdFullTypesToJava.put("xsd:unsignedByte", "java/lang/Short;");
        xsdFullTypesToJava.put("xs:unsignedByte", "java/lang/Short;");
        xsdFullTypesToJava.put("xsd:positiveInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xs:positiveInteger","Ljava/math/BigInteger;");
        xsdFullTypesToJava.put("xsd:double","Ljava/lang/Double;");
        xsdFullTypesToJava.put("xs:double","Ljava/lang/Double;");
        xsdFullTypesToJava.put("xsd:float","Ljava/lang/Float;");
        xsdFullTypesToJava.put("xs:float","Ljava/lang/Float;");
        xsdFullTypesToJava.put("xsd:QName","Ljavax/xml/namespace/QName;");
        xsdFullTypesToJava.put("xs:QName","Ljavax/xml/namespace/QName;");
        xsdFullTypesToJava.put("xsd:NOTATION","Ljavax/xml/namespace/QName;");
        xsdFullTypesToJava.put("xs:NOTATION","Ljavax/xml/namespace/QName;");
        xsdFullTypesToJava.put("xsd:string","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:string","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:normalizedString","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:normalizedString","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:token","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:token","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:language","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:language","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:NMTOKEN","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:NMTOKEN","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:Name","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:Name","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:NCName","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:NCName","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:ID","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:ID","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:IDREF","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:IDREF","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:ENTITY","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:ENTITY","Ljava/lang/String;");
        xsdFullTypesToJava.put("xsd:untypedAtomic","Ljava/lang/String;");
        xsdFullTypesToJava.put("xs:untypedAtomic","Ljava/lang/String;");
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

    static String firstToLower(String name){
        if (name.length() == 1){
            return name.toLowerCase();
        }

        String firstLetter = name.substring(0, 1).toLowerCase();
        return firstLetter + name.substring(1);
    }

    public static String getPackage(String apiName){
        return PACKAGE_BASE + apiName + "/";
    }

    /**
     * @param apiName The name of the api to be generated.
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
     * Indicates if the method is from an interface or a concrete class based on its return type.
     * @param returnType The method return type.
     * @return True if the method belongs to an interface and false if it belongs to a concrete class.
     */
    static boolean isInterfaceMethod(String returnType) {
        return returnType.equals(IELEMENT_TYPE_DESC);
    }

    static String getFullJavaType(String itemType) {
        return xsdFullTypesToJava.getOrDefault(itemType, JAVA_OBJECT_DESC);
    }

    /**
     * Obtains the java type descriptor based on the attribute type attribute.
     * @param attribute The attribute from which the type will be obtained.
     * @return The java descriptor of the attribute type.
     */
    static String getFullJavaType(XsdAttribute attribute){
        List<XsdRestriction> restrictions = getAttributeRestrictions(attribute);
        String javaType = xsdFullTypesToJava.getOrDefault(attribute.getType(), null);

        if (javaType == null){
            if (restrictions.size() != 0){
                return xsdFullTypesToJava.getOrDefault(restrictions.get(0).getBase(), JAVA_OBJECT_DESC);
            }

            return JAVA_OBJECT_DESC;
        }

        return javaType;
    }

    static String getEnumElementName(XsdEnumeration enumElem) {
        return enumElem.getValue().toUpperCase().replaceAll("[^a-zA-Z0-9]", "_");
    }

    static List<XsdRestriction> getAttributeRestrictions(XsdAttribute attribute) {
        try {
            return attribute.getAllRestrictions();
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage() + " at attribute with name = " + attribute.getName());
        }
    }

    /**
     * Generates the required methods for adding a given xsdAttribute and creates the
     * respective class, if needed.
     * @param classWriter The class writer to write the methods.
     * @param elementAttribute The attribute element.
     * @param apiName The api this class will belong.
     */
    static void generateMethodsAndCreateAttribute(List<String> createdAttributes, ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String apiName) {
        generateMethodsForAttribute(classWriter, elementAttribute, returnType, apiName);

        if (!createdAttributes.contains(elementAttribute.getName())){
            generateAttribute(elementAttribute, apiName);

            createdAttributes.add(elementAttribute.getName());
        }
    }

    /**
     * Obtains the attributes which are specific to the given element.
     * @param element The element containing the attributes.
     * @return A list of attributes that are exclusive to the element.
     */
    static Stream<XsdAttribute> getOwnAttributes(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            return complexType.getXsdAttributes()
                    .filter(attribute -> attribute.getParent().getClass().equals(XsdComplexType.class));
        }

        return Stream.empty();
    }

    /**
     * Obtains the signature for a class given the interface names.
     * @param interfaces The implemented interfaces.
     * @param className The class name.
     * @param apiName The api this class will belong.
     * @return The signature of the class.
     */
    static String getClassSignature(String[] interfaces, String className, String apiName) {
        StringBuilder signature = new StringBuilder("<P::" + IELEMENT_TYPE_DESC + ">L" + ABSTRACT_ELEMENT_TYPE + "<L" + getFullClassTypeName(className, apiName) + "<TP;>;TP;>;");

        if (interfaces != null){
            for (String anInterface : interfaces) {
                signature.append("L")
                        .append(getFullClassTypeName(anInterface, apiName))
                        .append("<L")
                        .append(getFullClassTypeName(className, apiName))
                        .append("<TP;>;TP;>;");
            }
        }

        return signature.toString();
    }

    /**
     * Obtains the interface signature for a interface.
     * @param interfaces The extended interfaces.
     * @param apiName The name of the API to be generated.
     * @return The interface signature.
     */
    static String getInterfaceSignature(String[] interfaces, String apiName) {
        StringBuilder signature = new StringBuilder("<T::L" + IELEMENT_TYPE + "<TT;TP;>;P::" + IELEMENT_TYPE_DESC + ">Ljava/lang/Object;");

        if (interfaces != null){
            for (String anInterface : interfaces) {
                signature.append("L")
                        .append(getFullClassTypeName(anInterface, apiName))
                        .append("<TT;TP;>;");
            }
        }

        return signature.toString();
    }

    /**
     * Generates a default constructor.
     * @param classWriter The class writer from the class where the constructors will be added.
     * @param constructorType The modifiers for the constructor.
     */
    static void generateConstructor(ClassWriter classWriter, String baseClass, int constructorType) {
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

