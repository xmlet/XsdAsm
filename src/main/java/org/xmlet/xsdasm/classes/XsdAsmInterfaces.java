package org.xmlet.xsdasm.classes;

import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.*;
import org.xmlet.xsdasm.classes.utils.AttributeHierarchyItem;
import org.xmlet.xsdasm.classes.utils.ElementHierarchyItem;
import org.xmlet.xsdasm.classes.utils.InterfaceInfo;
import org.xmlet.xsdasm.classes.utils.SequenceMethodInfo;
import org.xmlet.xsdparser.xsdelements.*;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmElements.generateMethodsForElement;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

/**
 * This class is responsible to generate all the code that is related to interfaces.
 */
class XsdAsmInterfaces {

    /**
     * The value used to differentiate between two {@link XsdAttribute} object with the same name, only differing in
     * the case sensitive aspect.
     */
    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFFERENCE = "Alt";

    /**
     * The suffix applied to all the interfaces that are hierarchy interfaces.
     */
    private static final String HIERARCHY_INTERFACES_SUFFIX = "HierarchyInterface";

    /**
     * The suffix applied to all the interfaces that are generated based on the {@link XsdSequence} object.
     */
    private static final String SEQUENCE_SUFFIX = "Sequence";

    /**
     * The suffix applied to all the interfaces that are generated based on the {@link XsdAll} object.
     */
    private static final String ALL_SUFFIX = "All";

    /**
     * The suffix applied to all the interfaces that are generated based on the {@link XsdChoice} object.
     */
    private static final String CHOICE_SUFFIX = "Choice";

    /**
     * A {@link Map} with information regarding all the interfaces generated.
     */
    private Map<String, InterfaceInfo> createdInterfaces = new HashMap<>();

    /**
     * A {@link Map} with information regarding all the elements generated.
     */
    private Map<String, XsdAbstractElement> createdElements = new HashMap<>();

    /**
     * A {@link Map} with information regarding the hierarchy of attributeGroups.
     */
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();

    /**
     * A {@link Map} with information regarding the hierarchy interfaces.
     */
    private Map<String, ElementHierarchyItem> hierarchyInterfaces = new HashMap<>();

    /**
     * An {@link XsdAsm} instance. Used to delegate element generation.
     */
    private XsdAsm xsdAsmInstance;

    XsdAsmInterfaces(XsdAsm instance) {
        this.xsdAsmInstance = instance;
    }

    /**
     * Generates all the required interfaces, based on the information gathered while creating the other classes.
     * It creates both types of interfaces:
     *  ElementGroupInterfaces - Interfaces that serve as a base to adding child elements to the current element;
     *  AttributeGroupInterfaces - Interface that serve as a base to adding attributes to the current element;
     * @param createdAttributes Information about the attributes that are already created.
     * @param apiName The name of the generated fluent interface.
     */
    void generateInterfaces(Map<String, List<XsdAttribute>> createdAttributes, String apiName) {
        attributeGroupInterfaces.keySet().forEach(attributeGroupInterface -> generateAttributesGroupInterface(createdAttributes, attributeGroupInterface, attributeGroupInterfaces.get(attributeGroupInterface), apiName));
        hierarchyInterfaces.values().forEach(hierarchyInterface -> generateHierarchyAttributeInterfaces(createdAttributes, hierarchyInterface, apiName));
    }

