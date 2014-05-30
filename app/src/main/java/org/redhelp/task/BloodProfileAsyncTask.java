package org.redhelp.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.SaveBloodProfileRequest;
import org.redhelp.common.SaveBloodProfileResponse;
import org.redhelp.common.types.CreateBloodProfileResponseTypes;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 5/22/14.
 */
public class BloodProfileAsyncTask extends AsyncTask<SaveBloodProfileRequest, Void, String> {

    private static final String TAG = "BloodProfileAsyncTask";

    private Context context;
    private Exception e;
    private Intent intent_next_activity;
    public BloodProfileAsyncTask(Context context, Intent nextActivity) {
        this.context = context;
        this.intent_next_activity = nextActivity;
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
        Toast toast = Toast.makeText(context,"", Toast.LENGTH_SHORT);
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
            handleResponse(response, toast);
        }
    }

    private void handleResponse(SaveBloodProfileResponse response, Toast toast) {
        if(response == null) {
            toast.setText(R.string.toast_server_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if(response.getResponse_type().equals(CreateBloodProfileResponseTypes.SUCCESSFUL)) {
            toast.setText(R.string.toast_bloodprofile_updated);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
            if(intent_next_activity != null) {
                 //context.startActivity(intent_next_activity);
            }
        } else  {
            toast.setText(R.string.toast_invalid_login);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }
    }
}
