/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.xml.XmlMapper;

/**
 * The type Serialization config.
 */
@Configuration
public class SerializationConfig {

    /**
     * Object mapper object mapper.
     *
     * @return the object mapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Xml mapper xml mapper.
     *
     * @return the xml mapper
     */
    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    /**
     * Tika tika.
     *
     * @return the tika
     */
    @Bean
    public Tika tika() {
        return new Tika();
    }
}
