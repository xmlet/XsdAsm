package org.xmlet.xsdasm.classes.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterfaceInfo {
    private String interfaceName;
    private Integer interfaceIndex;
    private List<String> methodNames;
    private List<InterfaceInfo> extendedInterfaces;

    public InterfaceInfo(String interfaceName, Integer interfaceIndex, List<String> methodNames, List<InterfaceInfo> extendedInterfaces){
        this.interfaceName = interfaceName;
        this.interfaceIndex = interfaceIndex;
        this.methodNames = methodNames;
        this.extendedInterfaces = extendedInterfaces;
    }

    public InterfaceInfo(String interfaceName, Integer interfaceIndex, List<String> methodNames, InterfaceInfo[] extendedInterfaces){
        this.interfaceName = interfaceName;
        this.interfaceIndex = interfaceIndex;
        this.methodNames = methodNames;
        this.extendedInterfaces = new ArrayList<>();

        this.extendedInterfaces.addAll(Arrays.asList(extendedInterfaces));
    }

    public InterfaceInfo(String interfaceName, Integer interfaceIndex, List<String> methodNames){
        this.interfaceName = interfaceName;
        this.interfaceIndex = interfaceIndex;
        this.methodNames = methodNames;
        this.extendedInterfaces = new ArrayList<>();
    }

    public InterfaceInfo(String interfaceName, Integer interfaceIndex, InterfaceInfo[] extendedInterfaces){
        this.interfaceName = interfaceName;
        this.interfaceIndex = interfaceIndex;
        this.extendedInterfaces = new ArrayList<>();

        this.extendedInterfaces.addAll(Arrays.asList(extendedInterfaces));
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
