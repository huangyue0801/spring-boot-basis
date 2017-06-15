package com.service.boot.basis.dao;

import com.service.boot.basis.dao.annotation.Column;
import com.service.boot.utils.validator.NumberUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Locale;

public class ColumnField {
    public String column;
    public Field field;
    public Class<?> type;
    public Object defaultValue;
    public boolean primaryKey;
    public boolean foreignKey;

    public ColumnField(Field field, Column column) {
        this.field = field;
        this.field.setAccessible(true);
        this.type = field.getType();
        if (column != null) {
            this.column = column.name();
            if (this.column.trim().length() == 0) {
                this.column = underscoreName(field.getName());
            }
            String value;
            if ((value = column.defaultValue().trim()).length() > 0) {
                setDefaultValue(value);
            }
            this.primaryKey = column.primaryKey();
            this.foreignKey = column.foreignKey();
        }
    }

    private void setDefaultValue(String value) {
        if (type == String.class) {
            defaultValue = value;
        } else if (type == Integer.class || type == Integer.TYPE) {
            if (NumberUtils.isNumber(value)) {
                defaultValue = NumberUtils.parseNumber(value, Integer.class);
            }
        } else if (type == Long.class || type == Long.TYPE) {
            if (NumberUtils.isNumber(value)) {
                defaultValue = NumberUtils.parseNumber(value, Long.class);
            }
        } else if (type == Float.class || type == Float.TYPE) {
            if (NumberUtils.isDouble(value)) {
                defaultValue = NumberUtils.parseNumber(value, Float.class);
            }
        } else if (type == Double.class || type == Double.TYPE) {
            if (NumberUtils.isDouble(value)) {
                defaultValue = NumberUtils.parseNumber(value, Double.class);
            }
        } else if (type == Boolean.class || type == Boolean.TYPE) {
            if ("true".equals(value) || "1".equals(value)) {
                defaultValue = true;
            } else if ("false".equals(value) || "0".equals(value)) {
                defaultValue = false;
            }
        } else if (type == Short.class || type == Short.TYPE) {
            if (NumberUtils.isNumber(value)) {
                defaultValue = NumberUtils.parseNumber(value, Short.class);
            }
        } else if (type == Byte.class || type == Byte.TYPE) {
            if (NumberUtils.isNumber(value)) {
                defaultValue = NumberUtils.parseNumber(value, Byte.class);
            }
        }
    }

    public String underscoreName(String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(lowerCaseName(name.substring(0, 1)));
        for (int i = 1; i < name.length(); i++) {
            String s = name.substring(i, i + 1);
            String slc = lowerCaseName(s);
            if (!s.equals(slc)) {
                result.append("_").append(slc);
            } else {
                result.append(s);
            }
        }
        return result.toString();
    }

    private String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }
}
