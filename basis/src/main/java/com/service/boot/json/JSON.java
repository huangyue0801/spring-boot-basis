package com.service.boot.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSON {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSON.class);

    private static final ObjectMapper OM = new ObjectMapper();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String ERROR_JSON = "{\"code\":500, \"message\":\"服务器错误\"}";

    static {
        OM.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OM.setDateFormat(DATE_FORMAT);
    }

    public static <T> List<T> parseList(String json, Class<T> clazz) {
        try {
            return OM.readValue(json, OM.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
        } catch (IOException e) {
            LOGGER.error("JSON解析异常！", e);
            return java.util.Collections.emptyList();
        }
    }

    public static <K, V> Map<K, V> parseMap(String json, Class<K> keyClazz, Class<V> valueClazz) {
        try {
            return OM.readValue(json, OM.getTypeFactory().constructMapType(HashMap.class, keyClazz, valueClazz));
        } catch (IOException e) {
            LOGGER.error("JSON解析异常！", e);
            return java.util.Collections.emptyMap();
        }
    }

    public static Map<String, Object> parseMap(String json) {
        return parseMap(json, String.class, Object.class);
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return parseList(json, clazz);
    }

    public static JSONArray parseArray(String json) {
        return parseObject(json, JSONArray.class);
    }

    public static <T> T parseBean(String json, Class<T> clazz) {
        return parseObject(json, clazz);
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return OM.readValue(json, clazz);
        } catch (IOException e) {
            LOGGER.error("JSON解析异常！", e);
            return null;
        }
    }

    public static JSONObject parseObject(String json) {
        return parseObject(json, JSONObject.class);
    }

    public static String toJSON(Object object) {
        try {
            return OM.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("JSON解析异常！", e);
            return ERROR_JSON;
        }
    }

    public static String toJSONString(Object object) {
        return toJSON(object);
    }

}
