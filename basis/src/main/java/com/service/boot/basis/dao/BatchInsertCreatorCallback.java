package com.service.boot.basis.dao;

import com.service.boot.basis.reflection.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchInsertCreatorCallback<T> extends PreparedStatementCreatorCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchInsertCreatorCallback.class);
    private Class clazz;
    private Map<Integer, ColumnField> indexFieldModelMap;
    private List<T> list;
    private ColumnField pkColumnField;

    public BatchInsertCreatorCallback(List<T> list) {
        this.list = list;
        if (list == null || list.isEmpty()) {
            LOGGER.error("INSERT对象为NULL");
            throw new NullPointerException("batch insert list is null!!!");
        }
        this.clazz = list.get(0).getClass();
        indexFieldModelMap = new HashMap<>();
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement statement = con.prepareStatement(getInsertSql(), Statement.RETURN_GENERATED_KEYS);
        for (T t : list) {
            for (Map.Entry<Integer, ColumnField> entry : indexFieldModelMap.entrySet()) {
                ColumnField columnField = entry.getValue();
                Object value = Reflection.get(columnField.field, t);
                if (value == null) {
                    value = columnField.defaultValue;
                }
                statement.setObject(entry.getKey(), value);
            }
            statement.addBatch();
        }
        statement.executeBatch();
        return statement;
    }

    @Override
    public Integer doInPreparedStatement(PreparedStatement statement) throws SQLException, DataAccessException {
        ResultSet rs = statement.getGeneratedKeys();
        int index = 0;
        while (rs.next()) {
            Object id = rs.getInt(1);
            Reflection.set(pkColumnField.field, list.get(index), id);
            index++;
        }
        JdbcUtils.closeResultSet(rs);
        return list.size();
    }

    public String getInsertSql() {
        Map<String, ColumnField> map = getCacheFieldMap(clazz);
        StringBuilder sql = new StringBuilder("INSERT INTO `");
        sql.append(getCacheTable(clazz));
        sql.append("`(");
        int index = 1;
        for (Map.Entry<String, ColumnField> entry : map.entrySet()) {
            String column = entry.getKey();
            ColumnField model = entry.getValue();
            if (model.primaryKey) {
                pkColumnField = model;
                continue;
            }
            sql.append("`").append(column).append("`,");
            indexFieldModelMap.put(index, model);
            index++;
        }
        sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
        sql.append(" VALUES ").append("(");
        for (int i = 1; i < index; i++) {
            if ((i + 1) == index) {
                sql.append("?");
            } else {
                sql.append("?,");
            }
        }
        sql.append(");");
        LOGGER.info("插入 SQL=\"{}\"", sql.toString());
        return sql.toString();
    }
}
