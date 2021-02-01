package com.class100.atropos.env.context;

import androidx.annotation.DimenRes;

public final class AtDimens extends AtContextAbility {
    public static int get(@DimenRes int id) {
        return env._app.getResources().getDimensionPixelSize(id);
    }
}
