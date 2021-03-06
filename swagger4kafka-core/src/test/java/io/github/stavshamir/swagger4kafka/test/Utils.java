package io.github.stavshamir.swagger4kafka.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Utils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map jsonResourceAsMap(Class<?> testClass, String path) throws IOException {
        InputStream s = testClass.getResourceAsStream(path);
        String json = IOUtils.toString(s, StandardCharsets.UTF_8);
        return objectMapper.readValue(json, Map.class);
    }

}
