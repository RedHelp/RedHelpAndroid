package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.GetBloodProfileAccessResponseRequest;
import org.redhelp.common.GetBloodProfileAccessResponseResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 8/27/14.
 */
public class AcceptBloodProfileRequestAsyncTask extends AsyncTask<GetBloodProfileAccessResponseRequest, Void, String> {
    private static final String TAG = "RedHelp:AcceptBloodProfileRequestAsyncTask";

    public interface IAcceptBloodProfileRequestAsyncTaskListener {
        void handleAcceptBloodProfileRequestAccessError();
        void handleAcceptBloodProfileRequestResponse();
        void handleAcceptBloodProfileRequestResponse(GetBloodProfileAccessResponseResponse response);
    }

    private Context ctx;
    private IAcceptBloodProfileRequestAsyncTaskListener listener;

    private Exception e;
    public AcceptBloodProfileRequestAsyncTask(Context ctx, IAcceptBloodProfileRequestAsyncTaskListener listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(GetBloodProfileAccessResponseRequest... getBloodProfileAccessRequests) {
        Gson gson = new Gson();
        String json_response = null;
        try{
            String json_request = gson.toJson(getBloodProfileAccessRequests[0]);
            json_response = RestClientCall.postCall("/bloodProfile/respondAccess", json_request);
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
            GetBloodProfileAccessResponseResponse response = null;
            try {
                response = gson.fromJson(json_response, GetBloodProfileAccessResponseResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            listener.handleAcceptBloodProfileRequestResponse(response);
        }
    }
}
