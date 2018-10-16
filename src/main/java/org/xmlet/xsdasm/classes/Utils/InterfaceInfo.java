package org.xmlet.xsdasm.classes.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents information regarding element interfaces.
 */
public class InterfaceInfo {

    /**
     * The name of the interface that will be generated based on this class information.
     */
    private String interfaceName;

    /**
     * The interface index of the interface that will be generated based on this class information. This index is a value
     * based on an integer value that is incremented in order to avoid name repetition on the generated interfaces.
     */
    private Integer interfaceIndex;

    /**
     * A {@link List} of {@link String} objects. Each string represents a method name and by extension its return type.
     * Example:
     *  Method name: html
     *  public void Html<Z> html() { new Html<>(); }
     */
    private List<String> methodNames;

    /**
     * A {@link List} of {@link InterfaceInfo} objects. It contains information about the interfaces that will be extended
     * by the interface that will be generated based on the information present in an instance of this class.
     */
    private List<InterfaceInfo> extendedInterfaces;

    public InterfaceInfo(String interfaceName, Integer interfaceIndex, List<String> methodNames, List<InterfaceInfo> extendedInterfaces){
        this.interfaceName = interfaceName;
        this.interfaceIndex = interfaceIndex;
        this.methodNames = methodNames;
        this.extendedInterfaces = extendedInterfaces;
    }

    public InterfaceInfo(String interfaceName, Integer interfaceIndex, List<String> methodNames){
        this.interfaceName = interfaceName;
        this.interfaceIndex = interfaceIndex;
        this.methodNames = methodNames;
        this.extendedInterfaces = new ArrayList<>();
    }

    public InterfaceInfo(String interfaceName){
        this.interfaceName = interfaceName;
        this.interfaceIndex = 0;
        this.extendedInterfaces = new ArrayList<>();
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public Integer getInterfaceIndex() {
        return interfaceIndex;
    }

    public List<InterfaceInfo> getExtendedInterfaces() {
        return extendedInterfaces;
    }

    public List<String> getMethodNames(){
        return methodNames;
    }
}
