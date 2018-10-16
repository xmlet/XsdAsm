package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.xmlet.xsdasm.classes.utils.AsmException;
import org.xmlet.xsdasm.classes.utils.InterfaceInfo;
import org.xmlet.xsdparser.xsdelements.*;
import org.xmlet.xsdparser.xsdelements.xsdrestrictions.XsdEnumeration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.V1_8;
import static org.xmlet.xsdasm.classes.XsdAsmAttributes.generateMethodsForAttribute;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

/**
 * This class contains multiple utility methods to the whole XsdAsmFaster solution.
 */
public class XsdAsmUtils {

    /**
     * A {@link HashMap} which contains information to convert XSD types to their Java counterparts.
     */
    private static final HashMap<String, String> xsdFullTypesToJava;

    static {
        xsdFullTypesToJava = new HashMap<>();

        xsdFullTypesToJava.put("xsd:anyURI",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:anyURI",JAVA_STRING_DESC);
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
        xsdFullTypesToJava.put("xsd:string",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:string",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:normalizedString",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:normalizedString",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:token",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:token",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:language",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:language",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:NMTOKEN",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:NMTOKEN",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:Name",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:Name",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:NCName",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:NCName",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:ID",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:ID",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:IDREF",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:IDREF",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:ENTITY",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:ENTITY",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xsd:untypedAtomic",JAVA_STRING_DESC);
        xsdFullTypesToJava.put("xs:untypedAtomic",JAVA_STRING_DESC);
    }

    private XsdAsmUtils(){}

    /**
     * Changes the first char of the received {@link String} to uppercase.
     * @param name The received {@link String}.
     * @return The received {@link String} with the first char in uppercase.
     */
    static String firstToUpper(String name){
        if (name.length() == 1){
            return name.toUpperCase();
        }

        String firstLetter = name.substring(0, 1).toUpperCase();
        return firstLetter + name.substring(1);
    }

    /**
     * Changes the first char of the received {@link String} to lowercase.
     * @param name The received {@link String}.
     * @return The received {@link String} with the first char in lowercase.
     */
    static String firstToLower(String name){
        if (name.length() == 1){
            return name.toLowerCase();
        }

        String firstLetter = name.substring(0, 1).toLowerCase();
        return firstLetter + name.substring(1);
    }

    /**
     * Returns the name of the package where the fluent interface will be contained.
     * @param apiName The name of the generated fluent interface.
     * @return The name of the package where the fluent interface will be contained.
     */
    public static String getPackage(String apiName){
        return "org/xmlet/" + apiName + "/";
    }

    /**
     * @param apiName The name of the generated fluent interface.
     * @return The path to the destination folder of all the generated classes.
     */
    public static String getDestinationDirectory(String apiName){
        URL resource = XsdAsm.class.getClassLoader().getResource("");

        if (resource != null){
            return resource.getPath() /*+ "/" */ + getPackage(apiName);
        }

        throw new AsmException("Target folder not found.");
    }

    /**
     * @param className The class name.
     * @param apiName The name of the generated fluent interface.
     * @return The complete file path to the given class name.
     */
    private static String getFinalPathPart(String className, String apiName){
        return getDestinationDirectory(apiName) + className + ".class";
    }

    /**
     * @param className The class name.
     * @param apiName The name of the generated fluent interface.
     * @return The full type of the class, e.g. Html -> XsdAsm/ParsedObjects/Html
     */
    static String getFullClassTypeName(String className, String apiName){
        String infrastructureClass = infrastructureVars.get(className);

        if (infrastructureClass != null){
            return infrastructureClass;
        }

        return getPackage(apiName) + className;
    }

    /**
     * @param className The class name.
     * @param apiName The name of the generated fluent interface.
     * @return The full type descriptor of the class, e.g. Html -> LXsdClassGenerator/ParsedObjects/Html;
     */
    static String getFullClassTypeNameDesc(String className, String apiName){
        return "L" + getFullClassTypeName(className, apiName) + ";";
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

        try (FileOutputStream os = new FileOutputStream(new File(getFinalPathPart(className, apiName)))){
            os.write(constructedClass);
        } catch (IOException e) {
            throw new AsmException("Exception while writing generated classes to the .class files.", e);
        }
    }

    /**
     * Indicates if the method is from an interface or a concrete class based on its return type.
     * @param returnType The method return type.
     * @return True if the method belongs to an interface and false if it belongs to a concrete class.
     */
    static boolean isInterfaceMethod(String returnType) {
        return returnType.equals(elementTypeDesc);
    }

    /**
     * Converts the received XSD type to its Java counterpart.
     * @param itemType The received XSD type.
     * @return The Java counterpart to the received XSD type.
     */
    static String getFullJavaTypeDesc(String itemType) {
        return xsdFullTypesToJava.getOrDefault(itemType, JAVA_OBJECT_DESC);
    }

    /**
     * Obtains the java type descriptor based on the attribute type attribute.
     * @param attribute The attribute from which the type will be obtained.
     * @return The java descriptor of the attribute type.
     */
    static String getFullJavaTypeDesc(XsdAttribute attribute){
        List<XsdRestriction> restrictions = getAttributeRestrictions(attribute);
        String javaType = xsdFullTypesToJava.getOrDefault(attribute.getType(), null);

        if (javaType == null){
            if (!restrictions.isEmpty()){
                return xsdFullTypesToJava.getOrDefault(restrictions.get(0).getBase(), JAVA_OBJECT_DESC);
            }

            return JAVA_OBJECT_DESC;
        }

        return javaType;
    }

    static String getEnumElementName(XsdEnumeration enumElem) {
        return enumElem.getValue().toUpperCase().replaceAll("[^a-zA-Z0-9]", "_");
    }

    /**
     * Obtains the attribute restrictions.
     * @param attribute The received {@link XsdAttribute}.
     * @return The restrictions of the received {@link XsdAttribute}.
     */
    static List<XsdRestriction> getAttributeRestrictions(XsdAttribute attribute) {
        try {
            return attribute.getAllRestrictions();
        } catch (RuntimeException e){
            throw new AsmException("The xsd file is invalid because has contradictory restrictions.", e);
        }
    }

    /**
     * Generates the required methods for adding a given attribute and creates the
     * respective class, if needed.
     * @param createdAttributes Information about attributes that were already created.
     * @param classWriter The {@link ClassWriter} to write the methods.
     * @param elementAttribute The attribute element.
     * @param returnType The method return type.
     * @param className The name of the class which will contain the method to add the attribute.
     * @param apiName The name of the generated fluent interface.
     */
    static void generateMethodsAndCreateAttribute(Map<String, List<XsdAttribute>> createdAttributes, ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String className, String apiName) {
        generateMethodsForAttribute(classWriter, elementAttribute, returnType, className,apiName);
        createAttribute(createdAttributes, elementAttribute);
    }

    /**
     * Adds an attribute to createAttributes {@link Map} object.
     * @param createdAttributes The received {@link Map}. Contains the already created attributes.
     * @param elementAttribute The new attribute to add to the {@link Map} object.
     */
    private static void createAttribute(Map<String, List<XsdAttribute>> createdAttributes, XsdAttribute elementAttribute) {
        if (!createdAttributes.containsKey(elementAttribute.getName())){
            List<XsdAttribute> attributes = new ArrayList<>();

            attributes.add(elementAttribute);

            createdAttributes.put(elementAttribute.getName(), attributes);
        } else {
            List<XsdAttribute> attributes = createdAttributes.get(elementAttribute.getName());

            if (!attributes.contains(elementAttribute)){
                attributes.add(elementAttribute);
            }
        }
    }

    /**
     * @param element The received {@link XsdElement}.
     * @return The {@link XsdExtension} from the received {@link XsdElement}.
     */
    static XsdExtension getXsdExtension(XsdElement element){
        if (element != null){
            XsdComplexType complexType = element.getXsdComplexType();

            if (complexType != null) {
                return getXsdExtension(complexType.getComplexContent());
            }
        }

        return null;
    }

    /**
     * @param element The {@link XsdElement}.
     * @return The element which contains interface information.
     */
    static XsdAbstractElement getElementInterfacesElement(XsdElement element){
        XsdAbstractElement child = null;

        if (element != null){
            XsdComplexType complexType = element.getXsdComplexType();

            if (complexType != null){
                child = complexType.getXsdChildElement();

                if (child == null){
                    XsdComplexContent complexContent = complexType.getComplexContent();

                    if (complexContent != null){
                        XsdExtension extension = complexContent.getXsdExtension();

                        if (extension != null){
                            child = extension.getXsdChildElement();
                        }
                    }
                }
            }
        }

        return child;
    }

    private static XsdExtension getXsdExtension(XsdComplexContent complexContent){
        return complexContent != null ? complexContent.getXsdExtension(): null;
    }

    /**
     * Obtains the base attribute value from the {@link XsdExtension} from the received {@link XsdElement}.
     * @param element The received {@link XsdElement} object.
     * @return The base value of the received {@link XsdElement}.
     */
    static XsdElement getBaseFromElement(XsdElement element) {
        XsdExtension extension = getXsdExtension(element);

        return extension != null ? extension.getBase(): null;
    }

    /**
     * Converts a {@link List} of {@link String} into a {@link String} array.
     * @param elements The {@link List} of {@link String} objects.
     * @param defaultValue A default value, used if the {@link List} is either empty or null.
     * @return The {@link String} array.
     */
    static String[] listToArray(List<String> elements, String defaultValue){
        if (elements == null || elements.isEmpty()) return new String[]{defaultValue};

        String[] elementsArr = new String[elements.size()];
        elements.toArray(elementsArr);

        return elementsArr;
    }

    /**
     * Converts a {@link List} of {@link String} into a {@link String} array.
     * @param elements The {@link List} of {@link String} objects.
     * @return The {@link String} array.
     */
    static String[] listToArray(List<String> elements){
        if (elements == null || elements.isEmpty()) return new String[]{};

        String[] elementsArr = new String[elements.size()];
        elements.toArray(elementsArr);

        return elementsArr;
    }

    /**
     * Obtains the {@link XsdAttribute} objects which are specific to the given {@link XsdElement}.
     * @param element The {@link XsdElement} object containing the attributes.
     * @return A {@link Stream} of {@link XsdAttribute} that are exclusive to the received {@link XsdElement}.
     */
    static Stream<XsdAttribute> getOwnAttributes(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            Stream<XsdAttribute> extensionAttributes = null;
            Stream<XsdAttribute> complexTypeAttributes;

            XsdComplexContent complexContent = complexType.getComplexContent();

            if (complexContent != null){
                XsdExtension extension = complexContent.getXsdExtension();

                if (extension != null){
                    extensionAttributes = extension.getXsdAttributes();
                }
            }

            complexTypeAttributes = complexType.getXsdAttributes()
                    .filter(attribute -> attribute.getParent().getClass().equals(XsdComplexType.class));

            if (extensionAttributes != null){
                return Stream.concat(extensionAttributes, complexTypeAttributes);
            } else {
                return complexTypeAttributes;
            }
        }

        return Stream.empty();
    }

