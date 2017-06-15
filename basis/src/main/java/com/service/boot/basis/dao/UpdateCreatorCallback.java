package com.service.boot.basis.dao;

import com.service.boot.basis.reflection.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class UpdateCreatorCallback<T> extends PreparedStatementCreatorCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCreatorCallback.class);

    private T t;
    private String where;
    private Object[] values;
    private boolean full;

    public UpdateCreatorCallback(T t) {
        this(t, true);
    }

    public UpdateCreatorCallback(T t, boolean full) {
        this(t, full, null);
    }

    public UpdateCreatorCallback(T t, String where, Object... values) {
        this(t, true, where, values);
    }

    public UpdateCreatorCallback(T t, boolean full, String where, Object... values) {
        this.t = t;
        this.full = full;
        this.where = where;
        this.values = values;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        Pair<String, Object[]> pair;
        if (full) {
            pair = getFullUpdateSql();
        } else {
            pair = getUpdateSql();
        }
        String sql = pair.key;
        PreparedStatement statement = con.prepareStatement(sql);
        int index = 1;
        for (Object value : pair.value) {
            statement.setObject(index, value);
            index++;
        }
        statement.execute();
        return statement;
    }

    @Override
    public Integer doInPreparedStatement(PreparedStatement statement) throws SQLException, DataAccessException {
        return t != null ? 1 : 0;
    }

    public Pair<String, Object[]> getUpdateSql() {
        Class clazz = t.getClass();
        Map<String, ColumnField> map = getCacheFieldMap(clazz);
        StringBuilder sql = new StringBuilder("UPDATE `");
        sql.append(getCacheTable(clazz));
        sql.append("` SET ");
        int index = 0;
        ColumnField pkColumnField = null;
        String pkColumn = null;
        Object[] values = new Object[map.size()];
        for (Map.Entry<String, ColumnField> entry : map.entrySet()) {
            String column = entry.getKey();
            ColumnField model = entry.getValue();
            if (model.primaryKey) {
                pkColumn = entry.getKey();
                pkColumnField = model;
                continue;
            }
            Object value = Reflection.get(model.field, t);
            if (value != null) {
                sql.append("`").append(column).append("`=?,");
                values[index] = value;
                index++;
            }
        }
        int d = sql.lastIndexOf(",");
        if (d <= 0) {
            LOGGER.error("没有找到要更新的字段 !!");
            return null;
        }
        sql.deleteCharAt(d);
        if (where != null) {
            sql.append(" WHERE ").append(where).append(";");
        } else if (pkColumn != null) {
            sql.append(" WHERE `").append(pkColumn).append("`=?;");
            Object value = Reflection.get(pkColumnField.field, t);
            if (value == null) {
                LOGGER.error("主键字段没有值 !!");
                return null;
            }
            values[index] = value;
            index++;
        } else {
            LOGGER.error("更新没有找到where条件，没有主键或者没有where条件");
            return null;
        }
        int inValueSize = 0;
        if (this.values != null) {
            inValueSize = this.values.length;
        }
        Object[] newValues = new Object[index + inValueSize];
        System.arraycopy(values, 0, newValues, 0, index);
        if (inValueSize > 0) {
            System.arraycopy(this.values, 0, newValues, index, inValueSize);
        }
        LOGGER.info("更新 SQL=\"{}\"", sql.toString());
        return new Pair<>(sql.toString(), newValues);
    }

    public Pair<String, Object[]> getFullUpdateSql() {
        Class clazz = t.getClass();
        Map<String, ColumnField> map = getCacheFieldMap(clazz);
        StringBuilder sql = new StringBuilder("UPDATE `");
        sql.append(getCacheTable(clazz));
        sql.append("` SET ");
        int index = 0;
        ColumnField pkColumnField = null;
        String pkColumn = null;
        Object[] values = new Object[map.size()];
        for (Map.Entry<String, ColumnField> entry : map.entrySet()) {
            String column = entry.getKey();
            ColumnField model = entry.getValue();
            if (model.primaryKey) {
                pkColumn = entry.getKey();
                pkColumnField = model;
                continue;
            }
            sql.append("`").append(column).append("`=?,");
            values[index] = Reflection.get(model.field, t);
            index++;
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        if (where != null) {
            sql.append(" WHERE ").append(where).append(";");
        } else if (pkColumn != null) {
            sql.append(" WHERE `").append(pkColumn).append("`=?;");
            Object value = Reflection.get(pkColumnField.field, t);
            if (value == null) {
                LOGGER.error("主键字段没有值 !!");
                return null;
            }
            values[index] = value;
            index++;
        } else {
            LOGGER.error("更新没有找到where条件，没有主键或者没有where条件");
            return null;
        }
        int inValueSize = 0;
        if (this.values != null) {
            inValueSize = this.values.length;
        }
        Object[] newValues = new Object[index + inValueSize];
        System.arraycopy(values, 0, newValues, 0, index);
        if (inValueSize > 0) {
            System.arraycopy(this.values, 0, newValues, index, inValueSize);
        }
        LOGGER.info("更新 SQL=\"{}\"", sql.toString());
        return new Pair<>(sql.toString(), newValues);
    }
}
