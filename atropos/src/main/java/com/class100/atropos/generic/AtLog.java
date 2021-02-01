package com.class100.atropos.generic;

import android.util.Log;

public final class AtLog extends AtAbilityAdapter {
    private static boolean enable;

    private static final String HTTP_API = "HttpApi";

    public static void enableLog(boolean enable) {
        AtLog.enable = enable;
    }

    public static void d(String module, String tag, String message) {
        if (enable) {
            Log.d(module, tag + " => " + message);
        }
    }

    public static void logTraceStack(String tag) {
        if (enable) {
            d("TraceStackLogger", tag, AtRuntime.logTraceStack(3, 16));
        }
    }

    public static void logHttpApi(String tag, String message) {
        d(HTTP_API, tag, message);
    }
}
