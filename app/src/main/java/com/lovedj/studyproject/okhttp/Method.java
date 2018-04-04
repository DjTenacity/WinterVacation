package com.lovedj.studyproject.okhttp;

/**
 * Created by Administrator on 2018\3\30 0030.
 */

public enum Method {
    POST("POST"), GET("GET"), HEAD("HEAD"), DELETE("DELETE"), PUT("PUT"), PATCH("PATCH");

    public   String name;


    Method(String name) {
        this.name = name;
    }

    public boolean doOutput() {
        switch (this) {
            case POST:
            case PUT:
                return true;
        }
        return false;
    }
}
