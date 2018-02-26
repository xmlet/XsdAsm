package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

class XsdAsmVisitors {
    /**
     * Generates both the visitor interface and abstract visitor with method for each element from the list.
     * @param elementNames The elements names list.
     * @param apiName The api this classes will belong to.
     */
    static void generateVisitors(Set<String> elementNames, String apiName){
        generateVisitorInterface(elementNames, apiName);

        generateAbstractVisitor(elementNames, apiName);
    }

    /**
     * Generates the visitor class for this api with methods for all elements in the element list.
     * @param elementNames The elements names list.
     * @param apiName The api this class will belong to.
     */
    private static void generateVisitorInterface(Set<String> elementNames, String apiName) {
        ClassWriter classWriter = generateClass(VISITOR, JAVA_OBJECT, null, "<R:Ljava/lang/Object;>Ljava/lang/Object;", ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        elementNames.forEach(elementName -> addVisitorInterfaceMethod(classWriter, elementName, null, apiName));

        addVisitorInterfaceMethod(classWriter, TEXT_CLASS, "<U:Ljava/lang/Object;>(L" + TEXT_TYPE + "<TR;TU;*>;)V", apiName);

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

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "visit", "(" + elementTypeDesc + ")V", signature, null);
        mVisitor.visitLocalVariable(elementName, elementTypeDesc, signature, new Label(), new Label(),1);
        mVisitor.visitEnd();
    }

    /**
     * Generates the AbstractVisitor class, with methods for every element in the list.
     * @param elementNames The elements names list.
     * @param apiName The api this class will belong to.
     */
    private static void generateAbstractVisitor(Set<String> elementNames, String apiName) {
        ClassWriter classWriter = generateClass(ABSTRACT_VISITOR, JAVA_OBJECT, new String[]{VISITOR}, "<R:Ljava/lang/Object;>Ljava/lang/Object;L" + ELEMENT_VISITOR_TYPE + "<TR;>;", ACC_PUBLIC + ACC_ABSTRACT + ACC_SUPER, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_ABSTRACT + ACC_PUBLIC, "visit", "(" + ELEMENT_TYPE_DESC + ")V", "<T::" + ELEMENT_TYPE_DESC + ">(L" + ELEMENT_TYPE + "<TT;*>;)V", null);
        mVisitor.visitLocalVariable("elem", ELEMENT_TYPE_DESC, "L" + ELEMENT_TYPE + "<TT;*>;", new Label(), new Label(),1);
        mVisitor.visitEnd();

        elementNames.forEach(elementName -> addAbstractVisitorMethod(classWriter, elementName, null, apiName));

        addAbstractVisitorMethod(classWriter, TEXT_CLASS, "<U:Ljava/lang/Object;>(L" + TEXT_TYPE + "<TR;TU;*>;)V", apiName);

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

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, "visit", "(" + elementTypeDesc + ")V", signature, null);
        mVisitor.visitLocalVariable(elementName, elementTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, ABSTRACT_ELEMENT_VISITOR_TYPE, "visit", "(" + ELEMENT_TYPE_DESC + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    }
}
