package com.perusudroid.mypermissionpicker.permission;


import android.app.Activity;

/**
 * Created by guru on 2/27/2016.
 */
public interface PermissionProducer {
    void onReceivedPermissionStatus(int code, boolean isGrated);

    Activity getActivity();
}
