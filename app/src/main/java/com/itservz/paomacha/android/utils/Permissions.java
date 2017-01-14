package com.itservz.paomacha.android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Raju on 1/14/2017.
 */

public class Permissions {

    Activity activity;
    Context context;
    public String[] permissionArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, WRITE_EXTERNAL_STORAGE, CAMERA};

    public Permissions(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    public boolean checkPermissions() {
        int fine = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
        int coarse = ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION);
        int storage = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int camera = ContextCompat.checkSelfPermission(context, CAMERA);

        return fine == PackageManager.PERMISSION_GRANTED
                && coarse == PackageManager.PERMISSION_GRANTED
                && storage == PackageManager.PERMISSION_GRANTED
                && camera == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(activity, permissionArray, 200);
    }

    public boolean onRequestPermissionsResult(int requestCode, final String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0) {
                    boolean fine = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean coarse = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean camera = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (coarse && fine && storage && camera) {
                        Toast.makeText(context, "Permission Granted, Now you can access location data, camera and galary.", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Toast.makeText(context, "Permission Denied, You cannot access location data, camera and galary.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (activity.shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    activity.requestPermissions(permissionArray, 200);
                                                }
                                            }
                                        });
                                return false;
                            }
                        }

                    }
                }
                break;
        }
        return false;
    }


    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
