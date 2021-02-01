package com.class100.atropos;

import android.app.Application;

import com.class100.atropos.env.context.AtContextAbility;
import com.class100.atropos.env.context.AtContextEnv;
import com.class100.atropos.generic.AtLog;

public final class AtAbilityManager {
    public static void initialize(Application app, boolean debugBuild) {
        AtLog.enableLog(debugBuild);
        AtContextAbility.initialize(new AtContextEnv(app));
    }
}