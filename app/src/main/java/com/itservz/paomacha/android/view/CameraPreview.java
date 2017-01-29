package com.itservz.paomacha.android.view;

/**
 * Created by Raju on 1/2/2017.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.location.Location;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.context = context;
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
        Camera.Parameters parameters = mCamera.getParameters();

        List<String> flash = parameters.getSupportedFlashModes();
        if (flash != null && flash.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        List<String> focus = parameters.getSupportedFocusModes();
        if (focus != null && focus.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        List<?> whiteMode = parameters.getSupportedWhiteBalance();
        if (whiteMode != null && whiteMode.contains(android.hardware.Camera.Parameters.WHITE_BALANCE_AUTO)) {
            parameters.setWhiteBalance(parameters.WHITE_BALANCE_AUTO);
        }
        if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            //parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);
            parameters.setRotation(0);
        } else {
            parameters.set("orientation", "landscape");
            mCamera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }


        int index = 0;
        int oneMegaByte = 1024000;
        int almostOneMB = Integer.MAX_VALUE;
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        for (int i = 0; i < pictureSizes.size(); i++) {
            Camera.Size s = pictureSizes.get(i);
            int size = oneMegaByte - s.height * s.width;
            Log.d(TAG, size + " " + almostOneMB);
            if (almostOneMB > size && size > 0) {
                almostOneMB = size;
                index = i;
            }
        }
        parameters.setPictureSize(pictureSizes.get(index).width, pictureSizes.get(index).height);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public static void setGpsParameters(Camera.Parameters parameters, Location loc) {
        // Clear previous GPS location from the parameters.
        parameters.removeGpsData();
        // We always encode GpsTimeStamp
        parameters.setGpsTimestamp(System.currentTimeMillis() / 1000);
        // Set GPS location.
        if (loc != null) {
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            boolean hasLatLon = (lat != 0.0d) || (lon != 0.0d);

            if (hasLatLon) {
                Log.d(TAG, "Set gps location");
                parameters.setGpsLatitude(lat);
                parameters.setGpsLongitude(lon);
                parameters.setGpsProcessingMethod(loc.getProvider().toUpperCase());
                if (loc.hasAltitude()) {
                    parameters.setGpsAltitude(loc.getAltitude());
                } else {
                    // for NETWORK_PROVIDER location provider, we may have no altitude information, but the driver needs it, so we fake one.
                    parameters.setGpsAltitude(0);
                }
                if (loc.getTime() != 0) {
                    // Location.getTime() is UTC in milliseconds. gps-timestamp is UTC in seconds.
                    long utcTimeSeconds = loc.getTime() / 1000;
                    parameters.setGpsTimestamp(utcTimeSeconds);
                }
            } else {
                loc = null;
            }
        }
    }
}