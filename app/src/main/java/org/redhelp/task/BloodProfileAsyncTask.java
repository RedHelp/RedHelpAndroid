package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.SaveBloodProfileRequest;
import org.redhelp.common.SaveBloodProfileResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 5/22/14.
 */
public class BloodProfileAsyncTask extends AsyncTask<SaveBloodProfileRequest, Void, String> {
    private static final String TAG = "RedHelp:BloodProfileAsyncTask";

    public interface IBloodProfileAsyncTaskListener {
        void handleCreateBloodProfileError();
        void handleCreateBloodProfileResponse(SaveBloodProfileResponse response);
    }

    private Context ctx;
    private IBloodProfileAsyncTaskListener listener;

    private Exception e;
    public BloodProfileAsyncTask(Context ctx, IBloodProfileAsyncTaskListener listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(SaveBloodProfileRequest... createBloodProfileRequests) {
        Gson gson = new Gson();
        String json_response = null;
        try{
            String json_request = gson.toJson(createBloodProfileRequests[0]);
            json_response = RestClientCall.postCall("/bloodProfile/saveProfile", json_request);
        }catch(Exception e) {
            this.e = e;
            return null;
        }
        return json_response;
    }

    @Override
    protected void onPostExecute(String json_response) {
        Toast toast = Toast.makeText(ctx,"", Toast.LENGTH_SHORT);
        if(e != null) {
            Log.d(TAG, e.toString());
            toast.setText(R.string.toast_network_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Gson gson = new Gson();
            SaveBloodProfileResponse response = null;
            try {
                response = gson.fromJson(json_response, SaveBloodProfileResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            listener.handleCreateBloodProfileResponse(response);
        }
    }
}
