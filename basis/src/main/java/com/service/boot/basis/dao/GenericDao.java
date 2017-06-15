package com.service.boot.basis.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

public class GenericDao {

    @Resource
    protected JdbcTemplate jdbc;

    public <T> int insert(T t) {
        return inserts(Collections.singletonList(t));
    }

    public <T> int inserts(List<T> list) {
        BatchInsertCreatorCallback<T> insertCreatorCallback = new BatchInsertCreatorCallback<>(list);
        return jdbc.execute(insertCreatorCallback, insertCreatorCallback);
    }

    public <T> int update(T t) {
        UpdateCreatorCallback<T> creatorCallback = new UpdateCreatorCallback<T>(t);
        return jdbc.execute(creatorCallback, creatorCallback);
    }

    public <T> int update(T t, boolean full) {
        UpdateCreatorCallback<T> creatorCallback = new UpdateCreatorCallback<T>(t, full);
        return jdbc.execute(creatorCallback, creatorCallback);
    }

    protected <T> int update(T t, String where, Object... values) {
        UpdateCreatorCallback<T> creatorCallback = new UpdateCreatorCallback<T>(t, where, values);
        return jdbc.execute(creatorCallback, creatorCallback);
    }

    protected <T> int update(T t, boolean full, String where, Object... values) {
        UpdateCreatorCallback<T> creatorCallback = new UpdateCreatorCallback<T>(t, full, where, values);
        return jdbc.execute(creatorCallback, creatorCallback);
    }

    protected <T> T get(String sql, Class<T> clazz, Object... params) {
        List<T> list = jdbc.query(sql, BeanRowMapper.newInstance(clazz), params);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    protected <T> List<T> list(String sql, Class<T> clazz, Object... params) {
        List<T> list = jdbc.query(sql, BeanRowMapper.newInstance(clazz), params);
        return list != null ? list : Collections.emptyList();
    }
}
