package com.service.boot.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONObject extends HashMap<String, Object> {

    public JSONObject getJSONObject(String key) {
        Object value = this.get(key);
        if (value != null && value instanceof Map) {
            JSONObject object = new JSONObject();
            object.putAll((Map) value);
            return object;
        }
        return null;
    }

    public JSONArray getJSONArray(String key) {
        Object value = this.get(key);
        if (value != null && value instanceof List) {
            JSONArray array = new JSONArray();
            array.addAll((List) value);
            return array;
        }
        return null;
    }

    private <T> T getValue(String key, Class<T> clazz, Object defaultValue) {
        Object value = this.get(key);
        if (value == null) {
            return (T) defaultValue;
        }
        if (value.getClass() == clazz) {
            return (T) value;
        }
        return null;
    }

    public String getJSONString(String key) {
        Object value = this.get(key);
        if (value != null) {
            if (value.getClass() == String.class) {
                return (String) value;
            } else {
                return JSON.toJSON(value);
            }
        }
        return null;
    }

    public String getJSONStringValue(String key) {
        Object value = this.get(key);
        if (value != null) {
            if (value.getClass() == String.class) {
                return (String) value;
            } else {
                return JSON.toJSON(value);
            }
        }
        return "";
    }

    public String getString(String key) {
        return getValue(key, String.class, null);
    }

    public String getStringValue(String key) {
        return getValue(key, String.class, "");
    }

    public Integer getInteger(String key) {
        return getValue(key, Integer.class, null);
    }

    public Integer getIntegerValue(String key) {
        return getValue(key, Integer.class, 0);
    }

    public Long getLong(String key) {
        return getValue(key, Long.class, null);
    }

    public Long getLongValue(String key) {
        return getValue(key, Long.class, 0L);
    }

    public Float getFloat(String key) {
        return getValue(key, Float.class, null);
    }

    public Float getFloatValue(String key) {
        return getValue(key, Float.class, 0f);
    }

    public Double getDouble(String key) {
        return getValue(key, Double.class, null);
    }

    public Double getDoubleValue(String key) {
        return getValue(key, Double.class, 0d);
    }

    public Short getShort(String key) {
        return getValue(key, Short.class, null);
    }

    public Short getShortValue(String key) {
        return getValue(key, Short.class, 0);
    }

    public String toJSON() {
        return JSON.toJSON(this);
    }
}
