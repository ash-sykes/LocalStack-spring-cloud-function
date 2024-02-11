package com.ash.config;

import com.ash.service.ExampleServiceProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(ExampleServiceProperties.class)
@Slf4j
public class ExampleConfig {

    @Bean
    public Clock clock() {
        final Clock systemClock = Clock.systemDefaultZone();
        log.info("System clock is: {}", systemClock);
        return systemClock;
    }

    /**
     * Inspired by aws-lambda-java-serialization.
     * Making our own instance of ObjectMapper and copying their config
     * as Spring looks for an unwrapped object mapper instance
     * i.e. not one returned by the AWS lib of com.amazonaws.thirdparty...
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper(createJsonFactory());
        SerializationConfig scfg = mapper.getSerializationConfig();
        scfg = scfg.withFeatures(
                SerializationFeature.FAIL_ON_SELF_REFERENCES,
                SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS,
                SerializationFeature.WRAP_EXCEPTIONS
        );
        scfg = scfg.withoutFeatures(
                SerializationFeature.CLOSE_CLOSEABLE,
                SerializationFeature.EAGER_SERIALIZER_FETCH,
                SerializationFeature.FAIL_ON_EMPTY_BEANS,
                SerializationFeature.FLUSH_AFTER_WRITE_VALUE,
                SerializationFeature.INDENT_OUTPUT,
                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,
                SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID,
                SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS,
                SerializationFeature.WRAP_ROOT_VALUE
        );
        mapper.setConfig(scfg);

        DeserializationConfig dcfg = mapper.getDeserializationConfig();
        dcfg = dcfg.withFeatures(
                DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE,
                DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS,
                DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
                DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                DeserializationFeature.WRAP_EXCEPTIONS
        );
        dcfg = dcfg.withoutFeatures(
                DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
                DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        );
        mapper.setConfig(dcfg);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //NON_EMPTY?

        mapper.enable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS);
        mapper.enable(MapperFeature.AUTO_DETECT_FIELDS);
        mapper.enable(MapperFeature.AUTO_DETECT_GETTERS);
        mapper.enable(MapperFeature.AUTO_DETECT_IS_GETTERS);
        mapper.enable(MapperFeature.AUTO_DETECT_SETTERS);
        mapper.enable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        mapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
        mapper.enable(MapperFeature.USE_ANNOTATIONS);

        mapper.disable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        mapper.disable(MapperFeature.AUTO_DETECT_CREATORS);
        mapper.disable(MapperFeature.INFER_PROPERTY_MUTATORS);
        mapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
        mapper.disable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        mapper.disable(MapperFeature.USE_STATIC_TYPING);
        mapper.disable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS);

        return mapper;
    }

    private static JsonFactory createJsonFactory() {
        JsonFactory factory = new JsonFactory();

        //Json Parser enabled
        factory.enable(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS);
        factory.enable(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS);
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        factory.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);

        //Json Parser disabled
        factory.disable(JsonParser.Feature.ALLOW_COMMENTS);
        factory.disable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
        factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        factory.disable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

        //Json generator enabled
        factory.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        factory.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        factory.enable(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS);

        //Json generator disabled
        factory.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
        factory.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        factory.disable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
        factory.disable(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM);
        factory.disable(JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION);
        factory.disable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        factory.disable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
        
        return factory;
    }
}
