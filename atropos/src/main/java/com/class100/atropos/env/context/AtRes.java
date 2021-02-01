package com.class100.atropos.env.context;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

public final class AtRes extends AtContextAbility {
    public static String getString(@StringRes int id) {
        return env._app.getString(id);
    }

    public static String getString(@StringRes int id, Object... args) {
        return env._app.getString(id, args);
    }

    public static String[] getStringArray(@ArrayRes int id) {
        return env._app.getResources().getStringArray(id);
    }

    /**
     * Retrieve text by key depends on the default system language
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        int id = getStringId(key);
        if (id != 0) {
            return getString(id);
        }
        return null;
    }

    static int getStringId(String key) {
        return env._app.getResources().getIdentifier(key, "string", env._app.getPackageName());
    }
}
