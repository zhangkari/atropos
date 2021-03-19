package com.class100.atropos.env.context.permission;

import java.util.List;

public interface PermissionCallback {
    void onGrantedEntirely();

    void onPermissionDenied(List<String> permissions);
}