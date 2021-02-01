package com.class100.atropos.env.context;

import android.app.Application;

import com.class100.atropos.env.AtPluginEnv;

public class AtContextEnv extends AtPluginEnv {
    public final Application _app;

    public AtContextEnv(Application app) {
        super("AppContext");
        _app = app;
    }
}