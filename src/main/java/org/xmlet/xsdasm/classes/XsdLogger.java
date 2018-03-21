package org.xmlet.xsdasm.classes;

import java.util.logging.Logger;

public class XsdLogger {

    private static final Logger LOGGER = Logger.getLogger("Xsd");

    private XsdLogger() {}

    public static Logger getLogger() {
        return LOGGER;
    }
}
