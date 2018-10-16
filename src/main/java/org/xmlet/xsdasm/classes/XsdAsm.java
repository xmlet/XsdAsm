package org.xmlet.xsdasm.classes;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;
import org.xmlet.xsdparser.xsdelements.XsdAttribute;
import org.xmlet.xsdparser.xsdelements.XsdElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.xmlet.xsdasm.classes.XsdAsmAttributes.generateAttribute;
import static org.xmlet.xsdasm.classes.XsdAsmUtils.createGeneratedFilesDirectory;
import static org.xmlet.xsdasm.classes.XsdAsmVisitors.generateVisitors;
import static org.xmlet.xsdasm.classes.XsdSupportingStructure.createSupportingInfrastructure;

public class XsdAsm {

    /**
     * An instance of {@link XsdAsmInterfaces}. It is used to multiple types of interfaces.
     */
    private XsdAsmInterfaces interfaceGenerator = new XsdAsmInterfaces(this);

    /**
     * A {@link Map} object with information about all the attributes that were used in the element generated classes.
     */
    private Map<String, List<XsdAttribute>> createdAttributes = new HashMap<>();

    /**
     * This method is the entry point for the class creation process.
     * It receives all the {@link XsdAbstractElement} objects and creates the necessary infrastructure for the
     * generated fluent interface, the required interfaces, visitors and all the classes based on the elements received.
     * @param elements The elements which will serve as base to the generated classes.
     * @param apiName The resulting fluent interface name.
     */
    public void generateClassFromElements(Stream<XsdElement> elements, String apiName){
        createGeneratedFilesDirectory(apiName);

        createSupportingInfrastructure(apiName);

        List<XsdElement> elementList = elements.collect(Collectors.toList());

        interfaceGenerator.addCreatedElements(elementList);

        elementList.forEach(element -> generateClassFromElement(element, apiName));

        interfaceGenerator.generateInterfaces(createdAttributes, apiName);

        generateVisitors(interfaceGenerator.getExtraElementsForVisitor(), apiName);

        generateAttributes(apiName);
    }

    /**
     * Generates attribute classes that are used by some element.
     * @param apiName The name of the resulting fluent interface.
     */
    private void generateAttributes(String apiName) {
        createdAttributes.keySet().forEach(attribute ->
            createdAttributes.get(attribute).forEach(attributeVariation ->
                generateAttribute(attributeVariation, apiName)
            )
        );
    }

    /**
     * Generates an element class based on the received {@link XsdElement} object.
     * @param element The {@link XsdElement} containing information needed for the class creation.
     * @param apiName The name of the resulting fluent interface.
     */
    void generateClassFromElement(XsdElement element, String apiName){
        XsdAsmElements.generateClassFromElement(interfaceGenerator, createdAttributes, element, apiName);
    }
}
