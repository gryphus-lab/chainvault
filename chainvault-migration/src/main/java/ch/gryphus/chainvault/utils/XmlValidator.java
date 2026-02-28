/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

/**
 * The type Xml validator.
 */
@RequiredArgsConstructor
public class XmlValidator {

    private static String xsdPath;

    private static Validator initValidator(String xsdPath) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(new File(xsdPath));
        Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
    }

    /**
     * Is valid boolean.
     *
     * @param xmlString the xml string
     * @return the boolean
     * @throws IOException  the io exception
     * @throws SAXException the sax exception
     */
    public static boolean isValid(String xmlString) {
        try {
            Validator validator = initValidator(xsdPath);
            validator.validate(
                    new StreamSource(IOUtils.toInputStream(xmlString, StandardCharsets.UTF_8)));

            return true;
        } catch (SAXException | IOException e) {
            return false;
        }
    }

    public static void setXsdPath(String xsdPath) {
        XmlValidator.xsdPath = xsdPath;
    }
}
