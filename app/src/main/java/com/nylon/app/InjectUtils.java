package com.nylon.app;

import android.util.Log;

public class InjectUtils {
    private static final String TAG = "InjectUtils";

    public static void foobarBefore(String test) {
        Log.d(TAG, "foobarBefore " + test);
    }

    public static void foobarAfter(String test) {
        Log.d(TAG, "foobarAfter " + test);
    }

    public static void helloBefore(Utils utils, String test) {
        Log.d(TAG, "helloBefore " + test);
    }
}
