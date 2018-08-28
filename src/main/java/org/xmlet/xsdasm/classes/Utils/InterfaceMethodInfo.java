package org.xmlet.xsdasm.classes.Utils;

public class InterfaceMethodInfo {

    private String methodName;
    private String interfaceName;

    public InterfaceMethodInfo(String methodName, String interfaceName){
        this.methodName = methodName;
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }
}
