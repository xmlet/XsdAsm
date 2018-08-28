package org.xmlet.xsdasm.classes.Utils;

import org.xmlet.xsdparser.xsdelements.XsdAttribute;

import java.util.List;

public class AttributeHierarchyItem {

    private List<String> parentName;
    private List<XsdAttribute> ownElements;

    public AttributeHierarchyItem(List<String> parentName, List<XsdAttribute> ownElements){
        this.parentName = parentName;
        this.ownElements = ownElements;
    }

    public List<String> getParentsName() {
        return parentName;
    }

    public List<XsdAttribute> getOwnElements() {
        return ownElements;
    }
}
