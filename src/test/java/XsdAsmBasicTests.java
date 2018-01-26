import XsdAsm.XsdAsmUtils;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class XsdAsmBasicTests {

    private static String apiName = "TestObjects";

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        File generatedObjectsFolder = new File(XsdAsmUtils.getDestinationDirectory(apiName));
        File[] generatedFiles = generatedObjectsFolder.listFiles();

        assert generatedFiles != null;

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{
                        new URL("file://" + XsdAsmUtils.getDestinationDirectory(apiName)) });

        for (File generatedFile : generatedFiles) {
            if (generatedFile.getName().endsWith(".class")){
                String absolutePath = generatedFile.getAbsolutePath();

                String className = absolutePath.substring(absolutePath.lastIndexOf('\\') + 1, absolutePath.indexOf(".class"));

                ucl.loadClass( getDottedPackage() + className);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSelf() throws Exception {
        File generatedObjectsFolder = new File(XsdAsmUtils.getDestinationDirectory(apiName));
        File[] generatedFiles = generatedObjectsFolder.listFiles();

        assert generatedFiles != null;

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{
                        new URL("file://" + XsdAsmUtils.getDestinationDirectory(apiName)) });

        Class var = ucl.loadClass(getDottedPackage() + "Var");
        Object varInstance1 = var.newInstance();

        Method addVarMethod = var.getMethod("var", String.class);
        Method addVarMethodWithId = var.getMethod("var", String.class, String.class);

        addVarMethod.invoke(varInstance1, "varContent");

        //Assert.assertEquals(1, varInstance1.childs.size());

        Object varInstance2 = var.newInstance();

        addVarMethodWithId.invoke(varInstance2, "varContent", "varId");

        //Assert.assertEquals(varInstance2, returningVar);
        //Assert.assertEquals("varId", varInstance2.childs.get(0).id());
    }

    @Test
    public void testAttributeGroups() throws Exception {
        File generatedObjectsFolder = new File(XsdAsmUtils.getDestinationDirectory(apiName));
        File[] generatedFiles = generatedObjectsFolder.listFiles();

        assert generatedFiles != null;

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{
                        new URL("file://" + XsdAsmUtils.getDestinationDirectory(apiName)) });

        Class c = ucl.loadClass(getDottedPackage() + "ICoreAttributeGroup");

        c.getDeclaredMethods();
    }

    private String getDottedPackage(){
        return XsdAsmUtils.getPackage(apiName).replaceAll("/", ".");
    }
}


