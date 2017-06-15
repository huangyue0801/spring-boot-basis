package com.service.boot.basis.dao;

import com.service.boot.basis.dao.annotation.Column;
import com.service.boot.basis.dao.annotation.Table;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class PreparedStatementCreatorCallback implements PreparedStatementCreator, PreparedStatementCallback<Integer> {

    private static final Map<Class<?>, Map<String, ColumnField>> COLUMN_FIELD_CACHE = new HashMap<>();
    private static final Map<Class<?>, String> CLASS_TABLE_CACHE = new HashMap<>();

    protected static String getCacheTable(Class<?> clazz) {
        if (CLASS_TABLE_CACHE.get(clazz) == null) {
            Table table = clazz.getAnnotation(Table.class);
            if (table != null) {
                CLASS_TABLE_CACHE.put(clazz, table.name());
            }
        }
        return CLASS_TABLE_CACHE.get(clazz);
    }

    protected static Map<String, ColumnField> getCacheFieldMap(Class<?> clazz) {
        Map<String, ColumnField> cache = COLUMN_FIELD_CACHE.get(clazz);
        if (cache == null) {
            cache = new HashMap<>();
            Field[] fields = clazz.getDeclaredFields();
            ColumnField columnField;
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columnField = new ColumnField(field, column);
                    cache.put(columnField.column, columnField);
                }
            }
        }
        COLUMN_FIELD_CACHE.put(clazz, cache);
        return cache;
    }

}
