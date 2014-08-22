package org.redhelp.task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.redhelp.app.R;
import org.redhelp.common.GetEventResponse;
import org.redhelp.fagment.ViewEventFragment;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 7/4/14.
 */
public class GetEventAsyncTask extends AsyncTask<Long, Void, String> {

    private static final String TAG = "GetEventAsyncTask";
    private ViewEventFragment viewEventFragment;
    private Exception e;

    public interface IGetEventAsyncTaskListner {
        public void handleResponse(GetEventResponse response);
    }

    public GetEventAsyncTask(ViewEventFragment viewEventFragment) {
        this.viewEventFragment = viewEventFragment;
    }

    @Override
    protected String doInBackground(Long... e_ids) {
        if(e_ids[0]== null)
            return null;
        String json_response = null;
        try{
            json_response = RestClientCall.getCall("/event/" + e_ids[0]);
        }catch(Exception e) {
            this.e = e;
            return null;
        }
        return json_response;
    }

    @Override
    protected void onPostExecute(String json_response) {
        if(viewEventFragment == null)
            return;
        Toast toast = Toast.makeText(viewEventFragment.getActivity(), "", Toast.LENGTH_LONG);
        if(e != null) {
            Log.d(TAG, e.toString());
            toast.setText(R.string.toast_network_error);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Gson gson = new Gson();
            GetEventResponse response = null;
          /*  try {*/
                response = gson.fromJson(json_response, GetEventResponse.class);
                handleResponse(response);
           /* } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception while de-serializing :"+e.toString());
                toast.setText(R.string.toast_server_error);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();
            }*/
        }
    }

    private void handleResponse(GetEventResponse response) {
        if(viewEventFragment instanceof IGetEventAsyncTaskListner) {
            ((IGetEventAsyncTaskListner)viewEventFragment).handleResponse(response);
        }
    }


}
