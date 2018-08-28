package org.xmlet.xsdasm.classes.Utils;

import org.xmlet.xsdparser.xsdelements.XsdAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElementHierarchyItem {

    private String interfaceName;
    private List<XsdAttribute> attributes;
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

    public void addInterface(String interfaceName) {
        this.interfaces.add(interfaceName);
    }
}
