package com.nylon.app;

import android.util.Log;

public class InjectUtils {
    private static final String TAG = "InjectUtils";

    public static void foobarEnter(String test) {
        Log.d(TAG, "foobarEnter " + test);
    }

    public static void foobarExit(String test) {
        Log.d(TAG, "foobarExit " + test);
    }

    public static void helloEnter(Utils utils, String test) {
        Log.d(TAG, "helloEnter " + test);
    }

    public static void helloExit(Utils utils, String test) {
        Log.d(TAG, "helloExit " + test);
    }
}
