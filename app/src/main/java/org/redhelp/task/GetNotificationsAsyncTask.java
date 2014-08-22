package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.GetAllNotificationsResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 7/21/14.
 */
public class GetNotificationsAsyncTask extends AsyncTask<Long, Void, String>{

    public interface IGetNotificationsTaskResultListener {
        public void handleNotificationsResult(GetAllNotificationsResponse notificationsResponse);
        public void handleError(Exception e);
    }

    private static final String TAG = "GetNotificationsAsyncTask";

    private Exception e;
    private Context ctx;
    private IGetNotificationsTaskResultListener listener;


    public GetNotificationsAsyncTask(Context ctx, IGetNotificationsTaskResultListener listener) {
        this.listener = listener;
        this.ctx = ctx;
    }

    @Override
    protected String doInBackground(Long... b_p_ids) {
        if(b_p_ids[0]== null)
            return null;
        String json_response = null;
        try{
            json_response = RestClientCall.getCall("/notification/all/" + b_p_ids[0]);
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
            GetAllNotificationsResponse response = null;
            try {
                response = gson.fromJson(json_response, GetAllNotificationsResponse.class);
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

    private void handleResponse(GetAllNotificationsResponse response) {
        listener.handleNotificationsResult(response);
    }
    private void handleError(Exception e) {
        listener.handleError(e);
    }
}
