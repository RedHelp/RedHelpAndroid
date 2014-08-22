package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.SearchRequest;
import org.redhelp.common.SearchResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 6/18/14.
 */
public class SearchAsyncTask extends AsyncTask<SearchRequest, Void, String>{
    public interface ISearchAsyncTaskCaller {
        public void handleSearchResult(SearchResponse searchResponse);
        public void handleError(Exception e);
    }

    private static final String TAG = "SearchAsyncTask";


    private Fragment fragmentPassed;
    private Exception e;
    Context ctx;

    public SearchAsyncTask(Fragment fragmentPassed) {
        this.fragmentPassed = fragmentPassed;
        this.ctx = fragmentPassed.getActivity();
    }

    @Override
    protected String doInBackground(SearchRequest... searchRequests) {
        if(searchRequests[0]== null)
            return null;
        String json_response = null;
        Gson gson = new Gson();
        try{
            String json_request = gson.toJson(searchRequests[0]);
            json_response = RestClientCall.postCall("/search/v1", json_request);
        }catch(Exception e) {
            this.e = e;
            return null;
        }
        return json_response;
    }

    @Override
    protected void onPostExecute(String json_response) {
        Toast toast = Toast.makeText(ctx, "", Toast.LENGTH_LONG);
        if(e != null) {
            Log.d(TAG, e.toString());
            toast.setText(R.string.toast_network_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            handleError(e);
        }
        else {
            Gson gson = new Gson();
            SearchResponse response = null;
            try {
                response = gson.fromJson(json_response, SearchResponse.class);
                handleResponse(response);
            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception while de-serializing :"+e.toString());
                toast.setText(R.string.toast_server_error);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();

                handleError(e);
            }
        }
    }

    private void handleResponse(SearchResponse response) {
        if(fragmentPassed != null) {
            if (fragmentPassed instanceof ISearchAsyncTaskCaller) {
                ((ISearchAsyncTaskCaller) fragmentPassed).handleSearchResult(response);
            }
        }
    }
    private void handleError(Exception e) {
        if(fragmentPassed != null) {
            if (fragmentPassed instanceof ISearchAsyncTaskCaller) {
                ((ISearchAsyncTaskCaller) fragmentPassed).handleError(e);
            }
        }
    }
}
