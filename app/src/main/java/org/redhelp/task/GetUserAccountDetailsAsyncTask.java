package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.GetUserAccountResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 7/30/14.
 */
public class GetUserAccountDetailsAsyncTask extends AsyncTask<Long, Void, String> {
    private final String TAG = "RedHelp:GetUserAccountDetailsAsyncTask";

    private IGetUserAccountDetailsListener listner;
    private Context ctx;
    private Exception e;

    public GetUserAccountDetailsAsyncTask(IGetUserAccountDetailsListener listner, Context ctx) {
        this.listner = listner;
        this.ctx = ctx;
    }

    public interface IGetUserAccountDetailsListener {
        public void handleUserAccountResult(GetUserAccountResponse searchResponse);
        public void handleUserAccountError(Exception e);
    }

    @Override
    protected String doInBackground(Long... u_ids) {
        if(u_ids[0]== null)
            return null;
        String json_response = null;
        try{

            //TODO change path
            json_response = RestClientCall.getCall("/userAccount/" + u_ids[0]);
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

            listner.handleUserAccountError(e);
        }
        else {
            Gson gson = new Gson();
            GetUserAccountResponse response = null;
            try {
                response = gson.fromJson(json_response, GetUserAccountResponse.class);
                listner.handleUserAccountResult(response);
            } catch(Exception e) {

                e.printStackTrace();

                Log.e(TAG, "Exception while de-serializing :"+e.toString());
                toast.setText(R.string.toast_server_error);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();

                listner.handleUserAccountError(e);
            }
        }
    }

}
