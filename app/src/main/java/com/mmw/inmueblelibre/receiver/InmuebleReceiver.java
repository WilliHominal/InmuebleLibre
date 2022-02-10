package com.mmw.inmueblelibre.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mmw.inmueblelibre.R;

public class InmuebleReceiver extends BroadcastReceiver {
    public static String accionCaducidadInmuebleReceiver = "CADUCIDAD_INMUEBLE";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(accionCaducidadInmuebleReceiver)){
            PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            String id = "999";
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, id)
                    .setSmallIcon(R.drawable.icon_logo_app)
                    //TODO datos de la notificacion: caducara pronto? o ya caduco?
                    .setContentTitle("SU RESERVA CADUCARA PRONTO")
                    .setContentText("La reserva del inmueble " + intent.getStringExtra("ID_INMUEBLE") + " está por caducar.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            Notification n = mBuilder.build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(28, n);

            //TODO si el mensaje dice "la reserva ha caducado" se puede implementar aca el cambio del estado del inmueble en la base de datos
            //obtener de la base de datos el inmueble con uid = intent.getStringExtra("ID_INMUEBLE")
            //cambiar: estado -> CREADO, fecha_reserva -> "", id_cliente -> ""
        }
    }
}
