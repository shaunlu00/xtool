package org.crudboy.toolbar.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.crudboy.toolbar.exception.ToolbarRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class JSONUtil {

    private JSONUtil() {
    }

    private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static String toString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("JSON Util Error - object to str", e);
            throw new ToolbarRuntimeException("JSON Util Error - object to str", e);
        }
    }

    public static <T> T toObject(String str, Class<T> tClass) {
        try {
            return (T) objectMapper.readValue(str, tClass);
        } catch (IOException e) {
            logger.error("JSON Util Error - str to object", e);
            throw new ToolbarRuntimeException("JSON Util Error - str to object", e);
        }
    }

    public static Object toDynamicObject(String content, String typeField) {
        try {
            JsonNode jsonNode = objectMapper.readTree(content);
            Class objType = Class.forName(jsonNode.get(typeField).asText());
            return toObject(content, objType);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("JSON Util Error - str to dynamic object", e);
            throw new ToolbarRuntimeException("JSON Util Error - str to dynamic object", e);
        }
    }

    public static JsonNode getJSONNode(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (IOException e) {
            logger.error("JSON Util Error - get JSON node", e);
            throw new ToolbarRuntimeException("JSON Util Error - get JSON node", e);
        }
    }

    public static <T> List<T> toArray(String str, Class<T> tClass) {
        try {
            return objectMapper.readValue(str, objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (IOException e) {
            logger.error("JSON Util Error - str to array", e);
            throw new ToolbarRuntimeException("JSON Util Error - str to array", e);
        }
    }

}
