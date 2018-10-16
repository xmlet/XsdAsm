package org.xmlet.xsdasm.classes.utils;

import org.xmlet.xsdparser.xsdelements.XsdAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents information regarding the hierarchy that exist between elements in the XSD language.
 */
public class ElementHierarchyItem {

    /**
     * The name of the interface that will be generated based on the information existing in this class.
     */
    private String interfaceName;

    /**
     * A {@link List} of {@link XsdAttribute} objects. Each attribute will result in the creation of a method in the
     * interface generated based on an instance of this class.
     */
    private List<XsdAttribute> attributes;

    /**
     * A {@link List} of names of interfaces that will be extended by the interface generated based on an instance of
     * this class.
     */
    private List<String> interfaces;

    public ElementHierarchyItem(String interfaceName, List<XsdAttribute> attributes, String[] extendedInterfaces){
        this.interfaceName = interfaceName;
        this.attributes = attributes;
        this.interfaces = new ArrayList<>();
        this.interfaces.addAll(Arrays.asList(extendedInterfaces));
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public List<XsdAttribute> getAttributes() {
        return attributes;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }
}
