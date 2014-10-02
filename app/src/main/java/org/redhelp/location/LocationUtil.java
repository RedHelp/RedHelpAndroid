package org.redhelp.location;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by harshis on 5/22/14.
 */
public class LocationUtil {

    private static final String TAG = "LocationUtil";
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    public static boolean servicesConnected(Activity activity) {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getApplicationContext());

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(TAG, "Play service available!");

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            return false;
        }
    }

}
