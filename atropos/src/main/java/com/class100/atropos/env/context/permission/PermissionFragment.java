package com.class100.atropos.env.context.permission;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.class100.atropos.generic.AtCollections;
import com.class100.atropos.generic.AtLog;

import java.util.ArrayList;
import java.util.List;

public class PermissionFragment extends Fragment {
    private static final String TAG = "PermissionFragment";

    private int requestCode;
    private String[] permissions;
    private PermissionCallback permissionCallback;

    public PermissionFragment setPermissionCallback(PermissionCallback callback) {
        permissionCallback = callback;
        return this;
    }

    public PermissionFragment setPermissions(String[] permissions) {
        this.permissions = permissions;
        return this;
    }

    public PermissionFragment setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!AtCollections.isEmpty(permissions)) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionCallback == null) {
            return;
        }
        if (this.requestCode != requestCode) {
            AtLog.d(TAG, "onRequestPermissionsResult", "requestCode not the same !");
            return;
        }
        List<String> denied = new ArrayList<>(permissions.length);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                denied.add(permissions[i]);
            }
        }
        if (AtCollections.isEmpty(denied)) {
            permissionCallback.onGrantedEntirely();
        } else {
            permissionCallback.onPermissionDenied(denied);
        }
    }
}
