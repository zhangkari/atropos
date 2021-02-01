package com.class100.atropos.env;

import com.class100.atropos.AtAbility;

public abstract class AtPlugin<T extends AtPluginEnv> implements AtAbility {
    protected final T context;

    public AtPlugin(T t) {
        context = t;
    }

    @Override
    public void load() {
        context.loaded = true;
    }

    @Override
    public void enable() {
        context.enabled = true;
    }

    @Override
    public void disable() {
        context.enabled = false;
    }

    @Override
    public void unload() {
        context.loaded = false;
    }

    public boolean isEnabled() {
        return context.enabled;
    }

    public boolean isLoaded() {
        return context.loaded;
    }

    public String getId() {
        return context.key;
    }
}
