package XsdAsm;

import XsdElements.*;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassWriter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static XsdAsm.XsdAsmUtils.*;
import static XsdAsm.XsdSupportingStructure.createSupportingInfrastructure;
import static org.objectweb.asm.Opcodes.*;

public class XsdAsm {

    static final String JAVA_OBJECT = "java/lang/Object";
    static final String JAVA_OBJECT_DESC = "Ljava/lang/Object;";
    static final String JAVA_STRING = "java/lang/String";
    static final String JAVA_STRING_DESC = "Ljava/lang/String;";
    static final String JAVA_LIST = "java/util/List";
    static final String JAVA_LIST_DESC = "Ljava/util/List;";
    static final String CONSTRUCTOR = "<init>";
    static final String IELEMENT = "IElement";
    static final String IATTRIBUTE = "IAttribute";
    static final String ABSTRACT_ELEMENT = "AbstractElement";
    static final String ABSTRACT_ATTRIBUTE = "AbstractAttribute";
    static final String TEXT_CLASS = "Text";
    static final String ITEXT = "IText";
    static final String RESTRICTION_VIOLATION_EXCEPTION = "RestrictionViolationException";
    static final String RESTRICTION_VALIDATOR = "RestrictionValidator";
    static final String VISITOR = "Visitor";
    static final String ABSTRACT_VISITOR = "AbstractVisitor";

    static String TEXT_TYPE;
    static String TEXT_TYPE_DESC;
    static String ABSTRACT_ELEMENT_TYPE;
    static String ABSTRACT_ELEMENT_TYPE_DESC;
    static String ABSTRACT_ATTRIBUTE_TYPE;
    static String ABSTRACT_ATTRIBUTE_TYPE_DESC;
    static String IELEMENT_TYPE;
    static String IELEMENT_TYPE_DESC;
    static String IATTRIBUTE_TYPE;
    static String IATTRIBUTE_TYPE_DESC;
    static String ITEXT_TYPE;
    static String ITEXT_TYPE_DESC;
    static String RESTRICTION_VIOLATION_EXCEPTION_TYPE;
    static String RESTRICTION_VIOLATION_EXCEPTION_TYPE_DESC;
    static String RESTRICTION_VALIDATOR_TYPE;
    static String VISITOR_TYPE;
    static String VISITOR_TYPE_DESC;
    static String ABSTRACT_VISITOR_TYPE;

    static final String ATTRIBUTE_PREFIX = "Attr";
    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFERENCE = "Alt";

    private Map<String, Stream<XsdElement>> elementGroupInterfaces = new HashMap<>();
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();
    private List<String> createdAttributes = new ArrayList<>();

    public void generateClassFromElements(Stream<XsdAbstractElement> elements, String apiName){
        createGeneratedFilesDirectory(apiName);

        createSupportingInfrastructure(apiName);

        List<XsdElement> elementList = elements.filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .collect(Collectors.toList());

        elementList.forEach(element -> generateClassFromElement(element, apiName));

        generateInterfaces(apiName);

        generateVisitors(elementList, apiName);
    }

    /**
     * Generates a class from a given XsdElement. It also generated its constructors and methods.
     * @param element The element from which the class will be generated.
     * @param apiName The api this class will belong.
     */
    private void generateClassFromElement(XsdElement element, String apiName) {
        String className = toCamelCase(element.getName());

        Stream<XsdElement> elementChildren = getOwnChildren(element);
        Stream<XsdAttribute> elementAttributes = getOwnAttributes(element);
        String[] interfaces = getInterfaces(element);

        String signature = getClassSignature(interfaces, className, apiName);

        ClassWriter classWriter = generateClass(className, ABSTRACT_ELEMENT_TYPE, interfaces, signature,ACC_PUBLIC + ACC_SUPER, apiName);

        generateConstructor(classWriter, ABSTRACT_ELEMENT_TYPE, ACC_PUBLIC, apiName);

        generateClassSpecificMethods(classWriter, className, apiName);

        elementChildren.forEach(child -> generateMethodsForElement(classWriter, child, getFullClassTypeName(className, apiName), getFullClassTypeNameDesc(className, apiName), apiName));

        elementAttributes.forEach(elementAttribute -> generateMethodsAndCreateAttribute(classWriter, elementAttribute, getFullClassTypeNameDesc(className, apiName), apiName));

        writeClassToFile(className, classWriter, apiName);
    }

