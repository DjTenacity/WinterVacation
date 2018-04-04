package com.lovedj.studyproject.okhttp.interceptor;

import com.lovedj.studyproject.okhttp.Request;
import com.lovedj.studyproject.okhttp.Response;

import java.io.IOException;



public class CacheInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // 本地有没有缓存，如果有没过期
        /*if(true){
            return new Response(new );
        }*/

        return chain.proceed(request);
    }
}
