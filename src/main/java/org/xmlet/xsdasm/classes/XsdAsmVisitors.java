package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

class XsdAsmVisitors {

    private static final String VISIT_METHOD_NAME = "visit";
    private static final String SHARED_VISIT_METHOD_NAME = "sharedVisit";
    
    private XsdAsmVisitors(){}

    /**
     * Generates both the visitor interface and abstract visitor with method for each element from the list.
     * @param elementNames The elements names list.
     * @param apiName The api this classes will belong to.
     */
    static void generateVisitors(Set<String> elementNames, String apiName){
        generateVisitorInterface(elementNames, apiName);
    }

    /**
     * Generates the visitor class for this api with methods for all elements in the element list.
     * @param elementNames The elements names list.
     * @param apiName The api this class will belong to.
     */
    private static void generateVisitorInterface(Set<String> elementNames, String apiName) {
        ClassWriter classWriter = generateClass(VISITOR, JAVA_OBJECT, null, "<R:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, SHARED_VISIT_METHOD_NAME, "(" + elementTypeDesc + ")V", "<T::" + elementTypeDesc + ">(L" + elementType + "<TT;*>;)V", null);
        mVisitor.visitEnd();

        elementNames.forEach(elementName -> addVisitorInterfaceMethod(classWriter, elementName, null, apiName));

        addVisitorInterfaceMethod(classWriter, firstToLower(TEXT_CLASS), "<U:" + JAVA_OBJECT_DESC + ">(L" + textType + "<TR;TU;*>;)V", apiName);

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
        String methodTypeDesc = getFullClassTypeNameDesc(toCamelCase(elementName), apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, VISIT_METHOD_NAME, "(" + methodTypeDesc + ")V", signature, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEINTERFACE, elementVisitorType, SHARED_VISIT_METHOD_NAME, "(" + elementTypeDesc + ")V", true);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    }
}
