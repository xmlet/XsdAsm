package org.xmlet.xsdasm;

import org.junit.Test;
import org.xmlet.xsdasm.classes.XsdAsm;
import org.xmlet.xsdasm.classes.XsdAsmUtils;
import org.xmlet.xsdasm.main.XsdAsmMain;
import org.xmlet.xsdparser.core.XsdParser;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class XsdAsmBasicTest {

    private static final String HTML_FILE_NAME = XsdAsmBasicTest.class.getClassLoader().getResource("html_5.xsd").getPath();
    private static final String HTML_API_NAME = "Html5";

    static {
        XsdAsmMain.main(new String[]{HTML_FILE_NAME, HTML_API_NAME});
    }

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        testIntegrity(HTML_API_NAME);
    }

    @SuppressWarnings("Duplicates")
    private void testIntegrity(String apiName) throws Exception {
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

                ucl.loadClass( getDottedPackage(apiName) + className);
            }
        }
    }

    private String getDottedPackage(String apiName){
        return XsdAsmUtils.getPackage(apiName).replaceAll("/", ".");
    }

}
