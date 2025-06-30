package com.mudda.backend.common.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mudda.backend.common.utils.GeoJsonPointDeserializer;
import com.mudda.backend.common.utils.GeoJsonPointSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Configuration
public class JacksonConfig {

    @Bean
    public Module geoJsonPointModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(GeoJsonPoint.class, new GeoJsonPointSerializer());
        module.addDeserializer(GeoJsonPoint.class, new GeoJsonPointDeserializer());
        return module;
    }
}
