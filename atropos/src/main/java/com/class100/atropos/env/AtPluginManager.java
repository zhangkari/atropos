package com.class100.atropos.env;

import android.util.ArrayMap;

import java.util.Map;

public abstract class AtPluginManager<T extends AtPluginEnv> extends AtPlugin<T> {
    protected final Map<String, T> context;

    public AtPluginManager(T t) {
        super(t);
        context = new ArrayMap<>(32);
    }
}
