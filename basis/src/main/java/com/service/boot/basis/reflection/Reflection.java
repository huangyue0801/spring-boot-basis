package com.service.boot.basis.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class Reflection {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reflection.class);

    public static boolean set(Field field, Object object, Object value) {
        try {
            field.set(object, value);
            return true;
        } catch (IllegalAccessException e) {
            LOGGER.error("反射设置值错误！ field=\"{}\" type=\"{}\" value=\"{}\" valueType=\"{}\"", field.getName(), field.getType().getName(), value, value.getClass().getName());
            LOGGER.error("反射设置值错误！", e);
            return false;
        }
    }

    public static Object get(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            LOGGER.error("反射获取值错误！field=\"{}\" type=\"{}\" object=\"{}\"", field.getName(), field.getType().getName(), object.getClass().getName());
            LOGGER.error("反射获取值错误！", e);
        }
        return null;
    }

}
