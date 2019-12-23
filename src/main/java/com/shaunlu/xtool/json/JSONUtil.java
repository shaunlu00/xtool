package com.shaunlu.xtool.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shaunlu.xtool.error.ErrorCode;
import com.shaunlu.xtool.error.XToolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class JSONUtil {

    private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    public JSONUtil() {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public JSONUtil(SimpleDateFormat dateFormat){
        objectMapper.setDateFormat(dateFormat);
    }

    public String objToStr(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("JSON Serialize Error", e);
            throw new XToolException(ErrorCode.JSON_SERIALIZE_ERROR, e);
        }
    }

    public <T> T strToObj(String str, Class<T> tClass) {
        try {
            return (T) objectMapper.readValue(str, tClass);
        } catch (IOException e) {
            logger.error("JSON Deserialize Error", e);
            throw new XToolException(ErrorCode.JSON_DESERIALIZE_ERROR, e);
        }
    }


    public JsonNode getJSONNode(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (IOException e) {
            logger.error("JSON Deserialize To JSONNode Error", e);
            throw new XToolException(ErrorCode.JSON_DESERIALIZE_ERROR, e);
        }
    }

    public <T> List<T> toCollection(String str, Class<T> tClass) {
        try {
            return objectMapper.readValue(str, objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (IOException e) {
            logger.error("JSON Collection Deserialize Error", e);
            throw new XToolException(ErrorCode.JSON_DESERIALIZE_ERROR, e);
        }
    }

}
