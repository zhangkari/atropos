package com.class100.atropos.env;

public abstract class AtPluginEnv {
    public String key;
    public boolean loaded;
    public boolean enabled;

    public AtPluginEnv(String key) {
        this.key = key;
        loaded = true;
        enabled = true;
    }
}