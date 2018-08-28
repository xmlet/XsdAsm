package org.xmlet.xsdasm.classes;

import java.util.logging.Logger;

class XsdLogger {

    private static final Logger LOGGER = Logger.getLogger("Xsd");

    private XsdLogger() {}

    static Logger getLogger() {
        return LOGGER;
    }
}
