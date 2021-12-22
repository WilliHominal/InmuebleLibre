package com.mmw.inmueblelibre;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class ServicioFirebaseMensajes extends FirebaseMessagingService {

    private final String TAG = "APP_MSG";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        createNotificationChannel();
        Map<String,String> datos = remoteMessage.getData();
        for(Map.Entry<String,String> entrada : datos.entrySet()){
            Log.d(TAG, entrada.getKey() +" -> "+ entrada.getValue() );
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Datos del mensaje: " + remoteMessage.getData());
        }

        crearNotificacion(remoteMessage.getData().get("titulo"), remoteMessage.getData().get("texto"));

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.name_channel);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("999", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void crearNotificacion(String titulo, String texto)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "999")
                .setSmallIcon(R.drawable.icon_notificaciones)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(texto))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_logo_app))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(666, builder.build());
    }

}