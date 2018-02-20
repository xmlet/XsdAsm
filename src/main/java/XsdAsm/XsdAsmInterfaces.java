package XsdAsm;

import XsdElements.*;
import javafx.util.Pair;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static XsdAsm.XsdAsmElements.generateMethodsForElement;
import static XsdAsm.XsdAsmUtils.*;
import static XsdAsm.XsdSupportingStructure.*;
import static org.objectweb.asm.Opcodes.*;

class XsdAsmInterfaces {

    private static final String ATTRIBUTE_CASE_SENSITIVE_DIFERENCE = "Alt";

    private List<String> createdInterfaces = new ArrayList<>();
    private Map<String, XsdAbstractElement> createdElements = new HashMap<>();
    private Map<String, AttributeHierarchyItem> attributeGroupInterfaces = new HashMap<>();
    private XsdAsm xsdAsmInstance;

    XsdAsmInterfaces(XsdAsm instance) {
        this.xsdAsmInstance = instance;
    }

    /**
     * Generates all the required interfaces, based on the information gathered while
     * creating the other classes. It creates both types of interfaces:
     * ElementGroupInterfaces - Interfaces that serve as a base to adding child elements to the current element;
     * AttributeGroupInterfaces - Interface that serve as a base to adding attributes to the current element;
     * @param createdAttributes A list with the names of the attribute classes already created.
     * @param apiName The api this class will belong.
     */
    void generateInterfaces(List<String> createdAttributes, String apiName) {
        attributeGroupInterfaces.keySet().forEach(attributeGroupInterface -> generateAttributesGroupInterface(createdAttributes, attributeGroupInterface, attributeGroupInterfaces.get(attributeGroupInterface), apiName));
    }

    /**
     * This method obtains the element interfaces which his class will be implementing.
     * The interfaces are represented in XsdAbstractElements as XsdGroups, XsdAll, XsdSequence and XsdChoice.
     * The respective methods of the interfaces will be the elements from the given XsdGroup, XsdAll, XsdSequence and XsdChoice.
     * @param element The element from which the interfaces will be obtained.
     * @return A string array containing the names of all the interfaces this method implements in
     * interface-like names, e.g. flowContent will be IFlowContent.
     */
    private String[] getElementInterfaces(XsdElement element, String apiName){
        String[] interfaces = new String[0];
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null){
            interfaces = getElementInterfaces(complexType, apiName);
        }

        if (interfaces == null || interfaces.length == 0){
            return new String[]{ITEXT};
        }

