package com.class100.atropos.generic;

public class AtTracker extends AtAbilityAdapter {
    // todo
    public static void track(String page, String event) {
        AtLog.d("AtTracker", page, event);
    }
}