    /**
     * Generates all the hierarchy interfaces.
     * @param createdAttributes Information about the attributes that are already created.
     * @param hierarchyInterface Information about the hierarchy interface to create.
     * @param apiName The name of the generated fluent interface.
     */
    private void generateHierarchyAttributeInterfaces(Map<String, List<XsdAttribute>> createdAttributes, ElementHierarchyItem hierarchyInterface, String apiName) {
        String interfaceName = hierarchyInterface.getInterfaceName();
        List<String> extendedInterfaceList = hierarchyInterface.getInterfaces();
        String[] extendedInterfaces = listToArray(extendedInterfaceList, ELEMENT);

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfaces, getInterfaceSignature(extendedInterfaces, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        hierarchyInterface.getAttributes().forEach(attribute ->
                generateMethodsAndCreateAttribute(createdAttributes, classWriter, attribute, elementTypeDesc, interfaceName, apiName)
        );

        writeClassToFile(interfaceName, classWriter, apiName);
    }

    /**
     * This method obtains the element interfaces which his class will be implementing.
     * The interfaces are represented in {@link XsdAbstractElement} objects as {@link XsdGroup}, {@link XsdAll},
     * {@link XsdSequence} and {@link XsdChoice}. The respective methods of the interfaces will be the elements from the
     * types enumerated previously.
     * @param element The element from which the interfaces will be obtained.
     * @param apiName The name of the generated fluent interface.
     * @return A {@link String} array containing the names of all the interfaces this method implements.
     */
    private String[] getElementInterfaces(XsdElement element, String apiName){
        XsdAbstractElement child = getElementInterfacesElement(element);
        List<String> interfaceNames = null;

        if (child != null){
            List<InterfaceInfo> interfaceInfo = iterativeCreation(child, getCleanName(element), 0, apiName, null);
            interfaceNames = interfaceInfo.stream().map(InterfaceInfo::getInterfaceName).collect(Collectors.toList());
        }

        return listToArray(interfaceNames, TEXT_GROUP);
    }

    /**
     * Generates an attribute group interface with all the required methods. It uses the information gathered about in
     * attributeGroupInterfaces.
     * @param createdAttributes A list with the names of the attribute classes already created.
     * @param attributeGroupName The interface name.
     * @param attributeHierarchyItem An object containing information about the methods of this interface and which interface, if any,
     *                               this interface extends.
     * @param apiName The name of the generated fluent interface.
     */
    private void generateAttributesGroupInterface(Map<String, List<XsdAttribute>> createdAttributes, String attributeGroupName, AttributeHierarchyItem attributeHierarchyItem, String apiName){
        String baseClassNameCamelCase = firstToUpper(attributeGroupName);
        String[] interfaces = getAttributeGroupObjectInterfaces(attributeHierarchyItem.getParentsName());
        StringBuilder signature = getAttributeGroupSignature(interfaces, apiName);

        ClassWriter interfaceWriter = generateClass(baseClassNameCamelCase, JAVA_OBJECT, interfaces, signature.toString(), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        attributeHierarchyItem.getOwnElements().forEach(elementAttribute -> {
            if (createdAttributes.keySet().stream().anyMatch(createdAttributeName -> createdAttributeName.equalsIgnoreCase(elementAttribute.getName()))){
                elementAttribute.setName(elementAttribute.getName() + ATTRIBUTE_CASE_SENSITIVE_DIFFERENCE);
            }

            generateMethodsAndCreateAttribute(createdAttributes, interfaceWriter, elementAttribute, elementTypeDesc, baseClassNameCamelCase, apiName);
        });

        writeClassToFile(baseClassNameCamelCase, interfaceWriter, apiName);
    }

    /**
     * Obtains the names of the attribute interfaces that the given {@link XsdElement} will implement.
     * @param element The element that contains the attributes.
     * @return The elements interfaces names.
     */
    private String[] getAttributeGroupInterfaces(XsdElement element){
        List<String> attributeGroups = new ArrayList<>();
        XsdComplexType complexType = element.getXsdComplexType();
        Stream<XsdAttributeGroup> extensionAttributeGroups = Stream.empty();
        XsdExtension extension = getXsdExtension(element);

        if (complexType != null){
            if (extension != null){
                extensionAttributeGroups = extension.getXsdAttributeGroup();
            }

            attributeGroups.addAll(getTypeAttributeGroups(complexType, extensionAttributeGroups));
        }

        return listToArray(attributeGroups);
    }

    /**
     * Obtains hierarchy interface information from the received {@link XsdElement}.
     * @param element The received {@link XsdElement} object.
     * @param apiName The name of the generated fluent interface.
     * @return The names of the hierarchy interfaces.
     */
    private String[] getHierarchyInterfaces(XsdElement element, String apiName) {
        List<String> interfaceNames = new ArrayList<>();
        XsdElement base = getBaseFromElement(element);
        List<XsdAttribute> elementAttributes = getOwnAttributes(element).collect(Collectors.toList());
        List<ElementHierarchyItem> hierarchyInterfaceList = new ArrayList<>();

        while (base != null) {
            List<String> attributeNames = elementAttributes.stream().map(XsdAttribute::getName).collect(Collectors.toList());
            List<XsdAttribute> moreAttributes = getOwnAttributes(base).filter(attribute -> !attributeNames.contains(attribute.getName())).collect(Collectors.toList());
            elementAttributes.addAll(moreAttributes);

            hierarchyInterfaceList.add(new ElementHierarchyItem(base.getName() + HIERARCHY_INTERFACES_SUFFIX, moreAttributes, getInterfaces(base, apiName)));

            base = getBaseFromElement(base);
        }

        if (!hierarchyInterfaceList.isEmpty()){
            interfaceNames.add(hierarchyInterfaceList.get(0).getInterfaceName());

            hierarchyInterfaceList.forEach(item -> this.hierarchyInterfaces.put(item.getInterfaceName(), item));
        }

        return listToArray(interfaceNames);
    }

    /**
     * Obtains the attribute groups of a given element that are present in its type attribute.
     * @param complexType The {@link XsdComplexType} object with the type attribute.
     * @param extensionAttributeGroups A {@link Stream} of {@link XsdAttributeGroup} obtained from {@link XsdExtension}.
     * @return The names of the attribute groups present in the type attribute.
     */
    private Collection<String> getTypeAttributeGroups(XsdComplexType complexType, Stream<XsdAttributeGroup> extensionAttributeGroups) {
        Stream<XsdAttributeGroup> attributeGroups = complexType.getXsdAttributes()
                .filter(attribute -> attribute.getParent() instanceof XsdAttributeGroup)
                .map(attribute -> (XsdAttributeGroup) attribute.getParent())
                .distinct();

        attributeGroups = Stream.concat(attributeGroups, extensionAttributeGroups);

        attributeGroups = Stream.concat(attributeGroups, complexType.getXsdAttributeGroup());

        List<XsdAttributeGroup> attributeGroupList = attributeGroups.distinct().collect(Collectors.toList());

        attributeGroupList.forEach(this::addAttributeGroup);

        if (!attributeGroupList.isEmpty()){
            return getBaseAttributeGroupInterface(complexType.getXsdAttributeGroup().collect(Collectors.toList()));
        }

        return Collections.emptyList();
    }

    /**
     * Recursively iterates order to define an hierarchy on the attribute group interfaces.
     * @param attributeGroups The attributeGroups contained in the element.
     * @return A {@link List} of attribute group interface names.
     */
    private List<String> getBaseAttributeGroupInterface(List<XsdAttributeGroup> attributeGroups){
        List<XsdAttributeGroup> parents = new ArrayList<>();

        attributeGroups.forEach(attributeGroup -> {
            XsdAbstractElement parent = attributeGroup.getParent();

            if (parent instanceof XsdAttributeGroup && !parents.contains(parent)){
                parents.add((XsdAttributeGroup) parent);
            }
        });

        if (attributeGroups.size() == 1 || parents.isEmpty()){
            return attributeGroups.stream().map(attributeGroup -> firstToUpper(attributeGroup.getName())).collect(Collectors.toList());
        }

        return getBaseAttributeGroupInterface(parents);
    }

    /**
     * Adds information about the attribute group interface to the attributeGroupInterfaces variable.
     * @param attributeGroup The attributeGroup to add.
     */
    private void addAttributeGroup(XsdAttributeGroup attributeGroup) {
        String interfaceName = firstToUpper(attributeGroup.getName());

        if (!attributeGroupInterfaces.containsKey(interfaceName)){
            List<XsdAttribute> ownElements = attributeGroup.getXsdElements()
                    .filter(attribute -> attribute.getParent().equals(attributeGroup))
                    .map(attribute -> (XsdAttribute) attribute)
                    .collect(Collectors.toList());

            List<String> parentNames = attributeGroup.getAttributeGroups().stream().map(XsdNamedElements::getName).collect(Collectors.toList());
            AttributeHierarchyItem attributeHierarchyItemItem = new AttributeHierarchyItem(parentNames, ownElements);

            attributeGroupInterfaces.put(interfaceName, attributeHierarchyItemItem);
        }
    }

    /** Obtains the signature for the attribute group interfaces based on the implemented interfaces.
     * @param interfaces The implemented interfaces.
     * @return The signature of this interface.
     */
    private StringBuilder getAttributeGroupSignature(String[] interfaces, String apiName) {
        StringBuilder signature = new StringBuilder("<T::L" + elementType + "<TT;TZ;>;Z::" + elementTypeDesc + ">" + JAVA_OBJECT_DESC);

        if (interfaces.length == 0){
            signature.append("L").append(elementType).append("<TT;TZ;>;");
        } else {
            for (String anInterface : interfaces) {
                signature.append("L").append(getFullClassTypeName(anInterface, apiName)).append("<TT;TZ;>;");
            }
        }

        return signature;
    }

    /**
     * Obtains an array with the names of the interfaces implemented by a attribute group interface
     * with the given parents, as in interfaces that will be extended.
     * @param parentsName The list of interfaces that this interface will extend.
     * @return A {@link String} array containing the names of the interfaces that this interface will extend.
     */
    private String[] getAttributeGroupObjectInterfaces(List<String> parentsName) {
        return listToArray(parentsName.stream().map(XsdAsmUtils::firstToUpper).collect(Collectors.toList()), ELEMENT);
    }

    /**
     * Obtains all the interfaces that a given element will implement.
     * @param element The {@link XsdElement} in which the class will be based.
     * @param apiName The name of the generated fluent interface.
     * @return A {@link String} array with all the interface names.
     */
    String[] getInterfaces(XsdElement element, String apiName) {
        String[] attributeGroupInterfacesArr = getAttributeGroupInterfaces(element);
        String[] elementGroupInterfacesArr = getElementInterfaces(element, apiName);
        String[] hierarchyInterfacesArr = getHierarchyInterfaces(element, apiName);

        return ArrayUtils.addAll(ArrayUtils.addAll(attributeGroupInterfacesArr, elementGroupInterfacesArr), hierarchyInterfacesArr);
    }

    /**
     * Delegates the interface generation to one of the possible {@link XsdGroup} element children.
     * @param groupName The group name of the {@link XsdGroup} element.
     * @param choiceElement The child {@link XsdChoice}.
     * @param allElement The child {@link XsdAll}.
     * @param sequenceElement The child {@link XsdSequence}.
     * @param className The className of the element which contains this group.
     * @param interfaceIndex The current interface index that serves as a base to distinguish interface names.
     * @param apiName The name of the generated fluent interface.
     * @return A {@link InterfaceInfo} object containing relevant interface information.
     */
    private InterfaceInfo groupMethod(String groupName, XsdChoice choiceElement, XsdAll allElement, XsdSequence sequenceElement, String className, int interfaceIndex, String apiName){
        if (allElement != null) {
            return iterativeCreation(allElement, className, interfaceIndex + 1, apiName, groupName).get(0);
        }

        if (choiceElement != null) {
            return iterativeCreation(choiceElement, className, interfaceIndex + 1, apiName, groupName).get(0);
        }

        if (sequenceElement != null) {
            return iterativeCreation(sequenceElement, className, interfaceIndex + 1, apiName, groupName).get(0);
        }

        return new InterfaceInfo(TEXT_GROUP);
    }

    /**
     * Generates an interface based on a XsdSequence element.
     * @param xsdElements The elements, ordered, that represent the sequence.
     * @param className The className of the element which contains this sequence.
     * @param interfaceIndex The current interfaceIndex that serves as a base to distinguish interface names.
     * @param apiName The name of the API to be generated.
     * @param groupName The groupName, that indicates if this sequence belongs to a group.
     * @return A pair with the key being the name of the created sequence interface and the current interface index after the creation of the interface and its dependant interfaces.
     */
    private InterfaceInfo sequenceMethod(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, String apiName, String groupName) {
        String interfaceNameBase = groupName != null ? firstToUpper(groupName + SEQUENCE_SUFFIX) : className + SEQUENCE_SUFFIX;
        String interfaceName = interfaceNameBase + interfaceIndex;

        if (createdInterfaces.containsKey(interfaceName)){
            return createdInterfaces.get(interfaceName);
        }

        return createSequenceInterface(xsdElements, interfaceNameBase, interfaceName, className, interfaceIndex, apiName, groupName);
    }

    /**
     * Obtains sequence information and creates all the required classes and methods.
     * @param xsdElements A {@link Stream} of {@link XsdElement}, ordered, that represent the sequence.
     * @param className The className of the element which contains this sequence.
     * @param interfaceIndex The current interfaceIndex that serves as a base to distinguish interface names.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The groupName, that indicates if this sequence belongs to a group.
     */
    private InterfaceInfo createSequenceInterface(Stream<XsdAbstractElement> xsdElements, String interfaceNameBase, String interfaceName, String className, int interfaceIndex, String apiName, String groupName) {
        SequenceMethodInfo sequenceInfo = getSequenceInfo(xsdElements, className, interfaceIndex, 0, apiName, groupName);
        List<XsdAbstractElement> sequenceList = sequenceInfo.getSequenceElements();
        List<String> sequenceNames = sequenceInfo.getSequenceElementNames();

        for (int i = 0; i < sequenceList.size(); i++) {
            XsdAbstractElement sequenceElement = sequenceList.get(i);
            boolean isLast = i == sequenceList.size() - 1;
            boolean isFirst = i == 0;
            String sequenceName = firstToLower(getCleanName(sequenceNames.get(i)));
            String nextTypeName = getNextTypeName(className, groupName, sequenceName, isLast);
            String currentInterfaceName = interfaceNameBase + interfaceIndex;
            String[] interfaces = new String[] {TEXT_GROUP};

            ClassWriter classWriter = generateClass(currentInterfaceName, JAVA_OBJECT, interfaces, getInterfaceSignature(interfaces, apiName), ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT, apiName);

            boolean isElement = sequenceElement instanceof XsdElement;

            if (isElement){
                createElementsForSequence(classWriter, sequenceName, nextTypeName, apiName, className, isFirst, currentInterfaceName, (XsdElement) sequenceElement);
            }

            boolean isGroupChoiceAll = sequenceElement instanceof XsdGroup || sequenceElement instanceof XsdChoice || sequenceElement instanceof XsdAll;

            if (isGroupChoiceAll){
                List<XsdElement> elements = getAllElementsRecursively(sequenceElement);

                createElementsForSequence(classWriter, nextTypeName, apiName, className, isFirst, currentInterfaceName, elements);
            }

            if (isElement || isGroupChoiceAll){
                writeClassToFile(currentInterfaceName, classWriter, apiName);

                if (!isLast || groupName == null){
                    generateSequenceInnerElement(className, nextTypeName, isLast, apiName, interfaceNameBase, interfaceIndex, groupName);
                }
            }

            ++interfaceIndex;
        }

        return new InterfaceInfo(interfaceName, interfaceIndex, sequenceNames.subList(0, 1));
    }

    /** Obtains the name of the next type of the sequence.
     * @param className The name of the class which contains the sequence.
     * @param groupName The groupName of this sequence, if any.
     * @param sequenceName The sequence name.
     * @param isLast Indication if the next type will be the last of the sequence.
     * @return The next sequence type name.
     */
    private String getNextTypeName(String className, String groupName, String sequenceName, boolean isLast){
        if (isLast)
            return groupName == null ? className + "Complete" : className;
        else
            return className + firstToUpper(sequenceName);
    }

    /**
     * This method adds a method to a previously created Sequence interface and creates the subsequent types.
     * <xs:element name="personInfo">
     *      <xs:complexType>
     *          <xs:sequence>
     *              <xs:element name="firstName" type="xs:string"/>
     *              (...)
     * In this case this method will do the following:
     *  Receives the interface PersonInfoSequence0
     *  Adds the method firstName to that interface and writes the interface to disc.
     *  This method will add a FirstName element to the parent, so the FirstName will need to be created.
     *  That method will return PersonInfoFirstName to support the next element in the sequence, so the PersonInfoFirstName will need to be created.
     * @param classWriter The classWriter of the current sequence interface.
     * @param sequenceName The name of the sequence element. Following the above example that should be firstName.
     * @param apiName The name of the generated fluent interface.
     * @param className The className of the element that will implement the sequence. Following the above example that should be PersonInfo.
     * @param sequenceElement The element that serves as base to create this interface.
     */
    private void createElementsForSequence(ClassWriter classWriter, String sequenceName, String nextTypeName, String apiName, String className, boolean isFirst, String currentInterfaceName, XsdElement sequenceElement) {
        String addingChildName = firstToUpper(sequenceName);

        generateSequenceMethod(classWriter, getJavaType(sequenceElement.getType()), addingChildName, nextTypeName, className, apiName, sequenceName, currentInterfaceName, isFirst);

        createElement(sequenceElement, apiName);
    }

    /**
     * @param classWriter classWriter The classWriter of the current sequence interface.
     * @param nextTypeName The name of the next type to return.
     * @param apiName The name of the generated fluent interface.
     * @param className The className of the element that will implement the sequence.
     * @param sequenceElements The elements that serves as base to create this interface.
     */
    private void createElementsForSequence(ClassWriter classWriter, String nextTypeName, String apiName, String className, boolean isFirst, String currentInterfaceName, List<XsdElement> sequenceElements) {
        sequenceElements.forEach(sequenceElement ->
                generateSequenceMethod(classWriter, getJavaType(sequenceElement.getType()), getCleanName(sequenceElement.getName()), nextTypeName, className, apiName, sequenceElement.getName(), currentInterfaceName, isFirst));

        sequenceElements.forEach(element -> createElement(element, apiName));
    }

    /**
     * Generates an intermediate type to enforce the sequence order.
     * @param className The className of the element that will implement the sequence.
     * @param typeName The name of the element to create.
     * @param isLast Indicates if the element to be created is the last of the sequence.
     * @param apiName The name of the API to be generated.
     * @param interfaceNameBase A base name of the sequence interfaces.
     * @param interfaceIndex The current interface index.
     * @param groupName The group name of the group that contains this sequence, if any.
     */
    private void generateSequenceInnerElement(String className, String typeName, boolean isLast, String apiName, String interfaceNameBase, int interfaceIndex, String groupName) {
        String[] nextTypeInterfaces = new String[]{ interfaceNameBase + (interfaceIndex + 1)};

        if (isLast){
            nextTypeInterfaces = groupName != null ? new String[] {firstToUpper(groupName)} : null;
        }

        ClassWriter classWriter = generateClass(typeName, abstractElementType, nextTypeInterfaces, getClassSignature(nextTypeInterfaces, typeName, apiName), ACC_PUBLIC + ACC_SUPER, apiName);

        XsdAsmElements.generateClassSpecificMethods(classWriter, typeName, className, apiName);

        writeClassToFile(typeName, classWriter, apiName);
    }

    /**
     * <xs:element name="personInfo">
     <xs:complexType>
     <xs:sequence>
     <xs:element name="firstName" type="xs:string"/>
     (...)
     * Generates the method present in the sequence interface for a sequence element.
     * @param classWriter The classWriter of the sequence interface.
     * @param addingChildName The name of the child to be added. Based in the example above, it would be firstName.
     * @param nextTypeName The name of the next type, which would be PersonInfoFirstName based on the above example.
     * @param className The name of the class that contains the sequence.
     * @param apiName The name of the API to be generated.
     * @param sequenceName The name of the sequence element. Based on the above example, it would be firstName.
     * @param currentInterfaceName The name of the current interface name.
     */
    private void generateSequenceMethod(ClassWriter classWriter, String javaType, String addingChildName, String nextTypeName, String className, String apiName, String sequenceName, String currentInterfaceName, boolean isFirst) {
        String addingType = getFullClassTypeName(addingChildName, apiName);
        String interfaceType = getFullClassTypeName(currentInterfaceName, apiName);
        String nextType = getFullClassTypeName(nextTypeName, apiName);
        String nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);

        boolean allowsMultipleSequences = className.equals(nextTypeName);

        javaType = javaType == null ? JAVA_STRING_DESC : javaType;

        if (allowsMultipleSequences){
            reusableSequenceMethod(classWriter, javaType, sequenceName, interfaceType, addingType);
        } else {
            if (isFirst){
                firstRegularSequenceMethod(classWriter, javaType, className, sequenceName, interfaceType, addingType, nextType, nextTypeDesc);
            } else {
                remainingRegularSequenceMethod(classWriter, javaType, className, sequenceName, interfaceType, addingType, nextType, nextTypeDesc);
            }
        }
    }

    private void reusableSequenceMethod(ClassWriter classWriter, String javaType, String sequenceName, String interfaceType, String addingType) {
        String methodName = firstToLower(getCleanName(sequenceName));

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, methodName, "(" + javaType + ")" + elementTypeDesc, "(" + javaType + ")TZ;", null);
        mVisitor.visitLocalVariable(methodName, javaType, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "__", "()" + elementTypeDesc, true);
        mVisitor.visitTypeInsn(NEW, addingType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, addingType, CONSTRUCTOR, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "text", "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, false);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "__", "()" + elementTypeDesc, true);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 2);
        mVisitor.visitEnd();
    }

    private void firstRegularSequenceMethod(ClassWriter classWriter, String javaType, String className, String sequenceName, String interfaceType, String addingType, String nextType, String nextTypeDesc) {
        String methodName = firstToLower(getCleanName(sequenceName));

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, methodName, "(" + javaType + ")" + nextTypeDesc, "(" + javaType + ")L" + nextType + "<TT;>;", null);
        mVisitor.visitLocalVariable(methodName, javaType, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, addingType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, addingType, CONSTRUCTOR, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "text", "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, false);
        mVisitor.visitTypeInsn(CHECKCAST, addingType);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, true);
        mVisitor.visitInsn(POP);
        mVisitor.visitTypeInsn(NEW, nextType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitLdcInsn(firstToLower(getCleanName(className)));
        mVisitor.visitMethodInsn(INVOKESPECIAL, nextType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "getChildren", "()" + JAVA_LIST_DESC, true);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Objects", "requireNonNull", "(" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
        mVisitor.visitInsn(POP);
        mVisitor.visitInvokeDynamicInsn("accept", "(" + nextTypeDesc + ")Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKEVIRTUAL, abstractElementType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, false), Type.getType("(" + elementTypeDesc + ")V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();
    }

    private void remainingRegularSequenceMethod(ClassWriter classWriter, String javaType, String className, String sequenceName, String interfaceType, String addingType, String nextType, String nextTypeDesc) {
        String methodName = firstToLower(getCleanName(sequenceName));

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, methodName, "(" + javaType + ")" + nextTypeDesc, "(" + javaType + ")L" + nextType + "<TZ;>;", null);
        mVisitor.visitLocalVariable(methodName, javaType, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "__", "()" + elementTypeDesc, true);
        mVisitor.visitTypeInsn(NEW, addingType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, addingType, CONSTRUCTOR, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "text", "(" + JAVA_OBJECT_DESC + ")" + elementTypeDesc, false);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, true);
        mVisitor.visitInsn(POP);
        mVisitor.visitTypeInsn(NEW, nextType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "__", "()" + elementTypeDesc, true);
        mVisitor.visitLdcInsn(firstToLower(getCleanName(className)));
        mVisitor.visitMethodInsn(INVOKESPECIAL, nextType, CONSTRUCTOR, "(" + elementTypeDesc + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "__", "()" + elementTypeDesc, true);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "getChildren", "()" + JAVA_LIST_DESC, true);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKESTATIC, "java/util/Objects", "requireNonNull", "(" + JAVA_OBJECT_DESC + ")" + JAVA_OBJECT_DESC, false);
        mVisitor.visitInsn(POP);
        mVisitor.visitInvokeDynamicInsn("accept", "(" + nextTypeDesc + ")Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKEVIRTUAL, abstractElementType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, false), Type.getType("(" + elementTypeDesc + ")V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, JAVA_LIST, "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();
    }

