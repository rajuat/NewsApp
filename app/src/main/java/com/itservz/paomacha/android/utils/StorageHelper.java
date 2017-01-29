package com.itservz.paomacha.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.itservz.paomacha.android.PostActivity.MEDIA_TYPE_IMAGE;
import static com.itservz.paomacha.android.PostActivity.MEDIA_TYPE_VIDEO;

/**
 * Created by raju.athokpam on 06-01-2017.
 */

public class StorageHelper {
    static final String TAG = "StorageHelper";

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    public static void writeToFile(byte[] data, File pictureFile){
        if (data == null) return;
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pictureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    public static File getOutputMediaFile(int type) {
        File mediaFile = null;
        // To be safe, you should check that the SDCard is mounted using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "paomacha");
        // This location works best if you want the created images to be shared between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }
}
