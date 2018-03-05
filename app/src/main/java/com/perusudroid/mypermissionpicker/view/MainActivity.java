package com.perusudroid.mypermissionpicker.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.perusudroid.mypermissionpicker.adapter.AlertDialogListener;
import com.perusudroid.mypermissionpicker.utils.Constants;
import com.perusudroid.mypermissionpicker.utils.ImageUtils;
import com.perusudroid.mypermissionpicker.R;
import com.perusudroid.mypermissionpicker.permission.IPermissionHandler;
import com.perusudroid.mypermissionpicker.permission.PermissionProducer;
import com.perusudroid.mypermissionpicker.permission.RequestPermission;
import com.perusudroid.mypermissionpicker.utils.a;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PermissionProducer, AlertDialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private IPermissionHandler iPermissionHandler;
    private File resultCamFile, resultCroppedFile, resultGalleryFile;
    private ImageView imageView;
    private Uri galleryUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        setAssets();
    }

    private void bindViews() {
        imageView = findViewById(R.id.ivPic);
    }

    private void setAssets() {
        findViewById(R.id.btnChooser).setOnClickListener(this);
        iPermissionHandler = RequestPermission.newInstance(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChooser:

                //Custom Dialog
                //ImageUtils.showChooserDialog(this, this);

                //bottomSheet
                ImageUtils.showBottomChooser(this);
                break;
        }
    }


    @Override
    public void onClick(int which) {
        switch (which) {
            case 0:
                checkCamera();
                break;
            case 1:
                checKGallery();
                break;
        }
    }


    public void cameraClicked() {
        ImageUtils.dismissBottomChooser();
        checkCamera();
    }


    public void galleryClicked() {
        ImageUtils.dismissBottomChooser();
        checKGallery();
    }


    private void checKGallery() {
        if (ImageUtils.isAboveMarshmallow()) {
            iPermissionHandler.callStoragePermissionHandler();
        } else {
            ImageUtils.callGalleryPic(this);
        }
    }

    private void checkCamera() {
        if (ImageUtils.isAboveMarshmallow()) {
            iPermissionHandler.callCameraPermissionHandler();
        } else {
            resultCamFile = ImageUtils.onLaunchCamera(this);
        }
    }


    private void displayProfilePicture(File file) {
        Glide.with(getActivity()).load(file).into(imageView);
    }

    @Override
    public Activity getActivity() {
        return MainActivity.this;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode " + requestCode + " resultCode " + resultCode);

        int aspectX = 750;
        int aspectY = 750;

        switch (requestCode) {
            case Constants.requestCodes.CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    if (resultCamFile != null) {
                          /* WITHOUT CROPPING*/
                        // captured image will be stored in the resultCamFile.
                        Log.d(TAG, "onGetResult: file.exists() " + resultCamFile.exists());
                        if (!resultCamFile.exists()) {
                            return;
                        }

                        // doCrop(aspectX, aspectY);

                    }
                }
                break;

            case Constants.requestCodes.PICK_GALLERY_IMAGE_REQUEST_CODE:

                if (data != null) {

                    // WITHOUT CROPPING
                    galleryUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(galleryUri,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                    }
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    if (picturePath != null) {
                        File source = new File(picturePath);
                        if (!source.exists()) {
                            return;
                        }

                        resultGalleryFile = source;
                        displayProfilePicture(resultGalleryFile);
                        doUploadPic(resultGalleryFile);


                        //doCrop(aspectX, aspectY);
                    }
                }
                break;
            case Crop.REQUEST_CROP:
                if (Crop.getOutput(data) != null) {
                    String filePath = Crop.getOutput(data).getPath();

                    File file = new File(filePath);
                    if (file.exists()) {
                        resultCroppedFile = file;
                        displayProfilePicture(file);
                    }

                }
                break;
        }

    }


    @Override
    public void onReceivedPermissionStatus(int code, boolean isGrated) {
        if (IPermissionHandler.PERMISSIONS_REQUEST_CAMERA == code && isGrated) {
            resultCamFile = ImageUtils.onLaunchCamera(this);
        } else if (IPermissionHandler.PERMISSIONS_REQUEST_GALERY_AND_STORAGE == code && isGrated) {
            ImageUtils.callGalleryPic(this);
        }
    }

    private void doCrop(int aspectX, int aspectY) {

                    /* WITH CROPPING */
        Crop.of(Uri.fromFile(resultCamFile), Uri.fromFile(resultCamFile))
                .withAspect(aspectX, aspectY)
                .start(getActivity());
    }


    private void doUploadPic(final File fileToUpload) {
        try {

            displayProfilePicture(fileToUpload);

            Log.d(TAG, "doUploadHotelPic: ");

         // final File compressedFile = ImageUtils.getCompressedImageAsFile(this, fileToUpload, false);

            final String str = a.getInstance().compressImage(this, galleryUri.toString());

            final File compressedFile = new File(str);

            Log.d(TAG, "doUploadPic: file.exists "+ compressedFile.exists());

            Log.d(TAG, "doUploadPic:original file "+ ImageUtils.getFileSize(fileToUpload) + " compressedFile "+ ImageUtils.getFileSize(compressedFile));

            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                           displayProfilePicture(compressedFile);
                            Log.d(TAG, "run: compressed pic set");
                        }
                    }

            ,3000);

            displayProfilePicture(compressedFile);

/*

            RETROFIT MULTIPART

            RequestBody requestBody = RequestBody.create(MediaType.parse("** "), compressedFile);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", compressedFile.getName(), requestBody);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), compressedFile.getName());

            RXRetro.getInstance().retrofitEnque(ApiClient.getInterface().
                    hotelPicture(getSharedPref().getStringValue(Constants.sharedPref.TOKEN),
                            fileToUpload), this, 4);
*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
