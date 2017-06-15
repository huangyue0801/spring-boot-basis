package com.service.boot.basis.bind;

import java.util.HashMap;
import java.util.Map;

public class RequestParams<T> {
    public T params;
    public boolean error;
    private Map<String, String> errorMap;

    public void putError(String key, String value) {
        if (errorMap == null) {
            errorMap = new HashMap<>();
        }
    }

    public Map<String, String> getErrorMap() {
        return errorMap;
    }
}
