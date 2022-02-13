package com.mmw.inmueblelibre.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmw.inmueblelibre.R;

import java.util.HashMap;
import java.util.Map;

public class InmuebleReceiver extends BroadcastReceiver {
    public static String accionCaducidadInmuebleReceiver = "CADUCIDAD_INMUEBLE";

    DatabaseReference dbFirebase;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(accionCaducidadInmuebleReceiver)){
            PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            String id = "999";
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, id)
                    .setSmallIcon(R.drawable.icon_logo_app)
                    .setContentTitle("RESERVA CADUCADA")
                    .setContentText("La reserva del inmueble " + intent.getStringExtra("ID_INMUEBLE") + " ha caducado.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            Notification n = mBuilder.build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(28, n);

            //Actualizar estado DB
            dbFirebase = FirebaseDatabase.getInstance().getReference();
            Map<String, Object> mapaValores = new HashMap<>();
            mapaValores.put("estado", "CREADO");
            mapaValores.put("fecha_reserva", "");
            mapaValores.put("id_cliente", "");
            dbFirebase.child("Inmuebles").child(intent.getStringExtra("ID_INMUEBLE")).updateChildren(mapaValores);
        }
    }
}
