package com.lovedj.studyproject.okhttp.interceptor;

import com.lovedj.studyproject.okhttp.Request;
import com.lovedj.studyproject.okhttp.RequestBody;
import com.lovedj.studyproject.okhttp.Response;

import java.io.IOException;

/**
 * Created by Administrator on 2018\3\31 0031.
 */

public class BridgeInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        //添加一些请求头
        request.header("Connection","keep-alive");

        if(request. requestBody()!=null){
            RequestBody requestBody = request.requestBody();
            request.header("Content-Type",requestBody.getContentType());
            request.header("Content-Length",Long.toString(requestBody.getContentLength()));
        }

        //做一些其他的操作
        Response response = chain .proceed(request);



        return response;
    }
}
