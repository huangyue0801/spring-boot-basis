package com.service.boot.json;

import java.util.ArrayList;
import java.util.Map;

public class JSONArray extends ArrayList<Object>{

    public JSONObject getJSONObject(int index){
        Object value = this.get(index);
        if(value != null && value instanceof Map){
            JSONObject object = new JSONObject();
            object.putAll((Map)value);
            return object;
        }
        return null;
    }

    public String toJSON(){
        return JSON.toJSON(this);
    }
}
