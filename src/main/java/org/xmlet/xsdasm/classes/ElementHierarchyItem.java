package org.xmlet.xsdasm.classes;

import org.xmlet.xsdparser.xsdelements.XsdAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ElementHierarchyItem {

    private String interfaceName;
    private List<XsdAttribute> attributes;
    private List<String> interfaces;

    ElementHierarchyItem(String interfaceName, List<XsdAttribute> attributes, String[] extendedInterfaces){
        this.interfaceName = interfaceName;
        this.attributes = attributes;
        this.interfaces = new ArrayList<>();
        this.interfaces.addAll(Arrays.asList(extendedInterfaces));
    }

    String getInterfaceName() {
        return interfaceName;
    }

    List<XsdAttribute> getAttributes() {
        return attributes;
    }

    List<String> getInterfaces() {
        return interfaces;
    }

    void addInterface(String interfaceName) {
        this.interfaces.add(interfaceName);
    }
}
