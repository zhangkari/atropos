package com.class100.atropos.env.context.permission;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.class100.atropos.env.context.AtContextAbility;
import com.class100.atropos.generic.AtCollections;

import java.util.ArrayList;
import java.util.List;

public class AtPermission extends AtContextAbility {
    private static final String TAG = "AtPermission";
    private static final int REQUEST_CODE = 100;

    public static void requestAppPermission(@NonNull final AppCompatActivity activity, @NonNull final PermissionCallback callback) {
        String[] permissions = retrieveAppManifestPermission(activity);
        if (AtCollections.isEmpty(permissions)) {
            callback.onGrantedEntirely();
            return;
        }
        checkPermission(activity, new PermissionCallback() {
            @Override
            public void onGrantedEntirely() {

            }

            @Override
            public void onPermissionDenied(List<String> permissions) {
                String[] p = new String[permissions.size()];
                permissions.toArray(p);
                requestPermission(activity, p, callback);
            }
        }, permissions);
    }

    public static void checkPermission(@NonNull AppCompatActivity activity, @NonNull PermissionCallback callback, String... permissions) {
        if (AtCollections.isEmpty(permissions)) {
            callback.onGrantedEntirely();
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback.onGrantedEntirely();
            return;
        }

        List<String> denied = new ArrayList<>(permissions.length);
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                denied.add(p);
            }
        }
        if (AtCollections.isEmpty(denied)) {
            callback.onGrantedEntirely();
        } else {
            callback.onPermissionDenied(denied);
        }
    }

    public static void requestPermission(@NonNull AppCompatActivity activity, String[] permissions, @NonNull PermissionCallback callback) {
        if (AtCollections.isEmpty(permissions)) {
            return;
        }
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, retrieveFragment(activity, permissions, callback))
            .commitAllowingStateLoss();
    }

    public static String[] retrieveAppManifestPermission(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            return packageInfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private static PermissionFragment retrieveFragment(AppCompatActivity activity, String[] permissions, PermissionCallback callback) {
        Fragment fragment = activity.getSupportFragmentManager()
            .findFragmentByTag(TAG);
        if (fragment instanceof PermissionFragment) {
            PermissionFragment frag = (PermissionFragment) fragment;
            return frag.setPermissionCallback(callback)
                .setPermissions(permissions)
                .setRequestCode(REQUEST_CODE);
        }
        return new PermissionFragment()
            .setPermissionCallback(callback)
            .setPermissions(permissions)
            .setRequestCode(REQUEST_CODE);
    }
}
