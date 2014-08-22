package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.GetBloodProfileResponse;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 6/1/14.
 */
public class GetBloodProfileAsyncTask extends AsyncTask<Long, Void, String> {
    public enum Request_VIA {U_ID, B_P_ID};

    public interface IGetBloodProfileListener {
        void handleGetBloodProfileResponse(GetBloodProfileResponse response);
        void handleGetBloodProfileError(Exception e);
    }


    private static final String TAG = "GetBloodProfileAsyncTask";
    private IGetBloodProfileListener listener;
    private Context ctx;
    private Exception e;
    private Request_VIA request_via;

    public GetBloodProfileAsyncTask(IGetBloodProfileListener listener, Context ctx, Request_VIA request_via) {
        this.listener = listener;
        this.ctx = ctx;
        this.request_via = request_via;
    }

    @Override
    protected String doInBackground(Long... b_p_ids) {
        if(b_p_ids[0]== null)
            return null;
        String json_response = null;
        try{
            String base_path = "/bloodProfile/";
            if(request_via.equals(Request_VIA.U_ID)) {
                base_path = base_path + "getViaUid/";
            }
            json_response = RestClientCall.getCall(base_path + b_p_ids[0]);
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
        }
        else {
            Gson gson = new Gson();
            GetBloodProfileResponse response = null;
            try {
                response = gson.fromJson(json_response, GetBloodProfileResponse.class);
                listener.handleGetBloodProfileResponse(response);
            } catch(Exception e) {
                Log.e(TAG, "Exception while de-serializing :"+e.toString());
                toast.setText(R.string.toast_server_error);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


}
