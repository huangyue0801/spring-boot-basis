package com.service.boot.utils.network;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class Http {

    private static Map<String, String> cookieCache = new HashMap<String, String>();
    private static SSLContext sslcontext;
    private static HostnameVerifier hostnameVerifier;

    public static String get(String url, Map<String, String> params) throws IOException {
        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(urlEncode(entry.getKey())).append("=").append(urlEncode(entry.getValue())).append("&");
            }
            sb.deleteCharAt(sb.lastIndexOf("&"));
            if (url.indexOf("?") > 0) {
                url += sb.toString();
            } else {
                url = url + "?" + sb.toString();
            }
        }
        return get(url);
    }

    public static String get(String url) throws IOException {
        URL _url = new URL(url);
        String host = _url.getHost();
        HttpURLConnection conn = getHttpURLConnection(_url);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        String cookie = cookieCache.get(host);
        if (cookie != null) {
            conn.setRequestProperty("Cookie", cookie);
        }
        int responseCode = conn.getResponseCode();
        StringBuffer sb = null;
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        }
        cookie = conn.getHeaderField("Set-Cookie");
        if (cookie != null) {
            cookieCache.put(host, cookie);
        }
        conn.disconnect();
        return sb != null ? sb.toString() : null;
    }

    public static String post(String url, Map<String, String> params) throws IOException {
        URL _url = new URL(url);
        String host = _url.getHost();
        HttpURLConnection conn = getHttpURLConnection(_url);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Authorization", "b86b6565cbff4a179c79e049e74d6663");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        String cookie = cookieCache.get(host);
        if (cookie != null) {
            conn.setRequestProperty("Cookie", cookie);
        }
        conn.setDoInput(true);
        conn.setDoOutput(true);
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(urlEncode(entry.getValue())).append("&");
            }
            sb.deleteCharAt(sb.lastIndexOf("&"));
        }
        OutputStream out = conn.getOutputStream();
        out.write(sb.toString().getBytes());
        out.flush();
        out.close();
        sb.delete(0, sb.length());
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } else {
            sb = null;
        }
        cookie = conn.getHeaderField("Set-Cookie");
        if (cookie != null) {
            cookieCache.put(host, cookie);
        }
        conn.disconnect();
        return sb != null ? sb.toString() : null;
    }

    public static String upload(String url, Map<String, String> params, String fieldName, File... files) throws IOException {
        String boundary = "---------------------------";
        String endLine = "\r\n--" + boundary + "--\r\n";
        URL _url = new URL(url);
        String host = _url.getHost();
        HttpURLConnection conn = getHttpURLConnection(_url);
        conn.setConnectTimeout(3000);
        conn.setChunkedStreamingMode(0);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        String cookie = cookieCache.get(host);
        if (cookie != null) {
            conn.setRequestProperty("Cookie", cookie);
        }
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        StringBuilder textEntity = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {//构造文本类型参数的实体数据
                textEntity.append("--").append(boundary).append("\r\n");
                textEntity.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
                textEntity.append(entry.getValue());
                textEntity.append("\r\n");
            }
        }
        StringBuilder sb = new StringBuilder();
        long fileLength = 0;
        for (File file : files) {
            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(file.getName()).append("\"\r\n");
            sb.append("Content-Type: application/octet-stream\r\n\r\n");
            fileLength += file.length();
            sb.append("\r\n");
        }
        int textLength = sb.length();
        int textEntityLength = textEntity.length();
        conn.setRequestProperty("Content-Length", String.valueOf(textLength + fileLength + endLine.length() + textEntityLength));
        OutputStream out = conn.getOutputStream();
        out.write(textEntity.toString().getBytes());
        byte[] buffer = new byte[1024 * 5];
        for (File file : files) {
            sb.delete(0, sb.length());
            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(file.getName()).append("\"\r\n");
            sb.append("Content-Type: application/octet-stream\r\n\r\n");
            out.write(sb.toString().getBytes());
            FileInputStream fis = new FileInputStream(file);
            int len;
            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.write("\r\n".getBytes());
            out.flush();
            fis.close();
        }
        out.write(endLine.getBytes());
        out.flush();
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb.delete(0, sb.length());
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        }
        cookie = conn.getHeaderField("Set-Cookie");
        if (cookie != null) {
            cookieCache.put(host, cookie);
        }
        out.close();
        conn.disconnect();
        return sb.length() > 0 ? sb.toString() : null;
    }

    public static boolean download(String url, File saveFile) throws IOException {
        URL _url = new URL(url);
        String host = _url.getHost();
        HttpURLConnection conn = getHttpURLConnection(_url);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        String cookie = cookieCache.get(host);
        if (cookie != null) {
            conn.setRequestProperty("Cookie", cookie);
        }
        int responseCode = conn.getResponseCode();
        boolean result = false;
        if (responseCode == 200) {
            InputStream stream = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(saveFile);
            byte[] buffer = new byte[1024 * 5];
            int len;
            while ((len = stream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.close();
            stream.close();
            result = true;
        }
        cookie = conn.getHeaderField("Set-Cookie");
        if (cookie != null) {
            cookieCache.put(host, cookie);
        }
        conn.disconnect();
        return result;
    }

    private static String urlEncode(String params) {
        try {
            if (params == null) {
                return "";
            }
            return URLEncoder.encode(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return params;
        }
    }

    private static HttpURLConnection getHttpURLConnection(URL url) throws IOException {
        HttpURLConnection conn;
        if (url.toString().startsWith("https")) {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(getSSLContext().getSocketFactory());
            connection.setHostnameVerifier(getHostnameVerifier());
            conn = connection;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }
        return conn;
    }

    private static SSLContext getSSLContext() {
        if (sslcontext == null) {
            TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            try {
                sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
        return sslcontext;
    }

    private static HostnameVerifier getHostnameVerifier() {
        if (hostnameVerifier == null) {
            hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
        }
        return hostnameVerifier;
    }

}