    /**
     * Obtains information about all the members that make up the sequence.
     * @param xsdElements The members that make the sequence.
     * @param className The name of the element that this sequence belongs to.
     * @param interfaceIndex The current interface index.
     * @param unnamedIndex A special index for elements that have no name, which will help distinguish them.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The group name of the group that contains this sequence, if any.
     * @return A {@link SequenceMethodInfo} object which contains relevant information regarding sequence methods.
     */
    private SequenceMethodInfo getSequenceInfo(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, int unnamedIndex, String apiName, String groupName){
        List<XsdAbstractElement> xsdElementsList = xsdElements.collect(Collectors.toList());
        SequenceMethodInfo sequenceMethodInfo = new SequenceMethodInfo(xsdElementsList.stream().filter(element -> !(element instanceof XsdSequence)).collect(Collectors.toList()), interfaceIndex, unnamedIndex);

        for (XsdAbstractElement element : xsdElementsList) {
            if (element instanceof XsdElement){
                String elementName = ((XsdElement) element).getName();

                if (elementName != null){
                    sequenceMethodInfo.addElementName(elementName);
                } else {
                    sequenceMethodInfo.addElementName(className + "SequenceUnnamed" + sequenceMethodInfo.getUnnamedIndex());
                    sequenceMethodInfo.incrementUnnamedIndex();
                }
            } else {
                if (element instanceof XsdSequence){
                    sequenceMethodInfo.receiveChildSequence(getSequenceInfo(element.getXsdElements(), className, interfaceIndex, unnamedIndex, apiName, groupName));
                } else {
                    InterfaceInfo interfaceInfo = iterativeCreation(element, className, interfaceIndex + 1, apiName, groupName).get(0);

                    sequenceMethodInfo.setInterfaceIndex(interfaceInfo.getInterfaceIndex());
                    sequenceMethodInfo.addElementName(interfaceInfo.getInterfaceName());
                }
            }
        }

        return sequenceMethodInfo;
    }

