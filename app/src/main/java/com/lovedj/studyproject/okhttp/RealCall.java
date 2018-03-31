package com.lovedj.studyproject.okhttp;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Administrator on 2018\3\29 0029.
 */

public class RealCall implements Call {
    private final Request orignalRequest;
    private OkHttpClient client;

    public RealCall(Request request, OkHttpClient okHttpClient) {
        this.orignalRequest = request;
        this.client = okHttpClient;
    }

    public static Call newCall(Request request, OkHttpClient okHttpClient) {
        return new RealCall(request, okHttpClient);
    }

    @Override
    public void enqueue(CallBack callBack) {
        //异步的RealCall
        AsyncCall asyncCall = new AsyncCall(callBack);
        //交给线程池
        client.dispatcher.enqueue(asyncCall);
    }

    @Override
    public Response execute() {
        return null;
    }


    public class AsyncCall extends NamedRunnable {
        CallBack callBack;

        public AsyncCall(CallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        protected void execute() {
            //开始访问网络  requset-->response

            Log.w("TAG", "execute");

            //Volley xUtils Afinal  AsyHttpClient  --->  HttpURLConnection

            //okhttp = Socket + okio(IO)

            final Request request = orignalRequest;
            try {
                URL url = new URL(request.url);
                //HttpURLConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //Https
                if (urlConnection instanceof HttpsURLConnection) {

                    HttpsURLConnection httpURLConnection = (HttpsURLConnection) urlConnection;
                    //https的一些操作
                    //httpURLConnection.setHostnameVerifier();
                    //httpURLConnection.setSSLSocketFactory();


                }
                //urlConnection.setConnectTimeout();
                // urlConnection.setReadTimeout();

                //写东西
                //request.method.toString() 枚举会变成对象
                urlConnection.setRequestMethod(request.method.name);
                urlConnection.setDoOutput(request.method.doOutput());

                //写
                RequestBody requestBody = request.requestBody;
                if (requestBody != null) {
                    //头信息
                    urlConnection.setRequestProperty("Content-Type", requestBody.getContentType());
                    urlConnection.setRequestProperty("Content-Length", Long.toString(requestBody.getContentLength()));
                }

                urlConnection.connect();

                //写内容
                if (requestBody != null) {
                    requestBody.onWriter(urlConnection.getOutputStream());

                }

                int statusCode = urlConnection.getResponseCode();

                if (200 == statusCode) {
                    //成功  状态码200
                    InputStream inputStream = urlConnection.getInputStream();

                    Response response = new Response(inputStream);
                    callBack.onResponse(RealCall.this, response);
                }


            } catch (IOException e) {
                callBack.onFailure(RealCall.this, e);

            }


        }
    }
}
