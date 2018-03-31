package com.lovedj.studyproject.okhttp;

/**
 * Created by Administrator on 2018\3\30 0030.
 */

public abstract class NamedRunnable implements Runnable {
    @Override
    public void run() {
        execute();
    }

    protected abstract void execute();
}