    /**
     * Generates an interface based on a {@link XsdAll} element.
     * @param directElements The direct elements of the {@link XsdAll} element. Each one will be represented as a method
     *                       in the all interface.
     * @param className The name of the class that contains the {@link XsdAll}.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The name of the group which contains the {@link XsdAll} element, if any.
     * @return A {@link InterfaceInfo} object containing relevant interface information.
     */
    private InterfaceInfo allMethod(List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        String interfaceName = groupName != null ? groupName : className;
        String interfaceFullName = firstToUpper(interfaceName + ALL_SUFFIX + interfaceIndex);

        if (createdInterfaces.containsKey(interfaceName)){
            return createdInterfaces.get(interfaceName);
        }

        return createAllInterface(interfaceFullName, directElements, interfaceIndex, apiName);
    }

    /**
     * Creates the interface based on the information present in the {@link XsdAll} objects.
     * @param interfaceName The interface name.
     * @param directElements The direct elements of the {@link XsdAll} element. Each one will be represented as a method
     *                       in the all interface.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @return A {@link InterfaceInfo} object containing relevant interface information.
     */
    private InterfaceInfo createAllInterface(String interfaceName, List<XsdElement> directElements, int interfaceIndex, String apiName) {
        String[] extendedInterfacesArr = new String[]{TEXT_GROUP};

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        directElements.forEach(child -> {
            generateMethodsForElement(classWriter, child, getFullClassTypeName(interfaceName, apiName), elementTypeDesc, true, apiName);
            createElement(child, apiName);
        });

        writeClassToFile(interfaceName, classWriter, apiName);

        List<String> methodNames = directElements.stream().map(XsdElement::getName).collect(Collectors.toList());

        return new InterfaceInfo(interfaceName, interfaceIndex, methodNames);
    }

