package com.mmw.inmueblelibre;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.mmw.inmueblelibre.ui.cliente.InicioClienteActivity;
import com.mmw.inmueblelibre.ui.global.VerDetallesInmuebleActivity;
import com.mmw.inmueblelibre.ui.propietario.InicioPropietarioActivity;

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

        crearNotificacion(remoteMessage.getData().get("titulo"), remoteMessage.getData().get("texto"), remoteMessage.getData().get("tipoCliente"), remoteMessage.getData().get("idInmueble"));

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

    private void crearNotificacion(String titulo, String texto, String tipoCliente, String idInmueble)
    {

        NotificationCompat.Builder builder = null;

        if (tipoCliente.equals("CLIENTE")) {
            if (titulo.equals("RESERVA RECHAZADA")){
                Intent intent = new Intent(this, InicioClienteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntentCliente = PendingIntent.getActivity(this, 0, intent, 0);

                builder = new NotificationCompat.Builder(this, "999")
                        .setSmallIcon(R.drawable.icon_notificaciones)
                        .setContentTitle(titulo)
                        .setContentText(texto)
                        .setContentIntent(pendingIntentCliente)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(texto))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_logo_app))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                notificationManager.notify(123, builder.build());
            } else {
                Intent intent = new Intent(this, VerDetallesInmuebleActivity.class);
                intent.putExtra("id_inmueble", idInmueble);
                intent.putExtra("tipo_usuario", tipoCliente);
                intent.putExtra("estado_inmueble", "VENDIDO");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntentCliente = PendingIntent.getActivity(this, 0, intent, 0);

                builder = new NotificationCompat.Builder(this, "999")
                        .setSmallIcon(R.drawable.icon_notificaciones)
                        .setContentTitle(titulo)
                        .setContentText(texto)
                        .setContentIntent(pendingIntentCliente)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(texto))
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_logo_app))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                notificationManager.notify(456, builder.build());
            }

        } else {
            Intent intent = new Intent(this, VerDetallesInmuebleActivity.class);
            intent.putExtra("id_inmueble", idInmueble);
            intent.putExtra("tipo_usuario", tipoCliente);
            intent.putExtra("estado_inmueble", "RESERVADO");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntentPropietario = PendingIntent.getActivity(this, 0, intent, 0);

            builder = new NotificationCompat.Builder(this, "999")
                    .setSmallIcon(R.drawable.icon_notificaciones)
                    .setContentTitle(titulo)
                    .setContentText(texto)
                    .setContentIntent(pendingIntentPropietario)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(texto))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_logo_app))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(789, builder.build());
        }


    }

}