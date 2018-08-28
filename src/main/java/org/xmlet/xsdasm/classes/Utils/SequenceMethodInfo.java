package org.xmlet.xsdasm.classes.Utils;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;

import java.util.ArrayList;
import java.util.List;

public class SequenceMethodInfo {

    private List<XsdAbstractElement> sequenceElements;
    private List<String> sequenceElementNames;
    private int interfaceIndex;
    private int unnamedIndex;

    public SequenceMethodInfo(List<XsdAbstractElement> sequenceElements, int interfaceIndex, int unnamedIndex){
        this.sequenceElements = new ArrayList<>(sequenceElements);
        this.sequenceElementNames = new ArrayList<>();
        this.interfaceIndex = interfaceIndex;
        this.unnamedIndex = unnamedIndex;
    }

    public SequenceMethodInfo(List<XsdAbstractElement> sequenceElements, List<String> sequenceElementNames, int interfaceIndex, int unnamedIndex){
        this.sequenceElements = sequenceElements;
        this.sequenceElementNames = sequenceElementNames;
        this.interfaceIndex = interfaceIndex;
        this.unnamedIndex = unnamedIndex;
    }

    private int getInterfaceIndex() {
        return interfaceIndex;
    }

    public void setInterfaceIndex(int interfaceIndex) {
        this.interfaceIndex = interfaceIndex;
    }

    public int getUnnamedIndex() {
        return unnamedIndex;
    }

    public List<String> getSequenceElementNames() {
        return sequenceElementNames;
    }

    public List<XsdAbstractElement> getSequenceElements() {
        return sequenceElements;
    }

    public void addElementName(String sequenceElementName){
        this.sequenceElementNames.add(sequenceElementName);
    }

    public void incrementUnnamedIndex() {
        ++unnamedIndex;
    }

    public void receiveChildSequence(SequenceMethodInfo childSequenceInfo){
        sequenceElements.addAll(childSequenceInfo.getSequenceElements());
        sequenceElementNames.addAll(childSequenceInfo.getSequenceElementNames());
        interfaceIndex = childSequenceInfo.getInterfaceIndex();
        unnamedIndex = childSequenceInfo.getUnnamedIndex();
    }
}
