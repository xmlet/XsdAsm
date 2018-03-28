package org.xmlet.xsdasm.classes;

import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.xmlet.xsdasm.classes.XsdAsmAttributes.generateAttribute;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.createGeneratedFilesDirectory;
import static org.xmlet.xsdasm.classes.XsdAsmVisitors.generateVisitors;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.createSupportingInfrastructure;

public class XsdAsm {

    private XsdAsmInterfaces interfaceGenerator = new XsdAsmInterfaces(this);
    private Map<String, List<XsdAttribute>> createdAttributes = new HashMap<>();

    /**
     * This method is the entry point for the class creation process.
     * It receives all the XsdAbstractElements and creates the necessary infrastructure for the
     * generated API, the required interfaces, visitors and all the classes based on the elements received.
     * @param elements The elements which will serve as base to the generated classes.
     * @param apiName The resulting API name.
     */
    public void generateClassFromElements(Stream<XsdElement> elements, String apiName){
        createGeneratedFilesDirectory(apiName);

        createSupportingInfrastructure(apiName);

        elements.forEach(element -> {
            interfaceGenerator.addCreatedElement(element);
            generateClassFromElement(element, apiName);
        });

        interfaceGenerator.generateInterfaces(createdAttributes, apiName);

        generateVisitors(interfaceGenerator.getExtraElementsForVisitor(), apiName);

        generateAttributes(apiName);
    }

    private void generateAttributes(String apiName) {
        createdAttributes.keySet().forEach(attribute ->
            createdAttributes.get(attribute).forEach(attributeVariation ->
                generateAttribute(attributeVariation, apiName)
            )
        );
    }

    void generateClassFromElement(XsdElement element, String apiName){
        XsdAsmElements.generateClassFromElement(interfaceGenerator, createdAttributes, element, apiName);
    }
}
