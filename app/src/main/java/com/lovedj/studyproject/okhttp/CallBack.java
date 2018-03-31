package com.lovedj.studyproject.okhttp;

import java.io.IOException;

/**
 * Created by Administrator on 2018\3\29 0029.
 */

public interface CallBack {

    void onFailure(Call call, IOException e);


    void onResponse(Call call, Response response)throws IOException;
}
