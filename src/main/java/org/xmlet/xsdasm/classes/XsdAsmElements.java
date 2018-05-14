package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdparser.xsdelements.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

class XsdAsmElements {

    private XsdAsmElements(){}

    /**
     * Generates a class from a given XsdElement. It also generated its constructors and methods.
     * @param interfaceGenerator An instance of XsdAsmInterfaces which contains interface information.
     * @param createdAttributes A list of names of attribute classes already created.
     * @param element The element from which the class will be generated.
     * @param apiName The api this class will belong.
     */
    static void generateClassFromElement(XsdAsmInterfaces interfaceGenerator, Map<String, List<XsdAttribute>> createdAttributes, XsdElement element, String apiName) {
        String className = getCleanName(element);

        XsdElement base = getBaseFromElement(element);

        Stream<XsdAttribute> elementAttributes = getOwnAttributes(element);
        String[] interfaces = interfaceGenerator.getInterfaces(element, apiName);

        while (base != null) {
            if (element.getName().equals("Button")){
                int a = 5;
            }

            List<XsdAttribute> finalElementAttributes = elementAttributes.collect(Collectors.toList());
            List<String> attributeNames = finalElementAttributes.stream().map(XsdAttribute::getName).collect(Collectors.toList());
            List<XsdAttribute> moreAttributes = getOwnAttributes(base).filter(attribute -> !attributeNames.contains(attribute.getName())).collect(Collectors.toList());
            finalElementAttributes.addAll(moreAttributes);
            elementAttributes = finalElementAttributes.stream();

            List<String> interfaceNames = Arrays.asList(interfaces);

            for (String otherInterface : interfaceGenerator.getInterfaces(base, apiName)) {
                if (!interfaceNames.contains(otherInterface)){
                    interfaceNames.add(otherInterface);
                }
            }

            interfaces = new String[interfaceNames.size()];
            interfaceNames.toArray(interfaces);

            base = getBaseFromElement(base);
        }

        String signature = getClassSignature(interfaces, className, apiName);

        // String superType = base == null ? abstractElementType : getFullClassTypeName(getCleanName(base), apiName);

        String superType = abstractElementType;

        ClassWriter classWriter = generateClass(className, superType, interfaces, signature,ACC_PUBLIC + ACC_SUPER, apiName);

        generateClassSpecificMethods(classWriter, className, apiName, superType, null);

        /*
        if (element.getName().equals("Button")){
            List<String> c = elementAttributes.map(attribute -> attribute.getName()).collect(Collectors.toList());

            Collections.sort(c);

            int a = 5;
        }
        */

        elementAttributes.forEach(elementAttribute -> generateMethodsAndCreateAttribute(createdAttributes, classWriter, elementAttribute, getFullClassTypeNameDesc(className, apiName), apiName));

        writeClassToFile(className, classWriter, apiName);
    }

    private static XsdElement getBaseFromElement(XsdElement element) {
        XsdComplexType complexType = element.getXsdComplexType();

        if (complexType != null){
            XsdComplexContent complexContent = complexType.getComplexContent();

            if (complexContent != null){
                XsdExtension extension = complexContent.getXsdExtension();

                if (extension != null){
                    return extension.getBase();
                }
            }
        }

        return null;
    }

    /**
     * Creates some class specific methods that all implementations of AbstractElement should have, which are:
     * A constructor with a String parameter, which is it will create a Text attribute in the created element.
     * A constructor with two String parameters, the first being the value of the Text attribute, and the second being a value for its id.
     * An implementation of the self method, which should return this.
     * @param classWriter The class writer on which should be written the methods.
     * @param className The class name.
     * @param defaultName The defaultName for this element.
     */
    static void generateClassSpecificMethods(ClassWriter classWriter, String className, String apiName, String superType, String defaultName) {
        String classType = getFullClassTypeName(className, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(className, apiName);
        String name;

        if (defaultName != null){
            name = defaultName;
        } else {
            name = firstToLower(className);
        }

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn(name);
        mVisitor.visitMethodInsn(INVOKESPECIAL, superType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, superType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc + ")V", "(TZ;)V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitLdcInsn(name);
        mVisitor.visitMethodInsn(INVOKESPECIAL, superType, CONSTRUCTOR, "(" + elementTypeDesc + "" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc + "" + JAVA_STRING_DESC + ")V", "(TZ;" + JAVA_STRING_DESC + ")V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),2);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 2);
        mVisitor.visitMethodInsn(INVOKESPECIAL, superType, CONSTRUCTOR, "(" + elementTypeDesc + "" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(3, 3);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "self", "()" + classTypeDesc, "()L" + classType + "<TZ;>;", null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
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

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, "accept", "(" + elementVisitorTypeDesc + ")V", null, null);
        mVisitor.visitLocalVariable("visitor", elementVisitorTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementVisitorType, "visit", "(" + classTypeDesc + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        /*
        mVisitor = classWriter.visitMethod(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "lambda$accept$0", "(" + elementVisitorTypeDesc + elementTypeDesc + ")V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementType, "accept", "(" + elementVisitorTypeDesc + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
        */

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

    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param child The child of the element which generated the class. Their name represents a method.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, XsdElement child, String classType, String returnType, String apiName) {
        generateMethodsForElement(classWriter, child.getName(), classType, returnType, apiName, new String[]{});
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param childName The name of the element which generated the class.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, String childName, String classType, String returnType, String apiName, String[] annotationsDesc) {
        childName = firstToLower(getCleanName(childName));
        String childCamelName = toCamelCase(childName);
        String childType = getFullClassTypeName(childCamelName, apiName);
        String childTypeDesc = getFullClassTypeNameDesc(childCamelName, apiName);
        boolean isInterface = isInterfaceMethod(returnType);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, childName, "()" + childTypeDesc, "()L" + childType + "<TT;>;", null);

        for (String annotationDesc: annotationsDesc) {
            mVisitor.visitAnnotation(annotationDesc, true);
        }

        mVisitor.visitCode();
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitVarInsn(ASTORE, 1);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);

        if (isInterface){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, false);
        }

        mVisitor.visitInsn(POP);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(3, 2);
        mVisitor.visitEnd();
    }
}
