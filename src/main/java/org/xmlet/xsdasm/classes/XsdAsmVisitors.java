package org.xmlet.xsdasm.classes;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

import static org.objectweb.asm.Opcodes.*;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.*;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.*;

/**
 * This class has the responsibility of creating the ElementVisitor class.
 */
class XsdAsmVisitors {

    private static final String VISIT_METHOD_NAME = "visit";
    private static final String SHARED_VISIT_METHOD_NAME = "sharedVisit";
    
    private XsdAsmVisitors(){}

    /**
     * Generates both the abstract visitor class with methods for each element from the list.
     * @param elementNames The elements names list.
     * @param apiName The name of the generated fluent interface.
     */
    static void generateVisitors(Set<String> elementNames, String apiName){
        generateVisitorInterface(elementNames, apiName);
    }

    /**
     * Generates the visitor class for this fluent interface with methods for all elements in the element list.
     *
     * Main methods:
     *  void <T extends Element> void sharedVisit(Element<T, ?> elem);
     *  void visitAttribute(String attributeName, String attributeValue);
     *  void visit(Text elem);
     *  void visit(Comment elem);
     *  <U> void visit(TextFunction<R, U, ?> elem);
     * @param elementNames The elements names list.
     * @param apiName The name of the generated fluent interface.
     */
    private static void generateVisitorInterface(Set<String> elementNames, String apiName) {
        ClassWriter classWriter = generateClass(VISITOR, JAVA_OBJECT, null, "<R:" + JAVA_OBJECT_DESC + ">" + JAVA_OBJECT_DESC, ACC_PUBLIC + ACC_ABSTRACT + ACC_SUPER, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, CONSTRUCTOR, "()V", null, null);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitMethodInsn(INVOKESPECIAL, JAVA_OBJECT, CONSTRUCTOR, "()V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(1, 1);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, SHARED_VISIT_METHOD_NAME, "(" + elementTypeDesc + ")V", "<T::" + elementTypeDesc + ">(L" + elementType + "<TT;*>;)V", null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_METHOD_NAME, "(" + textTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_METHOD_NAME, "(" + commentTypeDesc + ")V", null, null);
        mVisitor.visitEnd();

        mVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, VISIT_METHOD_NAME, "(" + textFunctionTypeDesc +")V", "<U:" + JAVA_OBJECT_DESC + ">(L" + textFunctionType + "<TR;TU;*>;)V", null);
        mVisitor.visitEnd();

        elementNames.forEach(elementName -> addVisitorInterfaceMethod(classWriter, elementName, apiName));

        writeClassToFile(VISITOR, classWriter, apiName);
    }

    /**
     * Adds a specific method for a visit call.
     * Example:
     *  void visit(Html elem){
     *      sharedVisit(elem);
     *  }
     * @param classWriter The ElementVisitor class {@link ClassWriter}.
     * @param elementName The specific element.
     * @param apiName The name of the generated fluent interface.
     */
    private static void addVisitorInterfaceMethod(ClassWriter classWriter, String elementName, String apiName){
        String cleanElementName = firstToUpper(getCleanName(elementName));
        String methodTypeDesc = getFullClassTypeNameDesc(cleanElementName, apiName);

        MethodVisitor mVisitor = classWriter.visitMethod(ACC_PUBLIC, VISIT_METHOD_NAME, "(" + methodTypeDesc + ")V", null, null);
        mVisitor.visitLocalVariable(firstToLower(cleanElementName), methodTypeDesc, null, new Label(), new Label(),1);
        mVisitor.visitCode();
        mVisitor.visitVarInsn(ALOAD, 0);
        mVisitor.visitVarInsn(ALOAD, 1);
        mVisitor.visitMethodInsn(INVOKEVIRTUAL, elementVisitorType, SHARED_VISIT_METHOD_NAME, "(" + elementTypeDesc + ")V", false);
        mVisitor.visitInsn(RETURN);
        mVisitor.visitMaxs(2, 2);
        mVisitor.visitEnd();
    }
}
