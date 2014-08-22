package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.redhelp.data.PlacesSearchData;
import org.redhelp.util.DownloadHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by harshis on 6/9/14.
 */
// Fetches all places from GooglePlaces AutoComplete Web Service
public class PlacesAsyncTask extends AsyncTask<PlacesSearchData, Void, String> {


    Context ctx;
    private PlacesSearchData searchData;

    public PlacesAsyncTask(Context context) {
        this.ctx = context;
    }

    PlacesJsonParserAsyncTask parserTask;

    @Override
    protected String doInBackground(PlacesSearchData... searchDataPassed) {
        if(searchDataPassed == null)
            return null;

        this.searchData = searchDataPassed[0];


        // For storing data from web service
        String data = "";

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyAXWq62V9BYWUewXg3hSEY6xxFpuFjEDJM";

        String input="";

        try {
            input = "input=" + URLEncoder.encode(searchData.searchStr, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // place type to be searched
        String types = String.format("types=%s", searchData.type);

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = input+"&"+types+"&"+sensor+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

        try{
            // Fetching the data from we service
            data = DownloadHelper.downloadUrl(url);
        }catch(Exception e){
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // Creating ParserTask
        parserTask = new PlacesJsonParserAsyncTask(searchData.atvPlaces, searchData.searchStr, ctx);

        // Starting Parsing the JSON string returned by Web Service
        parserTask.execute(result);
    }
}