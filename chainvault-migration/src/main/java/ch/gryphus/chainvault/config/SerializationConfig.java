package ch.gryphus.chainvault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

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
     * @param builder the builder
     * @return the xml mapper
     */
    @Bean
    public XmlMapper xmlMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(true).build();
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
