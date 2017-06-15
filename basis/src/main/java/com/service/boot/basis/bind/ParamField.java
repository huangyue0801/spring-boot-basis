package com.service.boot.basis.bind;

import com.service.boot.basis.bind.annotation.Param;
import com.service.boot.basis.reflection.Reflection;

import java.lang.reflect.Field;

public class ParamField {

    public String name;
    public Field field;
    public Class<?> type;
    public String pattern;
    public boolean nullable = true;

    public ParamField(String name, Field field, Param param) {
        field.setAccessible(true);
        this.type = field.getType();
        this.name = name;
        this.field = field;
        if (param != null) {
            this.pattern = "".equals(param.pattern().trim()) ? null : param.pattern().trim();
            this.nullable = param.nullable();
        }
    }

    public boolean set(RequestParams params, Object data, Object value) {
        if (params != null) {
            if (!nullable) {
                params.error = true;
                params.putError(name, name + "参数不能为空！");
            }
        }
        return Reflection.set(field, data, value);
    }
}
