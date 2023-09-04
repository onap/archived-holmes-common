package org.onap.holmes.common.msb.entity;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class CustomDateSerializerTest {
    @Test
    public void testSerialize() throws IOException {
        Date date = new Date();

        StringWriter writer = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
        SerializerProvider provider = new ObjectMapper().getSerializerProvider();

        CustomDateSerializer serializer = new CustomDateSerializer();
        serializer.serialize(date, jsonGenerator, provider);
        jsonGenerator.flush();

        String expectedOutput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date);
        assertEquals("\"" + expectedOutput + "\"", writer.toString());
    }
}