package com.class100.atropos.generic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class AtRuntime extends AtAbilityAdapter {
    private static final String TAG = "AtRuntime";

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

    public static Method reflectMethod(Object obj, String methodName, Class<?>... parameterTypes) {
        if (obj == null) {
            throwNPE("obj can not be null !");
        }

        if (AtTexts.isEmpty(methodName)) {
            throwNPE("methodName can not be empty !");
        }

        try {
            Method method = obj.getClass().getDeclaredMethod(methodName, parameterTypes);
            if (method == null) {
                AtLog.e(TAG, "reflectMethod", "can not find method:" + methodName);
                return null;
            }
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object reflectInvoke(Object obj, Method method, Object... args) {
        if (obj == null || method == null) {
            AtLog.e(TAG, "reflectInvoke", "both object and method can not be null ! ");
            return null;
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}