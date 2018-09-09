package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.util.List;
import java.util.Map;

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

        String[] interfaces = interfaceGenerator.getInterfaces(element, apiName);
        String signature = getClassSignature(interfaces, className, apiName);
        String superType = abstractElementType;

        ClassWriter classWriter = generateClass(className, superType, interfaces, signature,ACC_PUBLIC + ACC_SUPER, apiName);

        generateClassSpecificMethods(classWriter, className, apiName);

        getOwnAttributes(element).forEach(elementAttribute -> generateMethodsAndCreateAttribute(createdAttributes, classWriter, elementAttribute, getFullClassTypeNameDesc(className, apiName), className, apiName));

        writeClassToFile(className, classWriter, apiName);
    }

    /**
     * Creates some class specific methods that all implementations of AbstractElement should have, which are:
     * A constructor with a String parameter, which is it will create a Text attribute in the created element.
     * A constructor with two String parameters, the first being the value of the Text attribute, and the second being a value for its id.
     * An implementation of the self method, which should return this.
     * @param classWriter The class writer on which should be written the methods.
     * @param className The class name.
     */
    private static void generateClassSpecificMethods(ClassWriter classWriter, String className, String apiName) {
        generateClassSpecificMethods(classWriter, className, className, apiName);
    }

    /**
     * Creates some class specific methods that all implementations of AbstractElement should have, which are:
     * A constructor with a String parameter, which is it will create a Text attribute in the created element.
     * A constructor with two String parameters, the first being the value of the Text attribute, and the second being a value for its id.
     * An implementation of the self method, which should return this.
     * @param classWriter The class writer on which should be written the methods.
     * @param className The class name.
     */
    static void generateClassSpecificMethods(ClassWriter classWriter, String typeName, String className, String apiName) {
        String classType = getFullClassTypeName(typeName, apiName);
        String classTypeDesc = getFullClassTypeNameDesc(typeName, apiName);
        String name = firstToLower(className);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitLdcInsn(name);
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", null, null);
        mVisitor.visitLocalVariable("name", JAVA_STRING_DESC, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + JAVA_STRING_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "(" + elementTypeDesc + ")V", "(TZ;)V", null);
        mVisitor.visitLocalVariable("parent", elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitLdcInsn(name);
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + elementTypeDesc + "" + JAVA_STRING_DESC + ")V", false);
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
        mVisitor.visitMethodInsn(INVOKESPECIAL, abstractElementType, CONSTRUCTOR, "(" + elementTypeDesc + "" + JAVA_STRING_DESC + ")V", false);
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
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, "visit", "(" + classTypeDesc + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
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
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param child The child of the element which generated the class. Their name represents a method.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, XsdElement child, String classType, String returnType, String apiName) {
        generateMethodsForElement(classWriter, child.getName(), classType, returnType, false, apiName, new String[]{});
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param child The child of the element which generated the class. Their name represents a method.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, XsdElement child, String classType, String returnType, boolean checkCast, String apiName) {
        generateMethodsForElement(classWriter, child.getName(), classType, returnType, checkCast, apiName, new String[]{});
    }

    static void generateMethodsForElement(ClassWriter classWriter, String childName, String classType, String returnType, String apiName, String[] annotationsDesc) {
        generateMethodsForElement(classWriter, childName, classType, returnType, false, apiName, annotationsDesc);
    }

    /**
     * Generates the methods in a given class for a given child that the class is allowed to have.
     * @param classWriter The class writer where the method will be written.
     * @param childName The name of the element which generated the class.
     * @param classType The type of the class which contains the children elements.
     */
    static void generateMethodsForElement(ClassWriter classWriter, String childName, String classType, String returnType, boolean checkCast, String apiName, String[] annotationsDesc) {
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
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitTypeInsn(NEW, childType);
        mVisitor.visitInsn(DUP);
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "self", "()" + elementTypeDesc, true);
        mVisitor.visitMethodInsn(INVOKESPECIAL, childType, CONSTRUCTOR, "(" + elementTypeDesc + ")V", false);

        if (isInterface){
            mVisitor.visitMethodInsn(INVOKEINTERFACE, classType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, true);
        } else {
            mVisitor.visitMethodInsn(INVOKEVIRTUAL, classType, "addChild", "(" + elementTypeDesc + ")" + elementTypeDesc, false);
        }

        if (checkCast){
            mVisitor.visitTypeInsn(CHECKCAST, childType);
        }

        mVisitor.visitInsn(ARETURN);
        mVisitor.visitMaxs(4, 1);
        mVisitor.visitEnd();
    }
}
