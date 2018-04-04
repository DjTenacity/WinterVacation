package com.lovedj.studyproject.okhttp;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * 客户端的请求
 */

public class Request {

    final String url;
    public final Method method;
    final Map<String, String> headers;
    final RequestBody requestBody;

    public Request(Builder builder) {

        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.requestBody = builder.requestBody;
    }

    public void header(String key, String value) {
        headers.put(key,value);
    }

    public RequestBody requestBody() {
        return requestBody;
    }
    public String url() {
        return url;
    }

    //okhttp 里面是HttpUrl,在里面会去判断http和https,,里面会解析 username,password, 端口(80,443)和host
    //http-->80  https-->443   volley,xutils就不再需要自己去解析端口,因为okhttp本身是基于socket 和okio的封装
    // volley,xutils是基于HttpURLConnection,由系统去解析,所以不需要去解析端口


    public static class Builder {
        String url;
        Method method;
        Map<String, String> headers;
        public RequestBody requestBody;

        //String url  ;  Body  post参数   ;请求头
        public Builder() {

            method = Method.GET;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Request build() {

            return new Request(this);
        }

        public Builder get() {
            method = Method.GET;
            return this;
        }

        public Builder post(RequestBody requestBody) {
            method = Method.POST;
            this.requestBody = requestBody;
            return this;
        }

        //实际中不要用map,就是添加了两个头就会被覆盖,
        public Builder header(String key, String vaule) {
            headers.put(key, vaule);
            return this;
        }
    }

}
