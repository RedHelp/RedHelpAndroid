package org.redhelp.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.redhelp.common.MarkNotificaionAsReadRequest;
import org.redhelp.network.RestClientCall;

/**
 * Created by harshis on 8/10/14.
 */

//Currently I am not handling response of this task.
// IDeally i should retry if something wrong happens
public class MarkNotificationAsReadTask extends AsyncTask<MarkNotificaionAsReadRequest, Void, String> {
    private static final String TAG = "RedHelp:EditUserAccountRequest";
    private Context ctx;

    private Exception e;
    public MarkNotificationAsReadTask(Context context) {
        this.ctx = context;
    }

    @Override
    protected String doInBackground(MarkNotificaionAsReadRequest... markNotificaionAsReadRequests) {
        Gson gson = new Gson();
        String json_response = null;
        try{
            String json_request = gson.toJson(markNotificaionAsReadRequests[0]);
            json_response = RestClientCall.postCall("/notification/markAsRead", json_request);
        }catch(Exception e) {
            this.e = e;
            return null;
        }
        return json_response;
    }
}
