package com.class100.atropos.generic;

import java.lang.reflect.Constructor;

public final class AtRuntime extends AtAbilityAdapter {
    public static void throwNPE(String message) {
        throw new NullPointerException(message);
    }

    public static String logTraceStack(int stackTopOffset, int maxStackDepth) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = stackTopOffset; i < maxStackDepth && i < elements.length; i++) {
            StackTraceElement e = elements[i];
            sb.append(e.getClassName())
                .append("#")
                .append(e.getMethodName())
                .append("\n");
        }
        return sb.toString();
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> cls = clazz.getConstructor();
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("reflect failed:" + e.getMessage());
        }
    }

    public static <T> T newInstance(Class<T> clazz, Class<?> parameter, Object arg) {
        try {
            Constructor<T> cls = clazz.getConstructor(parameter);
            return cls.newInstance(arg);
        } catch (Exception e) {
            throw new RuntimeException("reflect failed:" + e.getMessage());
        }
    }
}