package com.nylon.app;

import android.util.Log;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    public static void foobar(String test){
        Log.d(TAG, "foobar");
    }

    public void hello(String msg){
        Log.d(TAG, "hello " + msg);
    }

    public static void hello2(String msg){
        Log.d(TAG, "hello2 " + msg);
    }



}
