package com.lovedj.studyproject.okhttp;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的处理
 */
public class Dispatcher {

    private ExecutorService executorService;

    public synchronized ExecutorService executorService(){
        if(executorService == null){
            executorService =new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable runnable) {
                    Thread thread =new Thread(runnable,"okhttp");
                    thread.setDaemon(false);
                    return thread;
                }
            }
            );
        }
        return  executorService;
    }

    public void enqueue(RealCall.AsyncCall call) {
        executorService().execute(call);

    }
}
