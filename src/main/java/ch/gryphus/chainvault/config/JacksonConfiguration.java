package ch.gryphus.chainvault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper(); // Ensures a standard JSON ObjectMapper is Primary
    }

    // This forces the JSON converter to the front of the line
    @Bean
    public WebMvcConfigurer swapOrder() {
        return new WebMvcConfigurer() {
            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                // Find the JSON converter and move it to index 0
                converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                converters.addFirst(new MappingJackson2HttpMessageConverter());
            }
        };
    }
}