    /**
     * Generates all the required interfaces, based on the information gathered while
     * creating the other classes. It creates both types of interfaces:
     * ElementGroupInterfaces - Interfaces that serve as a base to adding child elements to the current element;
     * AttributeGroupInterfaces - Interface that serve as a base to adding attributes to the current element;
     * @param apiName The api this class will belong.
     */
    private void generateInterfaces(String apiName) {
        elementGroupInterfaces.keySet().forEach(interfaceName -> generateElementGroupInterface(interfaceName, apiName));

        attributeGroupInterfaces.keySet().forEach(attributeGroupInterface -> generateAttributesGroupInterface(attributeGroupInterface, attributeGroupInterfaces.get(attributeGroupInterface), apiName));
    }

    /**
     * Generates a interface with all the required methods. It uses the information gathered about in elementGroupInterfaces.
     * @param interfaceName The interface name.
     * @param apiName The api this class will belong.
     */
    private void generateElementGroupInterface(String interfaceName, String apiName){
        ClassWriter interfaceWriter = generateClass(interfaceName, JAVA_OBJECT, new String[]{ ITEXT },"<T::L" + IELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC + "L" + ITEXT_TYPE + "<TT;>;" ,ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        elementGroupInterfaces.get(interfaceName).forEach(child -> generateMethodsForElement(interfaceWriter, child, getFullClassTypeName(interfaceName, apiName), IELEMENT_TYPE_DESC, apiName));

        writeClassToFile(interfaceName, interfaceWriter, apiName);
    }

    /**
     * Generates a interface with all the required methods. It uses the information gathered about in attributeGroupInterfaces.
     * @param attributeGroupName The interface name.
     * @param attributeHierarchyItem An object containing information about the methods of this interface and which interface, if any,
     *                               this interface extends.
     * @param apiName The api this class will belong.
     */
    private void generateAttributesGroupInterface(String attributeGroupName, AttributeHierarchyItem attributeHierarchyItem, String apiName){
        String baseClassNameCamelCase = toCamelCase(attributeGroupName);
        String[] interfaces = getAttributeGroupObjectInterfaces(attributeHierarchyItem.getParentsName());
        StringBuilder signature = getAttributeGroupSignature(interfaces, apiName);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, interfaces, signature.toString(), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        attributeHierarchyItem.getOwnElements().forEach(elementAttribute -> {
            if (createdAttributes.stream().anyMatch(createdAttributeName -> createdAttributeName.equalsIgnoreCase(elementAttribute.getName()))){
                elementAttribute.setName(elementAttribute.getName() + ATTRIBUTE_CASE_SENSITIVE_DIFERENCE);
            }

            generateMethodsAndCreateAttribute(interfaceWriter, elementAttribute, IELEMENT_TYPE_DESC, apiName);
        });

        writeClassToFile(baseClassNameCamelCase, interfaceWriter, apiName);
    }

    /**
     * Generates the required methods for adding a given xsdAttribute and creates the
     * respective class, if needed.
     * @param classWriter The class writer to write the methods.
     * @param elementAttribute The attribute element.
     * @param apiName The api this class will belong.
     */
    private void generateMethodsAndCreateAttribute(ClassWriter classWriter, XsdAttribute elementAttribute, String returnType, String apiName) {
        generateMethodsForAttribute(classWriter, elementAttribute, returnType, apiName);

        if (!createdAttributes.contains(elementAttribute.getName())){
            generateAttribute(elementAttribute, apiName);

            createdAttributes.add(elementAttribute.getName());
        }
    }

    /**
     * This method obtains the element interfaces which his class will be implementing.
     * The interfaces are represented in XsdElements as XsdGroups, and their respective
     * methods as children of the XsdGroup.
     * @param element The element from which the interfaces will be obtained.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getElementGroupInterfaces(XsdElement element){
        String[] typeInterfaces = new String[0], groupInterfaces = new String[0];
        XsdAbstractElement typeWrapper = element.getXsdType();

        if (typeWrapper != null && typeWrapper instanceof XsdComplexType){

            typeInterfaces = getElementGroupInterfaces((XsdComplexType) typeWrapper);
        }

        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null){
            groupInterfaces = getElementGroupInterfaces(complexType);
        }

        String[] interfaces = ArrayUtils.addAll(typeInterfaces, groupInterfaces);

        if (interfaces.length == 0){
            return new String[]{ITEXT};
        }

        return interfaces;
    }

    /**
     * Obtains all the xsdGroups in the given element which will be used to create interfaces.
     * @param complexType The complexType of the element which will be implementing the interfaces.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getElementGroupInterfaces(XsdComplexType complexType) {
        XsdAbstractElement complexChildElement = complexType.getXsdChildElement();

        Map<String, Stream<XsdElement>> groupElements = new HashMap<>();

        if (complexChildElement instanceof XsdGroup){
            groupElements.put(((XsdGroup) complexChildElement).getName(), complexChildElement.getXsdElements().map(element -> (XsdElement) element));
        }

        if (complexChildElement instanceof XsdMultipleElements){
            groupElements = ((XsdMultipleElements) complexChildElement).getGroupElements();
        }

        storeInterfaceInformation(groupElements);

        return groupElements.keySet()
                            .stream()
                            .map(groupElement -> getInterfaceName(groupElement))
                            .toArray(String[]::new);
    }

    /**
     * This method will populate the elementGroupInterfaces field with all the interface information
     * that will be obtained while creating the classes in order to create all the required interfaces
     * afterwards.
     * @param groupElements The Map containing the information about interfaces from a given element.
     */
    private void storeInterfaceInformation(Map<String, Stream<XsdElement>> groupElements) {
        groupElements.keySet().forEach((String groupName) -> {
            if (!elementGroupInterfaces.containsKey(groupName)){
                Map<String, XsdElement> mappedElements = new HashMap<>();

                groupElements.get(groupName)
                             .forEach(elementObj -> mappedElements.put(elementObj.getName(), elementObj));

                elementGroupInterfaces.put(
                        getInterfaceName(groupName),
                        mappedElements.values().stream());
            }
        });
    }

    /**
     * Returns all the concrete children of a given element. With the separation made between
     * XsdGroups children and the remaining children this method is able to return only the
     * children that are not shared in any interface.
     * @param element The element from which the children will be obtained.
     * @return The children that are exclusive to the current element.
     */
    private Stream<XsdElement> getOwnChildren(XsdElement element) {
        if (element.getXsdComplexType() != null){
            XsdAbstractElement childElement = element.getXsdComplexType().getXsdChildElement();

            if (childElement != null) {
                Map<String, XsdElement> mappedElements = new HashMap<>();

                childElement
                        .getXsdElements()
                        .filter(referenceBase -> !(referenceBase.getParent() instanceof XsdGroup))
                        .map(referenceBase -> (XsdElement) referenceBase)
                        .forEach(elementObj -> mappedElements.put(elementObj.getName(), elementObj));

                return mappedElements.values().stream();
            }
        }

        return Stream.empty();
    }

    /**
     * Obtains the names of the attribute interfaces that the given element will implement.
     * @param element The element that contains the attributes.
     * @return The elements interfaces names.
     */
    private String[] getAttributeGroupInterfaces(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            List<XsdAttributeGroup> attributeGroups = complexType.getXsdAttributes()
                                                                .filter(attribute -> attribute.getParent() instanceof XsdAttributeGroup)
                                                                .map(attribute -> (XsdAttributeGroup) attribute.getParent())
                                                                .distinct()
                                                                .collect(Collectors.toList());

            attributeGroups.addAll(complexType.getXsdAttributeGroup());

            attributeGroups.stream().distinct().forEach(this::addAttributeGroup);

            if (!attributeGroups.isEmpty()){
                return getBaseAttributeGroupInterface(complexType.getXsdAttributeGroup());
            }
        }

        return new String[0];
    }

