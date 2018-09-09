package org.xmlet.xsdasm.main;

import org.xmlet.xsdasm.classes.XsdAsm;
import org.xmlet.xsdparser.core.XsdParser;

public class XsdAsmMain {

    public static void main(String[] args){
        if (args.length == 2){
            new XsdAsm().generateClassFromElements(new XsdParser(args[0]).getResultXsdElements(), args[1]);
        }
    }
}
