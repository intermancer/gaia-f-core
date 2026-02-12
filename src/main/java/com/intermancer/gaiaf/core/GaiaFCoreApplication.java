package com.intermancer.gaiaf.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;

@SpringBootApplication
@EnableAsync
public class GaiaFCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaiaFCoreApplication.class, args);
    }
    
    /**
     * Creates a custom DefaultPrettyPrinter that ensures arrays are always
     * printed with each element on a new line.
     * 
     * @return A configured PrettyPrinter
     */
    @Bean
    public PrettyPrinter prettyPrinter() {
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        DefaultIndenter indenter = new DefaultIndenter("    ", "\n");
        prettyPrinter.indentArraysWith(indenter);
        prettyPrinter.indentObjectsWith(indenter);
        return prettyPrinter;
    }
    
    /**
     * Configures Jackson to output pretty, indented JSON for all responses,
     * including simple collections like List<String> on multiple lines.
     * 
     * @return A configured ObjectMapper that handles all JSON formatting
     */
    @Bean
    public ObjectMapper objectMapper(PrettyPrinter prettyPrinter) {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule for Java 8 date/time types (Instant, etc.)
        mapper.registerModule(new JavaTimeModule());
        
        // Write dates as ISO-8601 strings instead of timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Enable pretty printing with indentation
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Disable features that would compact arrays
        mapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
        
        // Set the custom pretty printer
        mapper.setDefaultPrettyPrinter((DefaultPrettyPrinter) prettyPrinter);
        
        return mapper;
    }
}