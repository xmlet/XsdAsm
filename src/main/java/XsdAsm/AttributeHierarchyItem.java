package XsdAsm;

import XsdElements.XsdAttribute;

import java.util.List;

class AttributeHierarchyItem {

    private List<String> parentName;
    private List<XsdAttribute> ownElements;

    AttributeHierarchyItem(List<String> parentName, List<XsdAttribute> ownElements){
        this.parentName = parentName;
        this.ownElements = ownElements;
    }

    List<String> getParentsName() {
        return parentName;
    }

    List<XsdAttribute> getOwnElements() {
        return ownElements;
    }
}
