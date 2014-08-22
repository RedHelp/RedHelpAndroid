package org.redhelp.task;

/**
 * Created by harshis on 6/9/14.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import org.json.JSONObject;
import org.redhelp.util.PlaceJSONParser;

import java.util.HashMap;
import java.util.List;

/** A class to parse the Google Places in JSON format */
public class PlacesJsonParserAsyncTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {

    Context ctx;
    AutoCompleteTextView atvPlaces;
    String searchStr;

    JSONObject jObject;

    public PlacesJsonParserAsyncTask(AutoCompleteTextView atvPlaces,String searchStr, Context context){
        this.searchStr = searchStr;
        this.atvPlaces = atvPlaces;
        this.ctx = context;
    }

    @Override
    protected List<HashMap<String, String>> doInBackground(String... jsonData) {

        List<HashMap<String, String>> places = null;

        PlaceJSONParser placeJsonParser = new PlaceJSONParser();

        try{
            jObject = new JSONObject(jsonData[0]);

            // Getting the parsed data as a List construct
            places = placeJsonParser.parse(jObject);

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }
        return places;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> result) {

       String[] from = new String[] { "description"};
        int[] to = new int[] { android.R.id.text1 };

        // Creating a SimpleAdapter for the AutoCompleteTextView
        SimpleAdapter adapter = new SimpleAdapter(ctx, result, android.R.layout.simple_list_item_1, from, to);

       /* PlacesAutoCompleteAdapter adapter_new = new PlacesAutoCompleteAdapter(ctx);
        for(HashMap<String,String> row: result) {

            adapter_new.add(new PlacesAutoCompleteItem(row.get("description"), row.get("reference")));
        }
       */ // Setting the adapter
        atvPlaces.setAdapter(adapter);
        adapter.getFilter().filter(searchStr, atvPlaces);
    }
}