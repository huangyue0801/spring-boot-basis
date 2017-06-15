package com.sample;

import okhttp3.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Http {

    public static void main(String[] args) throws Exception {
        Map<String, Object> params = new HashMap<>();
        System.out.println(upload("http://zhongcaiwang.muwood.com/index.php/Api/userOne/save_user_pic/", params, "params:111", new File("C:\\Users\\develop\\Pictures\\Camera Roll\\1493869599251.jpg")));
    }

    public static String upload(String url, Map<String, Object> params, String fileParamName, File file) throws Exception {
        MultipartBody.Builder body = new MultipartBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof String) {
                body.addFormDataPart(entry.getKey(), (String) entry.getValue());
            } else {
                body.addFormDataPart(entry.getKey(), value.toString());
            }
        }
        body.addFormDataPart(fileParamName, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(body.build());
        Response response = new OkHttpClient().newCall(builder.build()).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        throw new Exception("ok http upload request fail!");
    }
}
