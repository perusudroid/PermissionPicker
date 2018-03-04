package com.perusudroid.mypermissionpicker.permission;

/**
 * Created by perusu on 26/2/18.
 */

public interface IPermissionHandler {
    int PERMISSIONS_REQUEST_CAMERA = 40;
    int PERMISSIONS_REQUEST_RECEVIE_SMS = 41;
    int PERMISSIONS_REQUEST_CAMERA_AND_STORAGE = 42;
    int PERMISSIONS_REQUEST_GALERY_AND_STORAGE = 43;
    int PERMISSIONS_REQUEST_LOCATION = 44;
    int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1;

    void callSmsPermissionHandler();

    void callCameraPermissionHandler();

    void callCameraAndStoragePermissionHandler();

    void callStoragePermissionHandler();
    void callLocationPermissionHandler();
}
