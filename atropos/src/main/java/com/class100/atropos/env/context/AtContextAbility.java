package com.class100.atropos.env.context;

import com.class100.atropos.generic.AtAbilityAdapter;

public abstract class AtContextAbility extends AtAbilityAdapter {
    protected static AtContextEnv env;

    public static void initialize(AtContextEnv env) {
        AtContextAbility.env = env;
    }
}
