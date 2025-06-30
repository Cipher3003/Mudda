package com.mudda.backend.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

public class GeoJsonPointDeserializer extends StdDeserializer<GeoJsonPoint> {

    public GeoJsonPointDeserializer() {
        super(GeoJsonPoint.class);
    }

    @Override
    public GeoJsonPoint deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = p.getCodec().readTree(p);

        JsonNode coordinatesNode = node.get("coordinates");
        if (coordinatesNode == null || !coordinatesNode.isArray() || coordinatesNode.size() != 2) {
            throw new IllegalArgumentException("Invalid GeoJSON coordinates");
        }

        double x = coordinatesNode.get(0).asDouble(); // longitude
        double y = coordinatesNode.get(1).asDouble(); // latitude

        return new GeoJsonPoint(x, y);
    }
}

