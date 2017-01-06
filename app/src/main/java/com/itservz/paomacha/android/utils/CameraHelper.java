package com.itservz.paomacha.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by Raju on 1/5/2017.
 */

public class CameraHelper {
    static final String TAG = "CameraHelper";

    public static class CameraHandlerThread extends HandlerThread {
        Handler mHandler = null;
        private Camera mCamera;

        public CameraHandlerThread() {
            super("CameraHandlerThread");
            start();
            mHandler = new Handler(getLooper());
        }

        synchronized void notifyCameraOpened() {
            notify();
        }

        public Camera openCamera() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCamera = Camera.open();
                    } catch (Exception e) {
                        Log.d(TAG, "Camera problem" + e.getMessage());
                        e.printStackTrace();
                    }
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            } catch (InterruptedException e) {
                Log.d(TAG, "wait was interrupted");
            }
            return mCamera;
        }
    }

    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

}
