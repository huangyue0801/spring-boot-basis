package com.service.boot.basis.bind;

import com.service.boot.basis.bind.annotation.Param;
import com.service.boot.json.JSON;
import com.service.boot.utils.formats.DateTimeFormat;
import com.service.boot.utils.validator.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestDataBind {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDataBind.class);

    private static final Map<Class<?>, Map<String, ParamField>> PARAMS_FIELD_CACHE = new HashMap<>();

    public static <T> T bindRequest(Class<T> clazz, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        try {
            Map<String, ParamField> paramsFieldMap = getParamsFieldMap(clazz);
            T data = clazz.newInstance();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (values != null && values.length > 0) {
                    setValue(null, paramsFieldMap, data, key, values);
                } else {
                    LOGGER.warn("参数 {}=\"null\"", key);
                }
            }
            return data;
        } catch (Exception e) {
            LOGGER.error("创建绑定参数对象失败！！ class=\"{}\"", clazz);
            return null;
        }
    }

    public static <T> RequestParams<T> bindRequestParams(Class<T> clazz, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        try {
            Map<String, ParamField> paramsFieldMap = getParamsFieldMap(clazz);
            RequestParams<T> params = new RequestParams<T>();
            T data = clazz.newInstance();
            params.params = data;
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (values != null && values.length > 0) {
                    setValue(params, paramsFieldMap, data, key, values);
                } else {
                    LOGGER.warn("参数 {}=\"null\"", key);
                }
            }
            return params;
        } catch (Exception e) {
            LOGGER.error("创建绑定参数对象失败！！请加入无参构造方法！ class=\"{}\"", clazz);
            return null;
        }
    }

    public static <T> T getParams(Class<T> returnType, String key, HttpServletRequest request) {
        String value = request.getParameter(key);
        if (value == null || "".equals(value.trim())) {
            return null;
        }
        if (returnType == String.class) {
            return (T) value;
        } else if (returnType == Integer.class || returnType == Integer.TYPE) {
            if (NumberUtils.isNumber(value)) {
                return (T) Integer.valueOf(value);
            }
            LOGGER.warn("参数格式错误 {}=\"{}\"", key, value);
        } else if (returnType == Long.class || returnType == Long.TYPE) {
            if (NumberUtils.isNumber(value)) {
                return (T) Long.valueOf(value);
            }
            LOGGER.warn("参数格式错误 {}=\"{}\"", key, value);
        } else if (returnType == Float.class || returnType == Float.TYPE) {
            if (NumberUtils.isDouble(value)) {
                return (T) Float.valueOf(value);
            }
            LOGGER.warn("参数格式错误 {}=\"{}\"", key, value);
        } else if (returnType == Double.class || returnType == Double.TYPE) {
            if (NumberUtils.isDouble(value)) {
                return (T) Double.valueOf(value);
            }
            LOGGER.warn("参数格式错误 {}=\"{}\"", key, value);
        } else if (returnType == Boolean.class || returnType == Boolean.TYPE) {
            if ("true".equals(value) || "1".equals(value)) {
                return (T) Boolean.valueOf(true);
            } else if ("false".equals(value) || "0".equals(value)) {
                return (T) Boolean.valueOf(false);
            }
            LOGGER.warn("参数格式错误 {}=\"{}\"", key, value);
        } else if (returnType == Short.class || returnType == Short.TYPE) {
            if (NumberUtils.isNumber(value)) {
                return (T) Short.valueOf(value);
            }
            LOGGER.warn("参数格式错误 {}=\"{}\"", key, value);
        } else if (returnType == Date.class) {
            Date date;
            if (value.contains(":")) {
                date = DateTimeFormat.formatDateTime(value);
            } else {
                date = DateTimeFormat.formatDate(value);
            }
            if (date != null) {
                return (T) date;
            }
            LOGGER.warn("参数格式错误 {}=\"{}\"", key, value);
        } else {
            LOGGER.warn("参数不支持转换 returnType=\"{}\" key=\"{}\" value=\"{}\"", key, value);
        }
        return null;
    }

    private static void setValue(RequestParams params, Map<String, ParamField> paramsFieldMap, Object data, String key, String[] values) {
        ParamField pf = paramsFieldMap.get(key);
        if (pf == null) {
            return;
        }
        int length = values.length;
        String endValue = values[length - 1].trim();
        if ("".equals(endValue)) {
            LOGGER.warn("参数 \"{}\" 值为NULL 不绑定！！", key);
            return;
        }
        if (pf.type == String.class) {
            pf.set(params, data, endValue);
        } else if (pf.type == Integer.TYPE || pf.type == Integer.class) {
            if (NumberUtils.isNumber(endValue)) {
                pf.set(params, data, Integer.parseInt(endValue));
            } else {
                LOGGER.warn("参数格式错误 {}=\"{}\"", key, endValue);
            }
        } else if (pf.type == Long.TYPE || pf.type == Long.class) {
            if (NumberUtils.isNumber(endValue)) {
                pf.set(params, data, Long.parseLong(endValue));
            } else {
                LOGGER.warn("参数格式错误 {}=\"{}\"", key, endValue);
            }
        } else if (pf.type == Float.TYPE || pf.type == Float.class) {
            if (NumberUtils.isDouble(endValue)) {
                pf.set(params, data, Float.parseFloat(endValue));
            } else {
                LOGGER.warn("参数格式错误 {}=\"{}\"", key, endValue);
            }
        } else if (pf.type == Double.TYPE || pf.type == Double.class) {
            if (NumberUtils.isDouble(endValue)) {
                pf.set(params, data, Double.parseDouble(endValue));
            } else {
                LOGGER.warn("参数格式错误 {}=\"{}\"", key, endValue);
            }
        } else if (pf.type == Boolean.class || pf.type == Boolean.TYPE) {
            String value = endValue.trim();
            if ("true".equals(value) || "1".equals(value)) {
                pf.set(params, data, true);
            } else if ("false".equals(value) || "0".equals(value)) {
                pf.set(params, data, false);
            } else {
                LOGGER.warn("参数格式错误 {}=\"{}\"", key, endValue);
            }
        } else if (pf.type == Short.class || pf.type == Short.TYPE) {
            if (NumberUtils.isNumber(endValue)) {
                pf.set(params, data, Short.parseShort(endValue));
            } else {
                LOGGER.warn("参数格式错误 {}=\"{}\"", key, endValue);
            }
        } else if (pf.type == Date.class) {
            if (pf.pattern != null) {
                Date date;
                if (Pattern.compile("[HhmSs]").matcher(pf.pattern).find()) {
                    date = DateTimeFormat.formatDateTime(pf.pattern, endValue);
                } else {
                    date = DateTimeFormat.formatDate(pf.pattern, endValue);
                }
                if (date != null) {
                    pf.set(params, data, date);
                } else {
                    LOGGER.warn("日期时间参数格式错误 {}=\"{}\" pattern=\"{}\"", key, endValue, pf.pattern);
                }
            } else {
                Date date;
                if (endValue.contains(":")) {
                    date = DateTimeFormat.formatDateTime(endValue);
                } else {
                    date = DateTimeFormat.formatDate(endValue);
                }
                if (date != null) {
                    pf.set(params, data, date);
                } else {
                    LOGGER.warn("日期时间参数格式错误 {}=\"{}\"", key, endValue);
                }
            }
        } else {
            LOGGER.warn("参数不支持绑定 returnType=\"{}\" key=\"{}\" value=\"{}\"", pf.type.getName(), key, endValue);
        }
    }

    private static Map<String, ParamField> getParamsFieldMap(Class<?> clazz) {
        Map<String, ParamField> paramsFieldMap = PARAMS_FIELD_CACHE.get(clazz);
        if (paramsFieldMap == null) {
            paramsFieldMap = new HashMap<>();
            Field[] fields = clazz.getDeclaredFields();
            ParamField paramField;
            for (Field field : fields) {
                Param param = field.getAnnotation(Param.class);
                if (param != null && !StringUtils.isEmpty(param.name())) {
                    String name = param.name();
                    if ("".equals(name.trim())) {
                        paramField = new ParamField(field.getName(), field, param);
                        paramsFieldMap.put(field.getName(), paramField);
                    } else {
                        paramField = new ParamField(name, field, param);
                        paramsFieldMap.put(name, paramField);
                    }
                } else {
                    paramField = new ParamField(field.getName(), field, param);
                    paramsFieldMap.put(field.getName(), paramField);
                }
            }
            PARAMS_FIELD_CACHE.put(clazz, paramsFieldMap);
        }
        return paramsFieldMap;
    }

    public static String getJSONBody(HttpServletRequest request) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            if (sb.length() > 0) {
                return sb.toString().replaceAll("\\s*|\t|\r|\n", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getJSONBodyToBean(HttpServletRequest request, Class<T> clazz){
        String json = getJSONBody(request);
        if(json != null){
            return JSON.parseBean(json, clazz);
        }
        return null;
    }

}
