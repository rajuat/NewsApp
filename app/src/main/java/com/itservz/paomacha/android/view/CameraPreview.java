package com.itservz.paomacha.android.view;

/**
 * Created by Raju on 1/2/2017.
 */

import android.content.Context;
import android.hardware.Camera;
import android.location.Location;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

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
        // If your preview can change or rotate, take care of those events here. Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or reformatting changes here
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

        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            Log.d(TAG, "0");
            //parameters.setPreviewSize(w, h);
            //parameters.setRotation(90);
            mCamera.setDisplayOrientation(90);
        }

        if (display.getRotation() == Surface.ROTATION_90) {
            Log.d(TAG, "90");
            parameters.setPreviewSize(w, h);
        }

        if (display.getRotation() == Surface.ROTATION_180) {
            Log.d(TAG, "180");
            parameters.setPreviewSize(h, w);
        }

        if (display.getRotation() == Surface.ROTATION_270) {
            Log.d(TAG, "270");
            //parameters.setPreviewSize(w, h);
            //parameters.setRotation(180);
            mCamera.setDisplayOrientation(180);
        }

        mCamera.setParameters(parameters);

        // start preview with new settings
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