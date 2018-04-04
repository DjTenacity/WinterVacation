package com.lovedj.studyproject.okhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lovedj.studyproject.R;

import java.io.IOException;

public class OkHttpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);


        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new RequestBody()
                .type("")
                .addParam("", "");

        Request request = new Request.Builder().url("https://www.baidu.com")
                .post(requestBody).build();

        Call call = client.newCall(request);

        call.enqueue(new CallBack() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", response.toString());


            }
        });
    }

    //涉及的类比较多 ,写出来的代码思想很重要
    //体现的调用形式打起来,把细节填好

/**
 *  OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(new Cache()).build();

 Request request = new Request.Builder().cacheControl(new CacheControl.Builder().noCache())
 *
 * */
}
