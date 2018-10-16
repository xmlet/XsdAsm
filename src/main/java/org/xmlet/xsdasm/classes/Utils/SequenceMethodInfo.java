package org.xmlet.xsdasm.classes.utils;

import org.xmlet.xsdparser.xsdelements.XsdAbstractElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents information to help with the generation of Sequence related code.
 */
public class SequenceMethodInfo {

    /**
     * A {@link List} of elements that are present in the sequence.
     */
    private List<XsdAbstractElement> sequenceElements;

    /**
     * A {@link List} of names of the sequence members.
     */
    private List<String> sequenceElementNames;

    /**
     * An incremental value used to distinguish the interface names.
     */
    private int interfaceIndex;

    /**
     * An incremental value used to distinguish between names of unnamed elements present in the sequence.
     */
    private int unnamedIndex;

    public SequenceMethodInfo(List<XsdAbstractElement> sequenceElements, int interfaceIndex, int unnamedIndex){
        this.sequenceElements = new ArrayList<>(sequenceElements);
        this.sequenceElementNames = new ArrayList<>();
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
