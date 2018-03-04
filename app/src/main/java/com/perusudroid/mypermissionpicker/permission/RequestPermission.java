package com.perusudroid.mypermissionpicker.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/**
 * Created by anand_android on 10/1/2016.
 */
public class RequestPermission implements IPermissionHandler {

    private String TAG = "RequestPermission";
    private PermissionProducer mPermissionProducer;

    public static IPermissionHandler newInstance(PermissionProducer permissionProducer) {
        RequestPermission requestPermission = new RequestPermission();
        requestPermission.mPermissionProducer = permissionProducer;
        return requestPermission;
    }

    @Override
    public void callSmsPermissionHandler() {
        if (ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                        Manifest.permission.READ_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"permission not granted!");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mPermissionProducer.getActivity(),
                    Manifest.permission.RECEIVE_SMS)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            //    mPermissionProducer.showMessage("Explanation will comes here!");
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS
                        },
                        PERMISSIONS_REQUEST_RECEVIE_SMS);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS
                        },
                        PERMISSIONS_REQUEST_RECEVIE_SMS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mPermissionProducer.onReceivedPermissionStatus(PERMISSIONS_REQUEST_RECEVIE_SMS, true);
        }
    }

    @Override
    public void callCameraPermissionHandler() {
        if (ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mPermissionProducer.getActivity(),
                    Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
              //  mPermissionProducer.showMessage("Explanation will comes here!");
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_CAMERA);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mPermissionProducer.onReceivedPermissionStatus(PERMISSIONS_REQUEST_CAMERA, true);
        }
    }

    @Override
    public void callCameraAndStoragePermissionHandler() {
        if (ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                        Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                        Manifest.permission.MODIFY_AUDIO_SETTINGS)!= PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mPermissionProducer.getActivity(),
                    Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //mPermissionProducer.showMessage("Explanation will comes here!");
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        PERMISSIONS_REQUEST_CAMERA);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
                        PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mPermissionProducer.onReceivedPermissionStatus(PERMISSIONS_REQUEST_CAMERA_AND_STORAGE, true);
        }
    }
    @Override
    public void callStoragePermissionHandler() {

        if (ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mPermissionProducer.getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
               // mPermissionProducer.showMessage("Explanation will comes here!");
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_GALERY_AND_STORAGE);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_GALERY_AND_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mPermissionProducer.onReceivedPermissionStatus(PERMISSIONS_REQUEST_GALERY_AND_STORAGE, true);
        }
    }
    @Override
    public void callLocationPermissionHandler( ) {
        if (ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mPermissionProducer.getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mPermissionProducer.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mPermissionProducer.getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mPermissionProducer.onReceivedPermissionStatus(PERMISSIONS_REQUEST_LOCATION, true);
        }
    }

}
