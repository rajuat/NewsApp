package com.itservz.paomacha.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.itservz.paomacha.android.service.FetchAddressIntentService;

/**
 * Created by Raju on 12/28/2016.
 */

public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected static final String TAG = "BaseActivity";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected static final int premissionCodeAccessFineLocation = 2;
    protected static final int premissionCodeAccessCoarseLocation = 3;
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    public String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResultReceiver = new AddressResultReceiver(new Handler());
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        buildGoogleApiClient();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case premissionCodeAccessFineLocation: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted for FINE_LOCATION");
                } else {
                    Log.d(TAG, "Permission denied for FINE_LOCATION");
                }
                return;
            }

            case premissionCodeAccessCoarseLocation: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted for COARSE_LOCATION");
                } else {
                    Log.d(TAG, "Permission denied for COARSE_LOCATION");
                }
                return;
            }
        }
    }


    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
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
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, premissionCodeAccessFineLocation);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, premissionCodeAccessFineLocation);
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG, "Last location: " + mLastLocation);
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        Log.d(TAG, "before service");
        startService(intent);
        Log.d(TAG, "after service");
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in PostActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            Log.d(TAG, "address: " + mAddressOutput);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;

        }
    }

    abstract void displayAddressOutput();


}