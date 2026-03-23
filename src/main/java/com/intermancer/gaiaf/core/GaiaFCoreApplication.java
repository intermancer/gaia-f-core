package com.intermancer.gaiaf.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.SerializationFeature;

@SpringBootApplication
@EnableAsync
public class GaiaFCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaiaFCoreApplication.class, args);
    }

    /**
     * Configures Jackson 3 to output pretty, indented JSON for all responses,
     * including simple collections such as List&lt;String&gt; on multiple lines.
     * Java date/time support (formerly JavaTimeModule) is built into Jackson 3
     * and requires no explicit registration.
     *
     * @return a {@link JsonMapperBuilderCustomizer} that applies global JSON formatting
     */
    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return jsonMapperBuilder -> {
            // Jackson 3 defaults: dates serialized as ISO-8601 strings, not timestamps.
            // No explicit date/time configuration is required.

            // Enable indented pretty-printing
            jsonMapperBuilder.enable(SerializationFeature.INDENT_OUTPUT);

            // Keep single-element arrays wrapped so collections are always multi-line
            jsonMapperBuilder.disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

            // Custom pretty printer: 4-space indentation with each element on its own line
            DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            DefaultIndenter indenter = new DefaultIndenter("    ", "\n");
            prettyPrinter.indentArraysWith(indenter);
            prettyPrinter.indentObjectsWith(indenter);
            jsonMapperBuilder.defaultPrettyPrinter(prettyPrinter);
        };
    }
}