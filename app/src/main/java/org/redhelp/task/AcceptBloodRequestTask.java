package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.AcceptBloodRequestRequest;
import org.redhelp.common.AcceptBloodRequestResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 8/30/14.
 */
public class AcceptBloodRequestTask extends AsyncTask<AcceptBloodRequestRequest, Void, String> {
    private static final String TAG = "RedHelp:AcceptBloodRequestTask";

    public interface IAcceptBloodRequestTaskListener {
        void handleAcceptBloodRequestError();
        void handleAcceptBloodRequestResponse(AcceptBloodRequestResponse response);
    }

    private Context ctx;
    private IAcceptBloodRequestTaskListener listener;

    private Exception e;
    public AcceptBloodRequestTask(Context ctx, IAcceptBloodRequestTaskListener listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(AcceptBloodRequestRequest... acceptBloodRequestRequests) {
        Gson gson = new Gson();
        String json_response = null;
        try{
            String json_request = gson.toJson(acceptBloodRequestRequests[0]);
            json_response = RestClientCall.postCall("/bloodRequest/acceptBloodRequest", json_request);
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
            AcceptBloodRequestResponse response = null;
            try {
                response = gson.fromJson(json_response, AcceptBloodRequestResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            listener.handleAcceptBloodRequestResponse(response);
        }
    }
}