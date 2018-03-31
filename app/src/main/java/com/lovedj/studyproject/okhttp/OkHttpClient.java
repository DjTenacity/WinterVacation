package com.lovedj.studyproject.okhttp;

/**
 * Created by Administrator on 2018\3\29 0029.
 */

public class OkHttpClient {

    Dispatcher dispatcher;

    public OkHttpClient(Builder builder) {
        this.dispatcher = builder.dispatcher;
    }

    public OkHttpClient() {
        this(new Builder());

    }

    public Call newCall(Request request) {


        return RealCall.newCall(request, this);
    }


    public static class Builder {
        Dispatcher dispatcher;

        //连接超时
        //https证书的一些参数
        //拦截器 等等
        public Builder() {
            dispatcher = new Dispatcher();
        }

        public OkHttpClient build() {

            return new OkHttpClient(this);
        }
    }
}