    /**
     * Generates an interface based on a {@link XsdChoice} element.
     * @param groupElements The contained groupElements.
     * @param directElements The direct elements of the {@link XsdChoice} element.
     * @param className The name of the class that contains the {@link XsdChoice} element.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The name of the group in which this {@link XsdChoice} element is contained, if any.
     * @return A {@link List} of {@link InterfaceInfo} objects containing relevant interface information.
     */
    private List<InterfaceInfo> choiceMethod(List<XsdGroup> groupElements, List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();
        String interfaceName;

        if (groupName != null){
            interfaceName = firstToUpper(groupName + CHOICE_SUFFIX);
        } else {
            interfaceName = className + CHOICE_SUFFIX + interfaceIndex;
        }

        if (createdInterfaces.containsKey(interfaceName)){
            interfaceInfoList.add(createdInterfaces.get(interfaceName));
            return interfaceInfoList;
        }

        return createChoiceInterface(groupElements, directElements, interfaceName, className, groupName, interfaceIndex, apiName);
    }

    /**
     * Generates an interface based on a {@link XsdChoice} element.
     * @param groupElements The contained groupElements.
     * @param directElements The direct elements of the {@link XsdChoice} element.
     * @param interfaceName The choice interface name.
     * @param className The name of the class that contains the {@link XsdChoice} element.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The name of the group in which this {@link XsdChoice} element is contained, if any.
     * @return A {@link List} of {@link InterfaceInfo} objects containing relevant interface information.
     */
    private List<InterfaceInfo> createChoiceInterface(List<XsdGroup> groupElements, List<XsdElement> directElements, String interfaceName, String className, String groupName, int interfaceIndex, String apiName) {
        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();
        List<String> extendedInterfaces = new ArrayList<>();

        for (XsdGroup groupElement : groupElements) {
            InterfaceInfo interfaceInfo = iterativeCreation(groupElement, className, interfaceIndex + 1, apiName, groupName).get(0);

            interfaceIndex = interfaceInfo.getInterfaceIndex();
            extendedInterfaces.add(interfaceInfo.getInterfaceName());
            interfaceInfoList.add(interfaceInfo);
        }

        Set<String> ambiguousMethods = getAmbiguousMethods(interfaceInfoList);

        if (ambiguousMethods.isEmpty() && directElements.isEmpty()){
            return interfaceInfoList;
        }

        String[] extendedInterfacesArr = listToArray(extendedInterfaces, TEXT_GROUP);

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        String interfaceType = getFullClassTypeName(interfaceName, apiName);

        directElements.forEach(child -> {
            XsdAsmElements.generateMethodsForElement(classWriter, child, interfaceType, elementTypeDesc, true, apiName);
            createElement(child, apiName);
        });

        ambiguousMethods.forEach(ambiguousMethodName ->
                XsdAsmElements.generateMethodsForElement(classWriter, ambiguousMethodName, interfaceType, elementTypeDesc, apiName, new String[]{"Ljava/lang/Override;"})
        );

        writeClassToFile(interfaceName, classWriter, apiName);

        List<InterfaceInfo> choiceInterface = new ArrayList<>();

        choiceInterface.add(new InterfaceInfo(interfaceName, interfaceIndex, directElements.stream().map(XsdElement::getName).collect(Collectors.toList()), interfaceInfoList));

        return choiceInterface;
    }

