package com.lovedj.studyproject.okhttp;

/**
 * Created by Administrator on 2018\3\29 0029.
 */

public interface Call {


    void enqueue(CallBack callBack);

    Response execute();

}