    /**
     * Recursively iterates in parents of attributes in order to try finding a common attribute group.
     * @param attributeGroups The attributeGroups contained in the element.
     * @return The elements super class name.
     */
    private String[] getBaseAttributeGroupInterface(List<XsdAttributeGroup> attributeGroups){
        List<XsdAttributeGroup> parents = new ArrayList<>();

        attributeGroups.forEach(attributeGroup -> {
            XsdAttributeGroup parent = (XsdAttributeGroup) attributeGroup.getParent();

            if (!parents.contains(parent) && parent != null){
                parents.add(parent);
            }
        });

        if (attributeGroups.size() == 1){
            return new String[]{ getInterfaceName(toCamelCase(attributeGroups.get(0).getName())) };
        }

        if (parents.size() == 0){
            return attributeGroups.stream()
                              .map(baseClass -> getInterfaceName(toCamelCase(baseClass.getName())))
                              .toArray(String[]::new);
        }

        return getBaseAttributeGroupInterface(parents);
    }

    /**
     * Obtains all the interfaces that a given element will implement.
     * @param element The element in which the class will be based.
     * @return A string array with all the interface names.
     */
    private String[] getInterfaces(XsdElement element) {
        String[] attributeGroupInterfaces =  getAttributeGroupInterfaces(element);
        String[] elementGroupInterfaces =  getElementGroupInterfaces(element);

        return ArrayUtils.addAll(attributeGroupInterfaces, elementGroupInterfaces);
    }

