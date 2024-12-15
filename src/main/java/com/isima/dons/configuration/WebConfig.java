package com.isima.dons.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Enable content negotiation based on the 'Accept' header
        configurer.favorParameter(false); // Disable content negotiation via query parameters
        configurer.ignoreAcceptHeader(false); // Don't ignore the Accept header
        configurer.defaultContentType(MediaType.APPLICATION_JSON); // Default to JSON

        // Map 'application/xml' to 'xml' and 'application/json' to 'json'
        configurer.mediaType("xml", MediaType.APPLICATION_XML);
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
    }

}
