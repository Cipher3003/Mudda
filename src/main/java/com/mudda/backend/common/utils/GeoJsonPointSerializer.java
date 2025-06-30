package com.mudda.backend.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

public class GeoJsonPointSerializer extends StdSerializer<GeoJsonPoint> {

    public GeoJsonPointSerializer() {
        super(GeoJsonPoint.class);
    }

    @Override
    public void serialize(GeoJsonPoint point, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "Point");
        gen.writeArrayFieldStart("coordinates");
        gen.writeNumber(point.getX()); // longitude
        gen.writeNumber(point.getY()); // latitude
        gen.writeEndArray();
        gen.writeEndObject();
    }
}