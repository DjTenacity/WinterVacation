package com.lovedj.studyproject.okhttp.interceptor;


import com.lovedj.studyproject.okhttp.Request;
import com.lovedj.studyproject.okhttp.Response;

import java.io.IOException;
import java.util.List;

public class RealInterceptorChain implements Interceptor.Chain {
    final List<Interceptor> interceptors;
    final int index;
    final Request request;

    public RealInterceptorChain(List<Interceptor> interceptors, int index, Request request) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response proceed(Request request) throws IOException {
        RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, request);
        Interceptor interceptor = interceptors.get(index);
        Response response = interceptor.intercept(next);
        return response;
    }
}
