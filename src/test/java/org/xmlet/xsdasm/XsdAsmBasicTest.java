package org.xmlet.xsdasm;

import org.junit.Test;
import org.xmlet.xsdasm.classes.XsdAsmUtils;
import org.xmlet.xsdasm.main.XsdAsmMain;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class XsdAsmBasicTest {

    /* Uncomment code for code coverage runs. The code is commented for faster development. */

    private static final String HTML_API_NAME = "htmlFaster";

    /*
    private static final String ANDROID_API_NAME = "androidFaster";
    private static final String MIN_API_NAME = "testMinFaster";
    private static final String WPFE_API_NAME = "wpfeFaster";
    */

    private static final String HTML_FILE_PATH = getFilePath("html_5.xsd");

    /*
    private static final String ANDROID_FILE_PATH = getFilePath("android.xsd");
    private static final String MIN_FILE_PATH = getFilePath("test_min.xsd");
    private static final String WPFE_FILE_PATH = getFilePath("wpfe.xsd");
    */

    static {
        XsdAsmMain.main(new String[]{HTML_FILE_PATH, HTML_API_NAME});

        /*
        XsdAsmMain.main(new String[]{ANDROID_FILE_PATH, ANDROID_API_NAME});
        XsdAsmMain.main(new String[]{MIN_FILE_PATH, MIN_API_NAME});
        XsdAsmMain.main(new String[]{WPFE_FILE_PATH, WPFE_API_NAME});
        */
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

    private static String getFilePath(String fileName){
        URL url = XsdAsmBasicTest.class.getClassLoader().getResource(fileName);

        if (url != null){
            return url.getPath();
        }

        throw new RuntimeException("The file " + fileName + " is missing from the resources folder.");
    }
}
