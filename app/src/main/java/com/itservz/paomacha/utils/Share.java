package com.itservz.paomacha.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.itservz.paomacha.android.PaoActivity;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by raju.athokpam on 04-01-2017.
 */

public class Share {

    private Activity activity = null;
    private Bitmap bitmap = null;
    private String dirPath = null;

    public Share(Activity activity){
        this.activity = activity;
    }

    public Share getScreenShot() {
        View screenView = activity.getWindow().getDecorView().getRootView();
        screenView.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return this;
    }

    public Share store(String fileName){
        dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/paoap";
        Log.d("Share", dirPath);
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void shareImage(String fileName){
        Uri uri = Uri.fromFile(new File(dirPath + "/" + fileName));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/jpeg");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject as extra text");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Text as extra text");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            activity.startActivity(Intent.createChooser(intent, "Send to"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }
}
