package org.xmlet.xsdasm.classes;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.xmlet.xsdasm.classes.XsdAsmUtils.createGeneratedFilesDirectory;
import static org.xmlet.xsdasm.classes.XsdAsmVisitors.generateVisitors;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.createSupportingInfrastructure;

public class XsdAsm {

    private XsdAsmInterfaces interfaceGenerator = new XsdAsmInterfaces(this);
    private List<String> createdAttributes = new ArrayList<>();

    /**
     * This method is the entry point for the class creation process.
     * It receives all the XsdAbstractElements and creates the necessary infrastructure for the
     * generated API, the required interfaces, visitors and all the classes based on the elements received.
     * @param elements The elements which will serve as base to the generated classes.
     * @param apiName The resulting API name.
     */
    public void generateClassFromElements(Stream<XsdAbstractElement> elements, String apiName){
        createGeneratedFilesDirectory(apiName);

        createSupportingInfrastructure(apiName);

        List<XsdElement> elementList = elements.filter(element -> element instanceof XsdElement)
                .map(element -> (XsdElement) element)
                .collect(Collectors.toList());

        elementList.forEach(element -> interfaceGenerator.addCreatedElement(element));

        elementList.forEach(element -> generateClassFromElement(element, apiName));

        interfaceGenerator.generateInterfaces(createdAttributes, apiName);

        generateVisitors(interfaceGenerator.getExtraElementsForVisitor(), apiName);
    }

    void generateClassFromElement(XsdElement element, String apiName){
        XsdAsmElements.generateClassFromElement(interfaceGenerator, createdAttributes, element, apiName);
    }
}
