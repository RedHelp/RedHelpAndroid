package org.redhelp.task;

/**
 * Created by harshis on 6/15/14.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.GetBloodRequestResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 6/1/14.
 */
public class GetBloodRequestAsyncTask extends AsyncTask<Long, Void, String> {

    private static final String TAG = "RedHelp:GetBloodRequestAsyncTask";

    public interface IBloodRequestListener {
        public void handleGetBloodRequestError(Exception e);
        public void handleGetBloodRequestResponse(GetBloodRequestResponse response);
    }


    private Context ctx;
    private IBloodRequestListener listener;
    private Exception e;

    public GetBloodRequestAsyncTask(Context ctx, IBloodRequestListener listener) {
        this.listener = listener;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(Long... data) {
        if(data[0]== null || data[1] == null)
            return null;
        String json_response = null;
        Long b_r_id = data[0];
        Long b_p_id = data[1];
        try{
            json_response = RestClientCall.getCall("/bloodRequest/" + b_r_id + "/" + b_p_id);
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
            listener.handleGetBloodRequestError(e);
        }
        else {
            Gson gson = new Gson();
            GetBloodRequestResponse response = null;
            try {
                response = gson.fromJson(json_response, GetBloodRequestResponse.class);
                listener.handleGetBloodRequestResponse(response);
            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception while de-serializing :"+e.toString());
                toast.setText(R.string.toast_server_error);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
                listener.handleGetBloodRequestError(e);
            }
        }
    }
}