    /**
     * Obtains the attributes which are specific to the given element.
     * @param element The element containing the attributes.
     * @return A list of attributes that are exclusive to the element.
     */
    private Stream<XsdAttribute> getOwnAttributes(XsdElement element){
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null) {
            return complexType.getXsdAttributes()
                                .filter(attribute -> attribute.getParent().equals(complexType));
        }

        return Stream.empty();
    }

    /**
     * Adds information about the attribute group interface to the attributeGroupInterfaces variable.
     * @param attributeGroup The attributeGroup to add.
     */
    private void addAttributeGroup(XsdAttributeGroup attributeGroup) {
        String interfaceName = getInterfaceName(attributeGroup.getName());

        if (!attributeGroupInterfaces.containsKey(interfaceName)){
            List<XsdAttribute> ownElements = attributeGroup.getXsdElements()
                    .filter(attribute -> attribute.getParent().equals(attributeGroup))
                    .map(attribute -> (XsdAttribute) attribute)
                    .collect(Collectors.toList());

            List<String> parentNames = attributeGroup.getAttributeGroups().stream().map(XsdReferenceElement::getName).collect(Collectors.toList());
            AttributeHierarchyItem attributeHierarchyItemItem = new AttributeHierarchyItem(attributeGroup.getName(), parentNames, ownElements);

            attributeGroupInterfaces.put(interfaceName, attributeHierarchyItemItem);
        }
    }

    /**
     * Obtains the signature for a class given the interface names.
     * @param interfaces The implemented interfaces.
     * @param className The class name.
     * @param apiName The api this class will belong.
     * @return The signature of the class.
     */
    private String getClassSignature(String[] interfaces, String className, String apiName) {
        StringBuilder signature = new StringBuilder("L" + ABSTRACT_ELEMENT_TYPE + "<" + getFullClassTypeNameDesc(className, apiName) + ">;");

        for (String anInterface : interfaces) {
            signature.append("L")
                     .append(getFullClassTypeName(anInterface, apiName))
                     .append("<")
                     .append(getFullClassTypeNameDesc(className, apiName))
                     .append(">;");
        }

        return signature.toString();
    }

    /** Obtains the signature for the attribute group interfaces based on the implemented interfaces.
     * @param interfaces The implemented interfaces.
     * @return The signature of this interface.
     */
    private StringBuilder getAttributeGroupSignature(String[] interfaces, String apiName) {
        StringBuilder signature;

        if (interfaces.length == 0){
            signature = new StringBuilder("<T::L" + IELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;>;");
        } else {
            signature = new StringBuilder("<T::L" + IELEMENT_TYPE + "<TT;>;>" + JAVA_OBJECT_DESC);

            for (String anInterface : interfaces) {
                signature.append("L").append(getFullClassTypeName(anInterface, apiName)).append("<TT;>;");
            }
        }

        return signature;
    }

    /**
     * Obtains an array with the names of the interfaces implemented by a attribute group interface
     * with the given parents, as in interfaces that will be extended.
     * @param parentsName The list of interfaces that this interface will extend
     * @return A string array containing the names of the interfaces that this interface will extend.
     */
    private String[] getAttributeGroupObjectInterfaces(List<String> parentsName) {
        String[] interfaces;

        if (parentsName.size() == 0){
            interfaces = new String[]{IELEMENT};
        } else {
            interfaces = new String[parentsName.size()];

            parentsName.stream().map(parentName -> getInterfaceName(toCamelCase(parentName))).collect(Collectors.toList()).toArray(interfaces);
        }

        return interfaces;
    }

}
