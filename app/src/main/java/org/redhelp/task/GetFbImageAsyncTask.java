package org.redhelp.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

/**
 * Created by harshis on 5/23/14.
 */

//TODO cleaning....
public class GetFbImageAsyncTask extends AsyncTask<String, Void, byte[]> {
    private static final String TAG = "RedHelp:GetImageAsyncTask";

    public interface IGetImageAsyncTaskListner {
        void handleError();
        void handleFbImageResponse(byte[] image_array);
    }

    private Context ctx;
    private IGetImageAsyncTaskListner listner;

    public GetFbImageAsyncTask(Context context, IGetImageAsyncTaskListner listner) {
        this.ctx = ctx;
        this.listner = listner;
    }
    /**
     * Function loads the users facebook profile pic
     *
     * @param userID
     */
    public byte[] getUserPic(String userID) {
        String imageURL;
        Bitmap bitmap = null;
        ByteBuffer buffer = null;
        byte[] array = null;
        Log.d(TAG, "Loading Picture");
        imageURL = "https://graph.facebook.com/"+userID+"/picture?type=normal";
        Log.e(TAG, "image url:"+imageURL);
        try {
            HttpURLConnection conn = (HttpURLConnection)(new URL(imageURL)).openConnection();
            conn.setDoInput(true);
            conn.connect();
            bitmap  = BitmapFactory.decodeStream(conn.getInputStream());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte imageInByte[] = stream.toByteArray();

            int bytes = bitmap.getByteCount();
            buffer = ByteBuffer.allocate(bytes); //Create a new buffer
            bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
            array = buffer.array(); //Get the underlying array containing the data.
            Log.e(TAG, "Compressed:"+imageInByte.toString());
            Log.e(TAG, "Uncompressed:"+array.toString());
            array = imageInByte;
        } catch (Exception e) {
            Log.e("TAG", "Loading Picture FAILED, Detailed exception:"+e.toString());
            e.printStackTrace();
        }

        return array;
    }


    @Override
    protected void onPreExecute() {
    //    this.dialog.setMessage("Processing...");
        //this.dialog.show();
    }

    @Override
    protected byte[] doInBackground(String... urls) {
        if(urls[0] == null ) {
            Log.e(TAG, "Url passed is null, Can't proceed forward.");
            return null;
        }

        byte[] array = getUserPic(urls[0]);
        if(array == null){
            Log.e(TAG, "Byte array is null, Can't proceed forward.");
            return null;
        }
        return array;
    }

    @Override
    protected void onPostExecute(byte[] imageByte) {

        if(imageByte == null) {
            listner.handleError();
        }
        else {
            listner.handleFbImageResponse(imageByte);
        }
      // this.dialog.dismiss();
    }
}
