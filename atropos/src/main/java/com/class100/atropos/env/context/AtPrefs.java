package com.class100.atropos.env.context;

public final class AtPrefs extends AtContextAbility {
    private static final String TAG = "AtPrefs";

    public static String get(String key, String def) {
        return env._app.getSharedPreferences(TAG, 0)
            .getString(key, def);
    }

    public static void put(String key, String value) {
        env._app.getSharedPreferences(TAG, 0)
            .edit()
            .putString(key, value)
            .apply();
    }

    public static int get(String key, int def) {
        return env._app.getSharedPreferences(TAG, 0)
            .getInt(key, def);
    }

    public static void put(String key, int value) {
        env._app.getSharedPreferences(TAG, 0)
            .edit()
            .putInt(key, value)
            .apply();
    }

    public static long get(String key, long def) {
        return env._app.getSharedPreferences(TAG, 0)
            .getLong(key, def);
    }

    public static void put(String key, long value) {
        env._app.getSharedPreferences(TAG, 0)
            .edit()
            .putLong(key, value)
            .apply();
    }
}
