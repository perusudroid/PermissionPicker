package com.perusudroid.mypermissionpicker.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


/**
 * Created by perusu on 26/2/18.
 */

public class ImageUtils {


    private static final String TAG = ImageUtils.class.getSimpleName();
    private static FragmentChooser fragmentChooser;
    private static File camFile;
    private static String camUri;


    private static String genRandomImageName() {
        return gen() + "_profile.jpg";
    }


    public static String getCamUri() {
        return camUri;
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

    public static void imageFileAttributes(Context mContext, File file, String type) {

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


    public static File getCompressedImageAsFile(Context paramContext, File originalFile, boolean reduceQuality) {

        Bitmap paramBitmap = getBitmap(originalFile.getAbsolutePath());

        imageFileAttributes(paramContext, originalFile, "originalFile");

        if (paramBitmap != null) {

            if (reduceQuality) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                paramBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            }

            File compressedFile = getFileFromBitmap1(paramContext, paramBitmap);

            imageFileAttributes(paramContext, compressedFile, "compressedFile");

            return compressedFile;
        }
        return null;
    }


    public static Bitmap getCompressedImageAsBitmap(Context paramContext, File originalFile, boolean reduceQuality) {

        Bitmap paramBitmap = getBitmap(originalFile.getAbsolutePath());

        Log.d(TAG, "getCompressedImageAsFile: original file size " + getFileSize(originalFile));

        if (paramBitmap != null) {

            if (reduceQuality) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                paramBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            }

            File compressedFile = getFileFromBitmap1(paramContext, paramBitmap);

            Log.d(TAG, "getCompressedImageAsFile: original file size " + getFileSize(compressedFile));

            return getBitmapFromFile(compressedFile);
        }
        return null;
    }


    public static File getCompressedImageAsFile(Context paramContext, String path, boolean reduceQuality) {

        Bitmap paramBitmap = getBitmap(path);


        if (paramBitmap != null) {

            if (reduceQuality) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                paramBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            }

            return getFileFromBitmap1(paramContext, paramBitmap);
        }
        return null;
    }


    public static Bitmap getCompressedImageAsBitmap(Context paramContext, String path, boolean reduceQuality) {
        Bitmap paramBitmap = getBitmap(path);
        if (paramBitmap != null) {

            if (reduceQuality) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                paramBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            }

            File file = getFileFromBitmap1(paramContext, paramBitmap);
            return getBitmapFromFile(file);
        }
        return null;
    }

    private static File getFileFromBitmap1(Context paramContext, Bitmap paramBitmap) {

        File localFile = new File(paramContext.getCacheDir(), "" + System.currentTimeMillis() + "_" + "_munch.png");
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        compressBitmap(paramBitmap, 700).compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream);
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


    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
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


    private static Bitmap compressBitmap(Bitmap paramBitmap, int paramInt) {
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        float f = i / j;
        int m = 0;
        int k = 0;
        if (f > 0.0F) {
            m = paramInt;
            k = (int) (m / f);
        }
        Bitmap localBitmap = Bitmap.createScaledBitmap(paramBitmap, m, k, true);
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        localBitmap.compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream);
        return localBitmap;
    }


    private static Bitmap getBitmap(final String imagePath) {

        return BitmapFactory.decodeFile(imagePath);
    }


/*
    public ByteArrayBody getCompressedImage(String path) {

        Bitmap imageBitmap = getBitmap(path);

        if (imageBitmap != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ByteArrayBody bab;
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            bab = new ByteArrayBody(data, "" + System.currentTimeMillis() + "displayPicture.jpg");
            return bab;
        }
        return null;
    }


    public ByteArrayBody getCompressedImage(File file) {
        Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (imageBitmap != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ByteArrayBody bab;
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            bab = new ByteArrayBody(data, "" + System.currentTimeMillis() + " displayPicture.jpg");
            return bab;
        }
        return null;
    }
*/


}
