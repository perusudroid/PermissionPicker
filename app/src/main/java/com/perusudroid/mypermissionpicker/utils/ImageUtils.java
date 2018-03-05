package com.perusudroid.mypermissionpicker.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;

import com.perusudroid.mypermissionpicker.BuildConfig;
import com.perusudroid.mypermissionpicker.R;
import com.perusudroid.mypermissionpicker.adapter.AlertDialogListener;
import com.perusudroid.mypermissionpicker.adapter.ArrayAdapterWithIcon;
import com.perusudroid.mypermissionpicker.view.FragmentChooser;
import com.perusudroid.mypermissionpicker.view.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


/**
 * Created by perusu on 26/2/18.
 */

public class ImageUtils<T> {


    private static final String TAG = ImageUtils.class.getSimpleName();
    private static FragmentChooser fragmentChooser;
    private static File camFile;
    private static String camUri;
    private static Context mContext;
    private int actualHeight;
    private int actualWidth;

    private static ImageUtils imageUtils;
    private BitmapFactory.Options options;


    public static ImageUtils getInstance(Context context) {
        mContext = context;
        if (imageUtils == null) {
            imageUtils = new ImageUtils();
        }
        return imageUtils;
    }

    public String getCompressedImage(File file){

        String filePath = file.getAbsolutePath();

        return getProcessedFile(filePath);

    }


    public String getCompressedImage(String imageUri) {

        String filePath = getRealPathFromURI(mContext, imageUri);

        return getProcessedFile(filePath);

    }

    private String getProcessedFile(String filePath){

        Bitmap decodedBmp = getDecodedBitmap(filePath);

        Bitmap scaledBitmap = getScaledBitmap(decodedBmp, filePath);

        return processFile(scaledBitmap);
    }


    private Bitmap getDecodedBitmap(String filePath) {

        options = new BitmapFactory.Options();

        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        actualHeight = options.outHeight;
        actualWidth = options.outWidth;

        //max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];


        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }

        return bmp;
    }

    private Bitmap getScaledBitmap(Bitmap decodedBmp, String filePath) {

        Bitmap scaledBitmap = null;

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }


        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(decodedBmp, middleX - decodedBmp.getWidth() / 2, middleY - decodedBmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            // e.printStackTrace();
        }

        return scaledBitmap;

    }




    private String processFile(Bitmap scaledBitmap) {
        FileOutputStream out = null;

        // Use this to use cache directory
        String filename = getTempFileName();

        // Use this to write compressed images to your storage
       // String filename = getExternalFileName();
        try {
            out = new FileOutputStream(filename);

            // write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }


    public String getExternalFileName() {

        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

    }


    public static String getTempFileName() {

        File file = new File(mContext.getCacheDir(), "" + System.currentTimeMillis() + "_" + ".png");

        return file.getAbsolutePath();
    }


    public static Bitmap getCompressedImage(File originalFile, int width, int height) {

        Bitmap paramBitmap = getBitmap(originalFile.getAbsolutePath());


        if (paramBitmap != null) {

            File compressedFile = getFileFromBitmap1(paramBitmap, width, height);

            imageFileAttributes(originalFile, "originalFile");
            imageFileAttributes(compressedFile, "compressedFile");

            //return compressedFile; // if you need file
            return getBitmapFromFile(compressedFile);
        }
        return null;
    }


    private static File getFileFromBitmap1(Bitmap paramBitmap, int width, int height) {

        File localFile = new File(getTempFileName());
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        compressBitmap(paramBitmap, width, height).compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream);
        byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();

        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            localFileOutputStream.write(arrayOfByte);
            localFileOutputStream.flush();
            localFileOutputStream.close();
            return localFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static String getFileSize(final File file) {
        long len = getFileLength(file);
        return len == -1 ? "" : byte2FitMemorySize(len);
    }

    private static long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    private static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }


    private static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format("%.3fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format("%.3fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format("%.3fMB", (double) byteNum / 1048576);
        } else {
            return String.format("%.3fGB", (double) byteNum / 1073741824);
        }
    }

    public static int getBitmapSize(Bitmap data) {
        return data.getRowBytes() * data.getHeight();
    }


    public static Bitmap getBitmapFromFile(File file) {
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }


    private static Bitmap compressBitmap(Bitmap paramBitmap, int paramWidth, int paramHeight) {
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        float f = i / j;
        int m = 0;
        int k = 0;
        if (f > 0.0F) {
            m = paramWidth;
            k = paramHeight;
        }
        Bitmap localBitmap = Bitmap.createScaledBitmap(paramBitmap, m, k, true);
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        localBitmap.compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream);
        return localBitmap;
    }


    private static Bitmap getBitmap(final String imagePath) {

        return BitmapFactory.decodeFile(imagePath);
    }



    private static String genRandomImageName() {
        return gen() + "_profile.jpg";
    }


    private static int gen() {
        return 10000 + new Random(System.currentTimeMillis()).nextInt(20000);
    }


    public static boolean isAboveMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * Custom dialog
     */

    public static void showChooserDialog(Context context, final AlertDialogListener alertDialogListener) {
        String[] items;
        Integer[] itemIcons;

        items = new String[]{"Take Photo", "Choose from Library"};
        itemIcons = new Integer[]{R.drawable.ic_camera_black_24dp, R.drawable.ic_image_black_24dp};

        ListAdapter adapter = new ArrayAdapterWithIcon(context, items, itemIcons);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                alertDialogListener.onClick(which);

                Log.d(TAG, "onClick: " + which);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * @param activity - functionality exists
     */

    public static void showBottomChooser(AppCompatActivity activity) {
        fragmentChooser = new FragmentChooser();
        fragmentChooser.show(activity.getSupportFragmentManager(), fragmentChooser.getTag());
    }

    public static void dismissBottomChooser() {
        if (fragmentChooser != null) {
            fragmentChooser.dismiss();
        }
    }


    //open gallery

    public static void callGalleryPic(Context context) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((MainActivity) context).startActivityForResult(intent, Constants.requestCodes.PICK_GALLERY_IMAGE_REQUEST_CODE);
        }
    }


    //open camera

    public static File onLaunchCamera(Context context) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camUri = ImageUtils.genRandomImageName();
        camFile = ImageUtils.getPhotoFileFromUri(context, camUri);

        Uri fileProvider = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", camFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((MainActivity) context).startActivityForResult(intent, Constants.requestCodes.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }

        return camFile;

    }

    /*
      Getting selected gallery image path
     */

    public static String getRealPathFromURI(Context context, String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }


    /*
      Creating directory for the pictures captured in camera
     */

    public static File getPhotoFileFromUri(Context context, String fileName) {

        if (isExternalStorageAvailable()) {

            //make sure to use your project folder i.e., Munchbox/profile

            File mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator
                            + context.getString(R.string.directory_name)
                            + File.separator
                            + context.getString(R.string.directory_name_images)
            );

            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
            }

            return new File(mediaStorageDir.getPath() + File.separator + fileName);
        }
        return null;
    }


    // decode resource with custom idth and height

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    //print image attributes

    public static void imageFileAttributes(File file, String type) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        // If set to true, the decoder will return null (no bitmap), but the out... fields will still
        // be set, allowing the caller to query the bitmap without having to allocate the memory for its pixels.
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int imageHeight = options.outHeight; // 1024
        int imageWidth = options.outWidth; // 860
        String imageType = options.outMimeType; // .jpg .png .gif

        Log.d(TAG, "bimapAttributes:type " + type + " fileSize " + getFileSize(file) + " imageHeight " + imageHeight + " imageWidth " + imageWidth + " imageType " + imageType);

    }


    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        //getResources().getDrawable(R.drawable.demo_image);
        return ((BitmapDrawable) drawable).getBitmap();
    }



}