    /**
     * This method functions as an iterative process for interface creation.
     * @param element The element which could have more interface information.
     * @param className The name of the class which contains the interfaces.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the generated fluent interface.
     * @param groupName The name of the group in which this {@link XsdChoice} element is contained, if any.
     * @return A {@link List} of {@link InterfaceInfo} objects containing relevant interface information.
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<InterfaceInfo> iterativeCreation(XsdAbstractElement element, String className, int interfaceIndex, String apiName, String groupName){
        List<XsdChoice> choiceElements = new ArrayList<>();
        List<XsdGroup> groupElements = new ArrayList<>();
        List<XsdAll> allElements = new ArrayList<>();
        List<XsdSequence> sequenceElements = new ArrayList<>();
        List<XsdElement> directElements = new ArrayList<>();

        Map<Class, List> mapper = new HashMap<>();

        mapper.put(XsdGroup.class, groupElements);
        mapper.put(XsdChoice.class, choiceElements);
        mapper.put(XsdAll.class, allElements);
        mapper.put(XsdSequence.class, sequenceElements);
        mapper.put(XsdElement.class, directElements);

        //noinspection unchecked
        element.getXsdElements()
                .forEach(elementChild ->
                        mapper.get(elementChild.getClass()).add(elementChild)
                );

        List<InterfaceInfo> interfaceInfoList = new ArrayList<>();

        if (element instanceof XsdGroup){
            XsdChoice choiceElement = choiceElements.size() == 1 ? choiceElements.get(0) : null;
            XsdSequence sequenceElement = sequenceElements.size() == 1 ? sequenceElements.get(0) : null;
            XsdAll allElement = allElements.size() == 1 ? allElements.get(0) : null;

            interfaceInfoList.add(groupMethod(((XsdGroup) element).getName(), choiceElement, allElement, sequenceElement, className, interfaceIndex, apiName));
        }

        if (element instanceof XsdAll){
            interfaceInfoList.add(allMethod(directElements, className, interfaceIndex, apiName, groupName));
        }

        if (element instanceof XsdChoice){
            interfaceInfoList.addAll(choiceMethod(groupElements, directElements, className, interfaceIndex, apiName, groupName));
        }

        if (element instanceof  XsdSequence){
            interfaceInfoList.add(sequenceMethod(element.getXsdElements(), className, interfaceIndex, apiName, groupName));
        }

        if (interfaceInfoList.isEmpty()){
            throw new InvalidParameterException("Invalid element interface type.");
        }

        interfaceInfoList.forEach(interfaceInfo -> createdInterfaces.put(interfaceInfo.getInterfaceName(), interfaceInfo));

        return interfaceInfoList;
    }

    /**
     * Creates a class based on a {@link XsdElement} if it wasn't been already.
     * @param element The element that serves as base to creating the class.
     * @param apiName The name of the generated fluent interface.
     */
    private void createElement(XsdElement element, String apiName) {
        String elementName = element.getName();

        if (!createdElements.containsKey(elementName)){
            createdElements.put(elementName, element);
            xsdAsmInstance.generateClassFromElement(element, apiName);
        }
    }

