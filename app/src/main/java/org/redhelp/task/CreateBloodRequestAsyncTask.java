package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.SaveBloodRequestRequest;
import org.redhelp.common.SaveBloodRequestResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 6/8/14.
 */
public class CreateBloodRequestAsyncTask extends AsyncTask<SaveBloodRequestRequest, Void, String> {

    private static final String TAG = "RedHelp:CreateBloodRequestAsyncTask";

    public interface ICreateBloodRequestAsyncTaskListener {

        void handlePreExecute();
        void handleCreateBloodRequestError();
        void handleCreateBloodRequestResponse(SaveBloodRequestResponse response);
    }

    private Context ctx;
    private ICreateBloodRequestAsyncTaskListener listener;

    private Exception e;
    public CreateBloodRequestAsyncTask(Context context, ICreateBloodRequestAsyncTaskListener listener) {
        this.ctx = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.handlePreExecute();
    }

    @Override
    protected String doInBackground(SaveBloodRequestRequest... saveBloodRequestRequest) {
        Gson gson = new Gson();
        String json_response = null;
        try{
            String json_request = gson.toJson(saveBloodRequestRequest[0]);
            json_response = RestClientCall.postCall("/bloodRequest/saveBloodRequest", json_request);
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
            SaveBloodRequestResponse response = null;
            try {
                response = gson.fromJson(json_response, SaveBloodRequestResponse.class);
            }catch(Exception e) {
                Log.e(TAG, "Exception while deserializing :"+e.toString());
            }
            handleResponse(response, toast);
        }
    }

    private void handleResponse(SaveBloodRequestResponse response, Toast toast) {
        if(response == null) {
            toast.setText(R.string.toast_server_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        toast.setText(R.string.toast_bloodrequest_created);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

        listener.handleCreateBloodRequestResponse(response);
    }
}
