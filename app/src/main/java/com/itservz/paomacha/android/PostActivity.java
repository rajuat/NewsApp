package com.itservz.paomacha.android;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.itservz.paomacha.android.fragment.PostFragment;
import com.itservz.paomacha.android.utils.CameraHelper;
import com.itservz.paomacha.android.utils.Permissions;
import com.itservz.paomacha.android.utils.StorageHelper;
import com.itservz.paomacha.android.view.CameraPreview;

import java.io.File;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

//https://developer.android.com/guide/topics/media/camera.html#custom-camera
public class PostActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
    private Permissions permissionsHelper;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation = new Location("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        permissionsHelper = new Permissions(this);
        if (permissionsHelper.checkPermissions()) {
            onCreatePrivate();
        } else {
            permissionsHelper.requestPermissions();
        }
    }

    private void onCreatePrivate() {
        buildGoogleApiClient();
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

            }
        });
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCamera.getParameters().getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                                public void onAutoFocus(boolean success, Camera camera) {
                                    if (success) {
                                        camera.takePicture(null, null, mPicture);
                                }
                                }
                            });
                        } else {
                            mCamera.takePicture(null, null, mPicture);
                    }
                        //now set reset taken pic
                        captureButton.setVisibility(View.GONE);
                        llDecide.setVisibility(View.VISIBLE);
                }
                }
        );
        //updateValuesFromBundle(savedInstanceState);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG, "Last location: " + mLastLocation);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
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
    public void onRequestPermissionsResult(int requestCode, final String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {
                    boolean fine = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarse = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean camera = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (coarse && fine && storage && camera) {
                        onCreatePrivate();
                    } else {
                        Toast.makeText(this, "Permission Denied, You cannot access location data, camera and galary.", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                permissionsHelper.showMessageOKCancel("You need to allow access to all the permissions.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(permissionsHelper.permissionArray, 200);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mediaFile = StorageHelper.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (mediaFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissionArray: ");
                return;
            }
            StorageHelper.writeToFile(data, mediaFile);
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mLastLocation = new Location("");
    }
}