    /**
     * Iterates in a given {@link XsdAbstractElement} object in order to obtain all the contained {@link XsdElement} objects.
     * @param element The element to iterate on.
     * @return All the {@link XsdElement} objects contained in the received element.
     */
    private List<XsdElement> getAllElementsRecursively(XsdAbstractElement element) {
        List<XsdElement> allGroupElements = new ArrayList<>();
        List<XsdAbstractElement> directElements = element.getXsdElements().collect(Collectors.toList());

        allGroupElements.addAll(
                directElements.stream()
                        .filter(directElement -> directElement instanceof XsdElement)
                        .map(directElement -> (XsdElement) directElement)
                        .collect(Collectors.toList()));

        for (XsdAbstractElement directElement : directElements) {
            if ((directElement instanceof XsdMultipleElements || directElement instanceof XsdGroup) && directElement.getXsdElements() != null) {
                allGroupElements.addAll(getAllElementsRecursively(directElement));
            }
        }

        return allGroupElements;
    }

    /**
     * @return Obtains all the names of the elements that were created in the current execution.
     */
    Set<String> getExtraElementsForVisitor() {
        return createdElements.keySet();
    }

    /**
     * Adds the received {@link XsdElement} to the list of created elements.
     * @param element The received {@link XsdElement}.
     */
    private void addCreatedElement(XsdElement element){
        createdElements.put(element.getName(), element);
    }

    /**
     * Adds a {@link List} of {@link XsdElement} to the list of created elements.
     * @param elementList The {@link List} to add.
     */
    void addCreatedElements(List<XsdElement> elementList) {
        elementList.forEach(this::addCreatedElement);
    }

}