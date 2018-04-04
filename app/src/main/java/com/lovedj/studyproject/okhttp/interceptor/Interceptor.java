package com.lovedj.studyproject.okhttp.interceptor;

import com.lovedj.studyproject.okhttp.Request;
import com.lovedj.studyproject.okhttp.Response;

import java.io.IOException;

/**
 *
 */

public interface  Interceptor {
    abstract Response intercept(Chain chain) throws IOException;

    interface Chain {
        Request request();

        Response proceed(Request request) throws IOException;
    }


}
