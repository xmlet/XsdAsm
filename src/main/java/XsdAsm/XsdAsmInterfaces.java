package XsdAsm;

import XsdElements.*;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static XsdAsm.XsdAsmElements.generateMethodsForElement;
import static XsdAsm.XsdAsmUtils.*;
import static XsdAsm.XsdSupportingStructure.*;
import static org.objectweb.asm.Opcodes.*;

class XsdAsmInterfaces {

    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFERENCE = "Alt";

    private Map<String, Stream<XsdElement>> elementGroupInterfaces = new HashMap<>();
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();

    /**
     * Generates all the required interfaces, based on the information gathered while
     * creating the other classes. It creates both types of interfaces:
     * ElementGroupInterfaces - Interfaces that serve as a base to adding child elements to the current element;
     * AttributeGroupInterfaces - Interface that serve as a base to adding attributes to the current element;
     * @param createdAttributes A list with the names of the attribute classes already created.
     * @param apiName The api this class will belong.
     */
    void generateInterfaces(List<String> createdAttributes, String apiName) {
        elementGroupInterfaces.keySet().forEach(interfaceName -> generateElementGroupInterface(interfaceName, apiName));

        attributeGroupInterfaces.keySet().forEach(attributeGroupInterface -> generateAttributesGroupInterface(createdAttributes, attributeGroupInterface, attributeGroupInterfaces.get(attributeGroupInterface), apiName));
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
     * Generates a interface with all the required methods. It uses the information gathered about in attributeGroupInterfaces.
     * @param createdAttributes A list with the names of the attribute classes already created.
     * @param attributeGroupName The interface name.
     * @param attributeHierarchyItem An object containing information about the methods of this interface and which interface, if any,
     *                               this interface extends.
     * @param apiName The api this class will belong.
     */
    private void generateAttributesGroupInterface(List<String> createdAttributes, String attributeGroupName, AttributeHierarchyItem attributeHierarchyItem, String apiName){
        String baseClassNameCamelCase = toCamelCase(attributeGroupName);
        String[] interfaces = getAttributeGroupObjectInterfaces(attributeHierarchyItem.getParentsName());
        StringBuilder signature = getAttributeGroupSignature(interfaces, apiName);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, interfaces, signature.toString(), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        attributeHierarchyItem.getOwnElements().forEach(elementAttribute -> {
            if (createdAttributes.stream().anyMatch(createdAttributeName -> createdAttributeName.equalsIgnoreCase(elementAttribute.getName()))){
                elementAttribute.setName(elementAttribute.getName() + ATTRIBUTE_CASE_SENSITIVE_DIFERENCE);
            }

            generateMethodsAndCreateAttribute(createdAttributes, interfaceWriter, elementAttribute, IELEMENT_TYPE_DESC, apiName);
        });

        writeClassToFile(baseClassNameCamelCase, interfaceWriter, apiName);
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
     * Obtains all the interfaces that a given element will implement.
     * @param element The element in which the class will be based.
     * @return A string array with all the interface names.
     */
    String[] getInterfaces(XsdElement element) {
        String[] attributeGroupInterfaces =  getAttributeGroupInterfaces(element);
        String[] elementGroupInterfaces =  getElementGroupInterfaces(element);

        return ArrayUtils.addAll(attributeGroupInterfaces, elementGroupInterfaces);
    }
}
