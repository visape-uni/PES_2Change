package pes.twochange.services;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import pes.twochange.R;

/**
 * Created by Victor on 10/04/2017.
 */

public class NotificationSender {
    private static final String TAG = "NotificationSender";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public void sendNotification(final String reg_token) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String legacy_server_key = "AIzaSyAFjCBqQkhTN57QH_bF75IXY3z9vgtlM5k";
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    /*JSONObject dataJson=new JSONObject();

                    String senderUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    if (senderUserName != null) dataJson.put("body","Has recibido un mensaje de " + senderUserName);
                    else dataJson.put("body","Has recibido un mensaje nuevo.");

                    dataJson.put("title","2Change");
                    json.put("notification",dataJson);*/

                    json.put("to", "/topics/"+reg_token);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+ legacy_server_key)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch (Exception e){
                    Log.e(TAG,"Error enviando notificación: " + e);
                }
                return null;
            }
        }.execute();
    }
}
