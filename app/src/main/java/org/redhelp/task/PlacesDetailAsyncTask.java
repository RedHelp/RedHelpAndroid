package org.redhelp.task;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.redhelp.types.PlacesDetailRequest;
import org.redhelp.util.DownloadHelper;

/**
 * Created by harshis on 6/9/14.
 * This async task is used to fetch details of place selected by user.
 *
 */
public class PlacesDetailAsyncTask extends AsyncTask<PlacesDetailRequest, Void, String> {

    private Fragment caller_fragment;
    private final String LOG_TAG = "PlacesDetailAsyncTask";

    public PlacesDetailAsyncTask(Fragment caller_fragment) {
        this.caller_fragment = caller_fragment;
    }
    @Override
    protected String doInBackground(PlacesDetailRequest... placesDetailRequests) {

        if(placesDetailRequests[0] == null) {
            return null;
        }

        String data = null;
        try {
            data = DownloadHelper.downloadUrl(placesDetailRequests[0].generateUrl());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while fetching places detail, request :"
                    + placesDetailRequests[0].toString() + "exception: " + e);
        }
        return data;
    }

    @Override
    protected void onPostExecute(String jsonData) {
        if(jsonData == null){
            //TODO handle null here
            return;
        }

        PlacesDetailJsonParserAsyncTask parserAsyncTask = new PlacesDetailJsonParserAsyncTask(caller_fragment);
        parserAsyncTask.execute(jsonData);
    }
}
