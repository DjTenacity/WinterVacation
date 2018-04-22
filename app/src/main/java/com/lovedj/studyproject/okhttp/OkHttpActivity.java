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


        //把request传入OkHttpClient,返回一个实现了Call的RealCall对象,
        //然后这个对象调用了enqueue(new Callback)方法,在这个enqueue方法里面就会创建一个异步的实现了 Runnable的AsyncCall对象,并传入我们创建的CallBack对象
        //然后把这个asyncCall 对象传给 okhttpClient的dispatch,添加到线程池里面轮询
        //值得注意的是  添加拦截器进行网络请求都是在这个AsyncCall对象里面execute()方法进行的

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
