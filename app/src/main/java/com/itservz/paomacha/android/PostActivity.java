package com.itservz.paomacha.android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itservz.paomacha.android.fragment.PostFragment;
import com.itservz.paomacha.android.utils.CameraHelper;
import com.itservz.paomacha.android.utils.StorageHelper;
import com.itservz.paomacha.android.view.CameraPreview;

import java.io.File;

//https://developer.android.com/guide/topics/media/camera.html#custom-camera
public class PostActivity extends BaseActivity {
    static final String TAG = "PostActivity";
    private static Camera mCamera;
    private static File mediaFile;
    private CameraPreview mPreview;
    private CameraHelper.CameraHandlerThread mThread = null;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Button captureButton;
    private LinearLayout llDecide;
    private Button noButton;
    private Button yesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        getCameraInstance();
        captureButton = (Button) findViewById(R.id.button_capture);
        llDecide = (LinearLayout) findViewById(R.id.ll_decide);
        noButton = (Button) findViewById(R.id.no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureButton.setVisibility(View.VISIBLE);
                llDecide.setVisibility(View.GONE);
                mCamera.startPreview();
            }
        });
        yesButton = (Button) findViewById(R.id.yes);
        getIntent().getStringArrayListExtra(PaoActivity.CATEGORY_TAG);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("file", mediaFile.getPath());
                bundle.putString("mAddressOutput", mAddressOutput);
                bundle.putParcelable("mLastLocation", mLastLocation);
                bundle.putStringArrayList(PaoActivity.CATEGORY_TAG, getIntent().getStringArrayListExtra(PaoActivity.CATEGORY_TAG));
                PostFragment postFragment = new PostFragment();
                postFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.post_root, postFragment);
                findViewById(R.id.camera_frame).setVisibility(View.GONE);
                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }
                fragmentTransaction.commit();
                if (mGoogleApiClient.isConnected() && mLastLocation != null) {
                    Log.d(TAG, "mGoogleApiClient");
                    startIntentService();
                    mAddressRequested = true;
                }
            }
        });
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            public void onAutoFocus(boolean success, Camera camera) {
                                Log.d("TAG", "before success");
                                if (success) {
                                    Log.d("TAG", "after success");
                                    camera.takePicture(null, null, mPicture);
                                }
                            }
                        });
                        //mCamera.takePicture(null, null, mPicture);
                        //now set reset taken pic
                        captureButton.setVisibility(View.GONE);
                        llDecide.setVisibility(View.VISIBLE);
                    }
                }
        );
        updateValuesFromBundle(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.camera_frame).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void getCameraInstance() {
        if (!CameraHelper.checkCameraHardware(this)) {
            Toast.makeText(this, "Camera is not present", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's response! After the user sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant. The callback method gets the result of the request.
            }
        } else {
            getCamera();
        }
    }



    private void getCamera() {
        if (mThread == null) {
            mThread = new CameraHelper.CameraHandlerThread();
        }
        synchronized (mThread) {
            mCamera = mThread.openCamera();
            mPreview = new CameraPreview(this, mCamera);
            LinearLayout preview = (LinearLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCamera();
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mediaFile = StorageHelper.getOutputMediaFile(MEDIA_TYPE_IMAGE);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

    @Override
    void displayAddressOutput() {
        Log.d(TAG, "displayAddressOutput: " + mAddressOutput);
    }

    //media
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        //private File pictureFile = null;
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's response! After the user sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant. The callback method gets the result of the request.
                }
            } else {
                mediaFile = StorageHelper.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            }
            if (mediaFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            StorageHelper.writeToFile(data, mediaFile);
        }
    };
}