    /**
     * Obtains the signature for a class given the interface names.
     * @param interfaces The implemented interfaces.
     * @param className The class name.
     * @param apiName The name of the generated fluent interface.
     * @return The signature of the class.
     */
    static String getClassSignature(String[] interfaces, String className, String apiName) {
        StringBuilder signature;

        signature = new StringBuilder("<Z::" + elementTypeDesc + ">L" + abstractElementType + "<L" + getFullClassTypeName(className, apiName) + "<TZ;>;TZ;>;");

        if (interfaces != null){
            for (String anInterface : interfaces) {
                signature.append("L")
                        .append(getFullClassTypeName(anInterface, apiName))
                        .append("<L")
                        .append(getFullClassTypeName(className, apiName))
                        .append("<TZ;>;TZ;>;");
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
        StringBuilder signature = new StringBuilder("<T::L" + elementType + "<TT;TZ;>;Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC);

        if (interfaces != null){
            for (String anInterface : interfaces) {
                signature.append("L")
                        .append(getFullClassTypeName(anInterface, apiName))
                        .append("<TT;TZ;>;");
            }
        }

        return signature.toString();
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

    /**
     * Replaces special characters with '_' from the received {@link XsdNamedElements} name.
     * @param element The received {@link XsdNamedElements} object.
     * @return The clean name.
     */
    static String getCleanName(XsdNamedElements element){
        return getCleanName(element.getName());
    }

    /**
     * Replaces special characters with '_' from the received {@link String}.
     * @param name The received name.
     * @return The clean name.
     */
    static String getCleanName(String name){
        String[] parts = name.split("_");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            result.append(firstToUpper(part));
        }

        return result.toString();
    }

    /**
     * Obtains a {@link Set} of ambiguous names from a given interface hierarchy.
     * @param interfaceInfoList A {@link List} of {@link InterfaceInfo} containing interface information.
     * @return A {@link Set} of ambiguous method names.
     */
    static Set<String> getAmbiguousMethods(List<InterfaceInfo> interfaceInfoList) {
        Set<String> ambiguousNames = new HashSet<>();
        Set<String> dummy = new HashSet<>();
        List<String> interfaceMethods = getAllInterfaceMethodInfo(interfaceInfoList);

        interfaceMethods.forEach(interfaceMethodName -> {
            boolean methodNameAlreadyPresent = dummy.add(interfaceMethodName);

            if (!methodNameAlreadyPresent){
                ambiguousNames.add(interfaceMethodName);
            }
        });

        return ambiguousNames;
    }

    /**
     * Obtain the names of all the methods in the current interface and all the extended interfaces.
     * @param interfaceInfoList A {@link List} of {@link InterfaceInfo}.
     * @return The names of all the methods in the current and extended interfaces.
     */
    private static List<String> getAllInterfaceMethodInfo(List<InterfaceInfo> interfaceInfoList) {
        List<String> names = new ArrayList<>();

        if (interfaceInfoList == null || interfaceInfoList.isEmpty()){
            return names;
        }

        interfaceInfoList.forEach((InterfaceInfo interfaceInfo) -> {
            if (interfaceInfo.getMethodNames() != null && !interfaceInfo.getMethodNames().isEmpty()){
                names.addAll(interfaceInfo.getMethodNames());
            }

            names.addAll(getAllInterfaceMethodInfo(interfaceInfo.getExtendedInterfaces()));
        });

        return names;
    }

    /**
     * Converts the received xsdType into the respective javaType.
     * @param xsdType The received XSD type.
     * @return The Java type that represents the received xsdType.
     */
    static String getJavaType(String xsdType){
        return xsdFullTypesToJava.getOrDefault(xsdType, null);
    }
}

