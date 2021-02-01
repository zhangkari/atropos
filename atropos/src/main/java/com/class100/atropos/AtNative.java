package com.class100.atropos;

import android.content.Context;

public class AtNative {
    static {
        System.loadLibrary("atNative");
    }

    public native String getKey(Context context, String key);
}
