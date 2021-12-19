package com.mmw.inmueblelibre;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class ServicioFirebaseMensajes extends FirebaseMessagingService {

    private final String TAG = "APP_MSG";
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /*Log.d(TAG, "From: " + remoteMessage.getFrom());

        Log.d(TAG, "getCollapseKey(: " + remoteMessage.getCollapseKey());
        Log.d(TAG, "getMessageId: " + remoteMessage.getMessageId());
        Log.d(TAG, "getMessageType: " + remoteMessage.getMessageType());
        Log.d(TAG, "getTo: " + remoteMessage.getTo());
        Log.d(TAG, "getOriginalPriority " + remoteMessage.getOriginalPriority());
        Log.d(TAG, "getPriority: " + remoteMessage.getPriority());
        Log.d(TAG, "getSentTime: " + remoteMessage.getSentTime());
        Log.d(TAG, "getTtl: " + remoteMessage.getTtl());*/
        Map<String,String> datos = remoteMessage.getData();
        for(Map.Entry<String,String> entrada : datos.entrySet()){
            Log.d(TAG, entrada.getKey() +" -> "+ entrada.getValue() );
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Datos del mensaje: " + remoteMessage.getData());
        }

        ShowToastInIntentService(remoteMessage.getData().toString());

    }

    public void ShowToastInIntentService(final String sText) {
        final Context MyContext = this;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast1 = Toast.makeText(MyContext, sText, Toast.LENGTH_LONG);
                toast1.show();
            }
        });
    };


    private void sendRegistrationToServer(String token){
        Log.d(TAG, "ENVIAR token: " + token);
    }
}