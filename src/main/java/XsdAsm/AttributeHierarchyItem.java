package XsdAsm;

import XsdElements.XsdAttribute;

import java.util.List;

public class AttributeHierarchyItem {

    private String className;
    private List<String> parentName;
    private List<XsdAttribute> ownElements;


    AttributeHierarchyItem(String className, List<String> parentName, List<XsdAttribute> ownElements){
        this.className = className;
        this.parentName = parentName;
        this.ownElements = ownElements;
    }

    public String getClassName() {
        return className;
    }

    List<String> getParentsName() {
        return parentName;
    }

    List<XsdAttribute> getOwnElements() {
        return ownElements;
    }
}
