package com.perusudroid.mypermissionpicker.permission;


import android.app.Activity;

/**
 * Created by perusu on 26/2/18.
 */
public interface PermissionProducer {
    void onReceivedPermissionStatus(int code, boolean isGrated);

    Activity getActivity();
}
