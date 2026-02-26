package ch.gryphus.chainvault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class SerializationConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper(); // prioritize json
    }

    @Bean
    public XmlMapper xmlMapper(Jackson2ObjectMapperBuilder builder) {
        // reuse spring's default mapper configuration (modules, date formats, etc.)
        // instruct builder to create an XmlMapper rather than plain ObjectMapper
        return builder.createXmlMapper(true).build();
    }

    @Bean
    public Tika tika() {
        return new Tika();
    }
}
