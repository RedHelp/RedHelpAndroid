package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.UpdateBloodRequest;
import org.redhelp.common.UpdateBloodRequestResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 9/23/14.
 */
public class UpdateBloodRequestTask  extends AsyncTask<UpdateBloodRequest, Void, String> {
private static final String TAG = "RedHelp:UpdateBloodRequestTask";

public interface IUpdateBloodRequestTaskListener {
    void handleUpdateBloodRequestError();
    void handleUpdateBloodRequestResponse(UpdateBloodRequestResponse response);
}

private Context ctx;
private IUpdateBloodRequestTaskListener listener;

private Exception e;
    public UpdateBloodRequestTask(Context ctx, IUpdateBloodRequestTaskListener listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(UpdateBloodRequest... updateBloodRequests) {
        Gson gson = new Gson();
        String json_response = null;
        try{
            String json_request = gson.toJson(updateBloodRequests[0]);
            json_response = RestClientCall.postCall("/bloodRequest/updateBloodRequest", json_request);
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
            UpdateBloodRequestResponse response = null;
            try {
                response = gson.fromJson(json_response, UpdateBloodRequestResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            listener.handleUpdateBloodRequestResponse(response);
        }
    }
}