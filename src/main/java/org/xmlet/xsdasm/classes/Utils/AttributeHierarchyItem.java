package org.xmlet.xsdasm.classes.utils;

import org.xmlet.xsdparser.xsdelements.XsdAttribute;

import java.util.List;

/**
 * Represents information regarding the existing hierarchy of XSD attributeGroups. It is used as a helper class when
 * generating the attributeGroup interfaces.
 */
public class AttributeHierarchyItem {

    /**
     * A {@link List} containing the names of other attributeGroup interfaces that will be extended by the attribute group
     * represented by an instance of this class.
     */
    private List<String> parentName;

    /**
     * A {@link List} containing {@link XsdAttribute} objects. Each attribute in this list will result in the creation
     * of a method in the interface that will be generated based on this attributeGroup instance.
     */
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
