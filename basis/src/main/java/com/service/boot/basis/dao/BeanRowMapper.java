package com.service.boot.basis.dao;

import com.service.boot.basis.dao.annotation.Column;
import com.service.boot.basis.reflection.Reflection;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BeanRowMapper<T> implements RowMapper<T> {

    private static final Map<Class<?>, Map<String, ColumnField>> COLUMN_FIELD_CACHE = new HashMap<>();
    private Class<T> clazz;
    private boolean isAnnotation;
    private Map<String, ColumnField> columnFieldMap;

    public BeanRowMapper(Class<T> clazz) {
        this(clazz, false);
    }

    public BeanRowMapper(Class<T> clazz, boolean isAnnotation) {
        this.clazz = clazz;
        this.isAnnotation = isAnnotation;
        columnFieldMap = getCacheFieldMap(clazz);
    }

    public static <T> BeanRowMapper<T> newInstance(Class<T> clazz) {
        return newInstance(clazz, false);
    }

    public static <T> BeanRowMapper<T> newInstance(Class<T> clazz, boolean isAnnotation) {
        return new BeanRowMapper<T>(clazz, isAnnotation);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T t = null;
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            t = clazz.newInstance();
            for (int index = 1; index <= columnCount; index++) {
                String column = JdbcUtils.lookupColumnName(rsmd, index);
                ColumnField columnField = columnFieldMap.get(column);
                if (columnField != null) {
                    Object value = JdbcUtils.getResultSetValue(rs, index, columnField.type);
                    Reflection.set(columnField.field, t, value);
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    private Map<String, ColumnField> getCacheFieldMap(Class<?> clazz) {
        Map<String, ColumnField> cache = COLUMN_FIELD_CACHE.get(clazz);
        if (cache == null) {
            cache = new HashMap<>();
            putColumnField(cache, clazz.getDeclaredFields());
            COLUMN_FIELD_CACHE.put(clazz, cache);
        }
        return cache;
    }

    private void putColumnField(Map<String, ColumnField> cache, Field[] fields){
        ColumnField columnField;
        for (Field field : fields) {
            if (isAnnotation) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columnField = new ColumnField(field, column);
                    cache.put(columnField.column, columnField);
                }
            } else {
                columnField = new ColumnField(field, null);
                String column = columnField.underscoreName(field.getName());
                columnField.column = column;
                cache.put(column, columnField);
            }
        }
    }

}
