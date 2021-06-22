package com.class100.atropos.env.context.bluetooth;

import com.class100.atropos.env.context.AtContextAbility;

/**
 * Use BluetoothManager must declare permissions of
 * <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
 * <uses-permission android:name="android.permission.BLUETOOTH" />
 */
public class BluetoothManager extends AtContextAbility {
    private static final BluetoothManager _instance = new BluetoothManager();

    private BluetoothManager() {

    }

    public static BluetoothManager getInstance() {
        return _instance;
    }

    @Override
    public void enable() {
        super.enable();
    }
}
