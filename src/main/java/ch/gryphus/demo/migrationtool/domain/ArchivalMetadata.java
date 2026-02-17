package ch.gryphus.demo.migrationtool.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ArchivalMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArchivalMetadata {

    @JacksonXmlProperty(localName = "Document")
    private Document document;
}
