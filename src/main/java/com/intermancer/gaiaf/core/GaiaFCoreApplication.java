package com.intermancer.gaiaf.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;

@SpringBootApplication
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
        
        // Enable pretty printing with indentation
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Disable features that would compact arrays
        mapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
        
        // Set the custom pretty printer
        mapper.setDefaultPrettyPrinter((DefaultPrettyPrinter) prettyPrinter);
        
        return mapper;
    }
}