        return interfaces;
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
        List<String> attributeGroups = new ArrayList<>();
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null){
            attributeGroups.addAll(getTypeAttributeGroups(complexType));
        }

        if (!attributeGroups.isEmpty()){
            String[] attributeGroupsArr = new String[attributeGroups.size()];
            attributeGroups.toArray(attributeGroupsArr);
            return attributeGroupsArr;
        }

        return new String[0];
    }

    /**
     * Obtains the attribute groups of a given element that are present in its type attribute.
     * @param complexType The XsdComplexType object with the type attribute.
     * @return The names of the attribute groups.
     */
    private Collection<String> getTypeAttributeGroups(XsdComplexType complexType) {
        List<XsdAttributeGroup> attributeGroups = complexType.getXsdAttributes()
                .filter(attribute -> attribute.getParent() instanceof XsdAttributeGroup)
                .map(attribute -> (XsdAttributeGroup) attribute.getParent())
                .distinct()
                .collect(Collectors.toList());

        attributeGroups.addAll(complexType.getXsdAttributeGroup().collect(Collectors.toList()));

        attributeGroups.stream().distinct().forEach(this::addAttributeGroup);

        if (!attributeGroups.isEmpty()){
            return getBaseAttributeGroupInterface(complexType.getXsdAttributeGroup().collect(Collectors.toList()));
        }

        return Collections.emptyList();
    }

    /**
     * Recursively iterates in parents of attributes in order to try finding a common attribute group.
     * @param attributeGroups The attributeGroups contained in the element.
     * @return The elements super class name.
     */
    private List<String> getBaseAttributeGroupInterface(List<XsdAttributeGroup> attributeGroups){
        List<XsdAttributeGroup> parents = new ArrayList<>();

        attributeGroups.forEach(attributeGroup -> {
            XsdAttributeGroup parent = (XsdAttributeGroup) attributeGroup.getParent();

            if (!parents.contains(parent) && parent != null){
                parents.add(parent);
            }
        });

        if (attributeGroups.size() == 1){
            List<String> interfaces = new ArrayList<>();
            interfaces.add(getInterfaceName(toCamelCase(attributeGroups.get(0).getName())));
            return interfaces;
        }

        if (parents.size() == 0){
            return attributeGroups.stream()
                    .map(baseClass -> getInterfaceName(toCamelCase(baseClass.getName())))
                    .collect(Collectors.toList());
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
            AttributeHierarchyItem attributeHierarchyItemItem = new AttributeHierarchyItem(parentNames, ownElements);

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
            signature = new StringBuilder("<T::L" + IELEMENT_TYPE + "<TT;TP;>;P::" + IELEMENT_TYPE_DESC + ">" + JAVA_OBJECT_DESC + "L" + IELEMENT_TYPE + "<TT;TP;>;");
        } else {
            signature = new StringBuilder("<T::L" + IELEMENT_TYPE + "<TT;TP;>;P::" + IELEMENT_TYPE_DESC + ">" + JAVA_OBJECT_DESC);

            for (String anInterface : interfaces) {
                signature.append("L").append(getFullClassTypeName(anInterface, apiName)).append("<TT;TP;>;");
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
     * Obtains all the interfaces that a given element will implement.
     * @param element The element in which the class will be based.
     * @return A string array with all the interface names.
     */
    String[] getInterfaces(XsdElement element, String apiName) {
        String[] attributeGroupInterfaces =  getAttributeGroupInterfaces(element);
        String[] elementGroupInterfaces =  getElementInterfaces(element, apiName);

        return ArrayUtils.addAll(attributeGroupInterfaces, elementGroupInterfaces);
    }

    /**
     * @param complexType The complexType of the element which will implement the interfaces.
     * @param apiName The name of the API to be generated.
     * @return The name of every interface that the element will implement directly.
     */
    private String[] getElementInterfaces(XsdComplexType complexType, String apiName) {
        XsdAbstractElement child = complexType.getXsdChildElement();

        if (child != null){
            Pair<String, Integer> interfaceInfo = iterativeCreation(child, toCamelCase(((XsdElement) complexType.getParent()).getName()), 0, apiName, null);

            return new String[] {interfaceInfo.getKey()};
        }

        return null;
    }

    /**
     * Generates an interface based on a XsdGroup element.
     * @param groupName The group name of the XsdGroup element.
     * @param choiceElement The child XsdChoice.
     * @param allElement The child XsdAll.
     * @param sequenceElement The child XsdSequence.
     * @param className The className of the element which contains this group.
     * @param interfaceIndex The current interface index that serves as a base to distinguish interface names.
     * @param apiName The name of the API to be generated.
     * @return A pair with the key being the name of the created group interface and the current interface index after the creation of the interface.
     */
    private Pair<String, Integer> groupMethod(String groupName, XsdChoice choiceElement, XsdAll allElement, XsdSequence sequenceElement, String className, int interfaceIndex, String apiName){
        List<String> extendedInterfaces = new ArrayList<>();
        String interfaceName = getInterfaceName(className + "Group" + interfaceIndex);
        String[] extendedInterfacesArr;

        if (groupName != null){
            interfaceName = getInterfaceName(toCamelCase(groupName));
        }

        if (allElement != null) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(allElement, className, interfaceIndex + 1, apiName, groupName);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        if (choiceElement != null) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(choiceElement, className, interfaceIndex + 1, apiName, groupName);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        if (sequenceElement != null) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(sequenceElement, className, interfaceIndex + 1, apiName, groupName);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        if (extendedInterfaces.isEmpty()){
            extendedInterfacesArr = new String[]{ITEXT};
        } else {
            extendedInterfacesArr = new String[extendedInterfaces.size()];
            extendedInterfaces.toArray(extendedInterfacesArr);
        }

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        writeClassToFile(interfaceName, classWriter, apiName);

        return new Pair<>(interfaceName, interfaceIndex);
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
    private Pair<String, Integer> sequenceMethod(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, String apiName, String groupName) {
        String interfaceNameBase = getInterfaceName(className + "Sequence");

        if (groupName != null){
            interfaceNameBase = getInterfaceName(toCamelCase(groupName + "Sequence"));
        }

        String interfaceName = interfaceNameBase + interfaceIndex;

        if (createdInterfaces.contains(interfaceName)){
            return new Pair<>(interfaceName, interfaceIndex);
        }

        Pair<List<XsdAbstractElement>, List<String>> sequenceInfo = getSequenceInfo(xsdElements, className, interfaceIndex, 0, apiName, groupName).getKey();
        List<XsdAbstractElement> sequenceList = sequenceInfo.getKey();
        List<String> sequenceNames = sequenceInfo.getValue();

        for (int i = 0; i < sequenceList.size(); i++) {
            XsdAbstractElement sequenceElement = sequenceList.get(i);
            String sequenceName = sequenceNames.get(i);

            String currentInterfaceName = interfaceNameBase + interfaceIndex;
            String[] interfaces = new String[] {ITEXT};

            boolean isLast = i == sequenceList.size() - 1;

            ClassWriter classWriter = generateClass(currentInterfaceName, JAVA_OBJECT, interfaces, getInterfaceSignature(interfaces, apiName), ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT, apiName);

            if (sequenceElement instanceof XsdElement){
                createElementsForSequence(classWriter, sequenceName, apiName, className, currentInterfaceName, isLast, interfaceNameBase, interfaceIndex, (XsdElement) sequenceElement, groupName);
            }

            if (sequenceElement instanceof XsdGroup || sequenceElement instanceof XsdChoice || sequenceElement instanceof XsdAll){
                List<XsdElement> elements = getAllElementsRecursively(sequenceElement);

                createElementsForSequence(classWriter, sequenceName, apiName, className, currentInterfaceName, isLast, interfaceNameBase, interfaceIndex, elements, groupName);
            }

            ++interfaceIndex;
        }

        return new Pair<>(interfaceName, interfaceIndex);
    }

    /**
     * This method adds a method to a previously created Sequence interface and creates the subsequent types.
     * <xs:element name="personInfo">
         <xs:complexType>
             <xs:sequence>
                <xs:element name="firstName" type="xs:string"/>
        (...)
        In this case this method will do the following:
            * Receives the interface PersonInfoSequence0
            * Adds the method firstName to that interface and writes the interface to disc.
            * This method will add a FirstName element to the parent, so the FirstName will need to be created.
            * That method will return PersonInfoFirstName to support the next element in the sequence, so the PersonInfoFirstName will need to be created.
     * @param classWriter The classWriter of the current sequence interface.
     * @param sequenceName The name of the sequence element. Following the above example that should be firstName.
     * @param apiName The name of the API to be generated.
     * @param className The className of the element that will implement the sequence. Following the above example that should be PersonInfo.
     * @param currentInterfaceName The name of the current sequence interface. Following the above example that should be PersonInfoSequence0.
     * @param isLast Indicates if the element to be created is the last of the sequence.
     * @param interfaceNameBase A base name of the sequence interfaces.
     * @param interfaceIndex The current interface index.
     * @param sequenceElement The element that serves as base to create this interface.
     * @param groupName The group name of the group that contains this sequence, if any.
     */
    private void createElementsForSequence(ClassWriter classWriter, String sequenceName, String apiName, String className, String currentInterfaceName, boolean isLast, String interfaceNameBase, int interfaceIndex, XsdElement sequenceElement, String groupName) {
        String addingChildName = toCamelCase(sequenceName);
        String nextTypeName;

        if (isLast){
            if (groupName != null){
                nextTypeName = className;
            } else {
                nextTypeName = className + "Complete";
            }
        } else {
            nextTypeName = className + toCamelCase(sequenceName);
        }

        generateSequenceMethod(classWriter, addingChildName, nextTypeName, className, apiName, sequenceName, currentInterfaceName, isLast, groupName);

        writeClassToFile(currentInterfaceName, classWriter, apiName);

        if (!(isLast && groupName != null)){
            generateSequenceInnerElement(className, nextTypeName, isLast, apiName, interfaceNameBase, interfaceIndex, groupName);
        }

        createElement(sequenceElement, apiName);

        createdElements.put(nextTypeName, null);
    }

    /**
     * @param classWriter classWriter The classWriter of the current sequence interface.
     * @param nextTypeName The name of the next type to return.
     * @param apiName The name of the API to be generated.
     * @param className The className of the element that will implement the sequence.
     * @param currentInterfaceName The name of the current sequence interface.
     * @param isLast Indicates if the element to be created is the last of the sequence.
     * @param interfaceNameBase A base name of the sequence interfaces.
     * @param interfaceIndex The current interface index.
     * @param sequenceElements The elements that serves as base to create this interface.
     * @param groupName The group name of the group that contains this sequence, if any.
     */
    private void createElementsForSequence(ClassWriter classWriter, String nextTypeName, String apiName, String className,
                                           String currentInterfaceName, boolean isLast, String interfaceNameBase, int interfaceIndex, List<XsdElement> sequenceElements, String groupName) {
        if (isLast){
            if (groupName != null){
                nextTypeName = className;
            } else {
                nextTypeName = className + "Complete";
            }
        } else {
            nextTypeName = className + nextTypeName;
        }

        String finalNextTypeName = nextTypeName;

        sequenceElements.stream().map(XsdReferenceElement::getName).forEach(sequenceName -> generateSequenceMethod(classWriter, toCamelCase(sequenceName), finalNextTypeName, className, apiName, sequenceName, currentInterfaceName, isLast, groupName));

        writeClassToFile(currentInterfaceName, classWriter, apiName);

        if (!(isLast && groupName != null)){
            generateSequenceInnerElement(className, finalNextTypeName, isLast, apiName, interfaceNameBase, interfaceIndex, groupName);
        }

        sequenceElements.forEach(element -> createElement(element, apiName));

        createdElements.put(nextTypeName, null);
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
        String[] nextTypeInterfaces;

        if (!isLast){
            nextTypeInterfaces = new String[]{ interfaceNameBase + (interfaceIndex + 1)};
        } else {
            if (groupName != null){
                nextTypeInterfaces = new String[] {getInterfaceName(toCamelCase(groupName))};
            } else {
                nextTypeInterfaces = null;
            }
        }

        ClassWriter classWriter = generateClass(typeName, ABSTRACT_ELEMENT_TYPE, nextTypeInterfaces, getClassSignature(nextTypeInterfaces, typeName, apiName), ACC_PUBLIC + ACC_SUPER, apiName);

        XsdAsmElements.generateClassSpecificMethods(classWriter, typeName, apiName, firstToLower(className));

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
     * @param isLast Indicates if the current element is the last of the sequence.
     * @param groupName The group name of the group that contains this sequence, if any.
     */
    private void generateSequenceMethod(ClassWriter classWriter, String addingChildName, String nextTypeName, String className, String apiName, String sequenceName, String currentInterfaceName, boolean isLast, String groupName) {
        String addingType = getFullClassTypeName(addingChildName, apiName);
        String nextType = getFullClassTypeName(nextTypeName, apiName);
        String nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);
        String interfaceType = getFullClassTypeName(currentInterfaceName, apiName);

        if (isLast){
            nextTypeName = className;

            if (groupName == null){
                nextTypeName += "Complete";
            }

            nextType = getFullClassTypeName(nextTypeName, apiName);
            nextTypeDesc = getFullClassTypeNameDesc(nextTypeName, apiName);
        }

        //TODO Receber o tipo do elemento, se o tiver. Isto implica ter de mudar Text possivelmente.
        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, firstToLower(sequenceName), "(Ljava/lang/String;)" + nextTypeDesc, "(Ljava/lang/String;)L" + nextType + "<TP;>;", null);
        mVisitor.visitLocalVariable(firstToLower(sequenceName), JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, nextType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "º", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, nextType, CONSTRUCTOR, "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 2);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "getChildren", "()Ljava/util/List;", true);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitInsn(DUP);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
        mVisitor.visitInsn(POP);
        mVisitor.visitInvokeDynamicInsn("accept", "(" + nextTypeDesc + ")Ljava/util/function/Consumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;)V"), new Handle(Opcodes.H_INVOKEVIRTUAL, ABSTRACT_ELEMENT_TYPE, "addChild", "(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, false), Type.getType("(" + IELEMENT_TYPE_DESC + ")V"));
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "forEach", "(Ljava/util/function/Consumer;)V", true);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitTypeInsn(NEW, addingType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "self", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, addingType, "<init>", "(" + IELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, addingType, "text", "(Ljava/lang/String;)" + IELEMENT_TYPE_DESC, false);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, nextType, "addChild", "(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, false);
        mVisitor.visitInsn(POP);

        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "º", "()" + IELEMENT_TYPE_DESC, true);
        Label l0 = new Label();
        mVisitor.visitJumpInsn(IFNULL, l0);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "º", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "getChildren", "()Ljava/util/List;", true);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "remove", "(Ljava/lang/Object;)Z", true);
        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, interfaceType, "º", "()" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, IELEMENT_TYPE, "addChild", "(" + IELEMENT_TYPE_DESC + ")" + IELEMENT_TYPE_DESC, true);
        mVisitor.visitInsn(POP);
        mVisitor.visitLabel(l0);
        mVisitor.visitFrame(Opcodes.F_APPEND,1, new Object[] {nextType}, 0, null);
        mVisitor.visitVarInsn(ALOAD, 2);


        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 3);
        mVisitor.visitEnd();

        createdElements.put(addingChildName, null);
    }

    /**
     * Obtains information about all the members that make up the sequence.
     * @param xsdElements The members that make the sequence.
     * @param className The name of the element that this sequence belongs to.
     * @param interfaceIndex The current interface index.
     * @param unnamedIndex A special index for elements that have no name, which will help distinguish them.
     * @param apiName The name of the API to be generated.
     * @param groupName The group name of the group that contains this sequence, if any.
     * @return A pair, which contains a second pair as key, having the elements as key and the elements name as value, and another pair as value, which contains values for the two indexes.
     */
    private Pair<Pair<List<XsdAbstractElement>, List<String>>, Pair<Integer, Integer>> getSequenceInfo(Stream<XsdAbstractElement> xsdElements, String className, int interfaceIndex, int unnamedIndex, String apiName, String groupName){
        List<XsdAbstractElement> sequenceList = xsdElements.collect(Collectors.toList());
        List<XsdAbstractElement> allSequenceElements = new ArrayList<>();
        List<String> sequenceNames = new ArrayList<>();

        for (XsdAbstractElement element : sequenceList) {
            allSequenceElements.add(element);

            if (element instanceof XsdElement){
                if (((XsdElement) element).getName() != null){
                    sequenceNames.add(((XsdElement) element).getName());
                } else {
                    sequenceNames.add(className + "SequenceUnnamed" + unnamedIndex);
                    unnamedIndex = unnamedIndex + 1;
                }
            } else {
                if (element instanceof XsdSequence){
                    Pair<Pair<List<XsdAbstractElement>, List<String>>, Pair<Integer, Integer>> innerSequenceInfo = getSequenceInfo(element.getXsdElements(), className, interfaceIndex, unnamedIndex, apiName, groupName);

                    allSequenceElements.addAll(innerSequenceInfo.getKey().getKey());
                    sequenceNames.addAll(innerSequenceInfo.getKey().getValue());
                    interfaceIndex = innerSequenceInfo.getValue().getKey();
                    unnamedIndex = innerSequenceInfo.getValue().getValue();
                } else {
                    Pair<String, Integer> interfaceInfo = iterativeCreation(element, className, interfaceIndex + 1, apiName, groupName);

                    interfaceIndex = interfaceInfo.getValue();
                    sequenceNames.add(interfaceInfo.getKey());
                }
            }
        }

        return new Pair<>(new Pair<>(allSequenceElements, sequenceNames), new Pair<>(interfaceIndex, unnamedIndex));
    }

    /**
     * Generates an interface based on a XsdAll element.
     * @param directElements The direct elements of the XsdAll element. Each one will be represented as a method in the all interface.
     * @param className The name of the class that contains the XsdAll.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the API to be generated.
     * @param groupName The name of the group which contains the XsdAll element, if any.
     * @return A pair with the interface name and the interface index.
     */
    private Pair<String, Integer> allMethod(List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        String interfaceName;

        if (groupName != null){
            interfaceName = getInterfaceName(toCamelCase(groupName + "All" + interfaceIndex));
        } else {
            interfaceName = getInterfaceName(className + "All" + interfaceIndex);
        }

        if (createdInterfaces.contains(interfaceName)){
            return new Pair<>(interfaceName, interfaceIndex);
        }

        String[] extendedInterfacesArr = new String[]{ITEXT};

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        directElements.forEach(child -> {
            generateMethodsForElement(classWriter, child, getFullClassTypeName(interfaceName, apiName), IELEMENT_TYPE_DESC, apiName);
            createElement(child, apiName);
        });

        writeClassToFile(interfaceName, classWriter, apiName);

        return new Pair<>(interfaceName, interfaceIndex);
    }

    /**
     * Generates an interface based on a XsdChoice element.
     * @param groupElements The contained groupElements.
     * @param directElements The direct elements of the XsdChoice element.
     * @param className The name of the class that contains the XsdChoice element.
     * @param interfaceIndex The current interface index.
     * @param apiName The name of the API to be generated.
     * @param groupName The name of the group in which this XsdChoice element is contained, if any.
     * @return A pair with the interface name and the interface index.
     */
    private Pair<String, Integer> choiceMethod(List<XsdGroup> groupElements, List<XsdElement> directElements, String className, int interfaceIndex, String apiName, String groupName){
        String interfaceName;

        if (groupName != null){
            interfaceName = getInterfaceName(toCamelCase(groupName + "Choice" + interfaceIndex));
        } else {
            interfaceName = getInterfaceName(className + "Choice" + interfaceIndex);
        }

        if (createdInterfaces.contains(interfaceName)){
            return new Pair<>(interfaceName, interfaceIndex);
        }

        String[] extendedInterfacesArr = new String[]{ITEXT};
        List<String> extendedInterfaces = new ArrayList<>();

        for (XsdGroup groupElement : groupElements) {
            Pair<String, Integer> interfaceInfo = iterativeCreation(groupElement, className, interfaceIndex + 1, apiName, groupName);

            interfaceIndex = interfaceInfo.getValue();
            extendedInterfaces.add(interfaceInfo.getKey());
        }

        if (!extendedInterfaces.isEmpty()){
            extendedInterfacesArr = new String[extendedInterfaces.size()];
            extendedInterfaces.toArray(extendedInterfacesArr);
        }

        ClassWriter classWriter = generateClass(interfaceName, JAVA_OBJECT, extendedInterfacesArr, getInterfaceSignature(extendedInterfacesArr, apiName), ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        directElements.forEach(child -> {
            XsdAsmElements.generateMethodsForElement(classWriter, child, getFullClassTypeName(interfaceName, apiName), IELEMENT_TYPE_DESC, apiName);
            createElement(child, apiName);
        });

        writeClassToFile(interfaceName, classWriter, apiName);

        return new Pair<>(interfaceName, interfaceIndex);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Pair<String, Integer> iterativeCreation(XsdAbstractElement element, String className, int interfaceIndex, String apiName, String groupName){
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

        Pair<String, Integer> pair = null;

        if (element instanceof XsdGroup){
            XsdChoice choiceElement = choiceElements.size() == 1 ? choiceElements.get(0) : null;
            XsdSequence sequenceElement = sequenceElements.size() == 1 ? sequenceElements.get(0) : null;
            XsdAll allElement = allElements.size() == 1 ? allElements.get(0) : null;

            pair = groupMethod(((XsdGroup) element).getName(), choiceElement, allElement, sequenceElement, className, interfaceIndex, apiName);
        }

        if (element instanceof XsdAll){
            pair = allMethod(directElements, className, interfaceIndex, apiName, groupName);
        }

        if (element instanceof XsdChoice){
            pair = choiceMethod(groupElements, directElements, className, interfaceIndex, apiName, groupName);
        }

        if (element instanceof  XsdSequence){
            pair = sequenceMethod(element.getXsdElements(), className, interfaceIndex, apiName, groupName);
        }

        if (pair == null){
            throw new RuntimeException("Invalid element interface type.");
        }

        createdInterfaces.add(pair.getKey());

        return pair;
    }

    /**
     * Creates a class based on a XsdElement if it wasn't been already.
     * @param element The element that serves as base to creating the class.
     * @param apiName The name of the API to be generated.
     */
    private void createElement(XsdElement element, String apiName) {
        String elementName = element.getName();

        if (!createdElements.containsKey(elementName)){
            createdElements.put(elementName, element);
            xsdAsmInstance.generateClassFromElement(element, apiName);
        }
    }

    /**
     * Iterates in a given XsdAbstractElement object in order to obtain all the contained XsdElements.
     * @param element The element to iterate on.
     * @return All the XsdElements contained in the element.
     */
    private List<XsdElement> getAllElementsRecursively(XsdAbstractElement element) {
        List<XsdElement> allGroupElements = new ArrayList<>();
        List<XsdAbstractElement> directElements = element.getXsdElements().collect(Collectors.toList());

        allGroupElements.addAll(
                directElements.stream()
                        .filter(elem1 -> elem1 instanceof XsdElement)
                        .map(elem1 -> (XsdElement) elem1)
                        .collect(Collectors.toList()));

        for (XsdAbstractElement elem : directElements) {
            if ((elem instanceof XsdMultipleElements || elem instanceof XsdGroup) && elem.getXsdElements() != null) {
                allGroupElements.addAll(getAllElementsRecursively(elem));
            }
        }

        return allGroupElements;
    }

    Set<String> getExtraElementsForVisitor() {
        return createdElements.keySet();
    }

    void addCreatedElement(XsdElement element){
        createdElements.put(element.getName(), element);
    }
}
