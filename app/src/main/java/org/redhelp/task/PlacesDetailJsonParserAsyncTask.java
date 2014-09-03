package org.redhelp.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.redhelp.util.PlaceDetailsJSONParser;

import java.util.HashMap;

/**
 * Created by harshis on 6/9/14.
 */
public class PlacesDetailJsonParserAsyncTask extends AsyncTask<String, Void, HashMap<String,String>> {
    private final String LOG_TAG = "PlacesDetailJsonParserAsyncTask";
    private IPlacesResponseHandler listener;

    public interface IPlacesResponseHandler {
         void handleResponse(Double lat, Double lng);
    }


    public PlacesDetailJsonParserAsyncTask(IPlacesResponseHandler listener) {
        this.listener = listener;
    }

    JSONObject jObject;
    @Override
    protected HashMap<String, String> doInBackground(String... jsonData) {
        HashMap<String, String> hPlaceDetails = null;
        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

        try{
            jObject = new JSONObject(jsonData[0]);

            // Start parsing Google place details in JSON format
            hPlaceDetails = placeDetailsJsonParser.parse(jObject);

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }
        return hPlaceDetails;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
        Double place_lat = null;
        Double place_long = null;
        if(stringStringHashMap!=null) {
            String place_lat_string = stringStringHashMap.get("lat");
            String place_long_string = stringStringHashMap.get("lng");

            place_lat = Double.parseDouble(place_lat_string);
            place_long = Double.parseDouble(place_long_string);

        }
        listener.handleResponse(place_lat, place_long);
    }
}
