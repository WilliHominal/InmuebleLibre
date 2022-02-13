package com.mmw.inmueblelibre.ui.global;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmw.inmueblelibre.R;
import com.mmw.inmueblelibre.receiver.InmuebleReceiver;
import com.mmw.inmueblelibre.ui.cliente.InicioClienteActivity;
import com.mmw.inmueblelibre.ui.propietario.InicioPropietarioActivity;
import com.mmw.inmueblelibre.repository.MensajesFirebaseRepository;
import com.mmw.inmueblelibre.ui.propietario.ModificarInmuebleActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VerDetallesInmuebleActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout vistaReservadoPropietarios;
    LinearLayout reservarBtnLayout;
    LinearLayout fechaVentaLayout;
    LinearLayout fechaReservaLayout;
    LinearLayout venderBtnLayout;
    Button reservarBtn;
    Button venderBtn;
    Button rechazarBtn;
    Toolbar toolbar;

    TextView descripcionTV;
    TextView nombrePropietarioTV;
    TextView fechaAltaTV;
    TextView precioTV;
    TextView direccionTV;

    TextView nombreClienteTV;
    TextView dniClienteTV;
    TextView correoClienteTV;
    TextView fechaReservaTV;

    TextView fechaVentaTV;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseFirebase;

    MensajesFirebaseRepository mensajesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ver_detalles_inmueble);
        toolbar = findViewById(R.id.DETALLES_toolbar);
        toolbar.setTitle("DETALLES");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        mensajesRepository = new MensajesFirebaseRepository();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        descripcionTV = findViewById(R.id.DETALLES_descripcion_view);
        nombrePropietarioTV = findViewById(R.id.DETALLES_propietario_view);
        fechaAltaTV = findViewById(R.id.DETALLES_fecha_alta_view);
        precioTV = findViewById(R.id.DETALLES_precio_view);
        direccionTV = findViewById(R.id.DETALLES_ubicacion_view);

        nombreClienteTV = findViewById(R.id.DETALLES_nombrecliente_view);
        dniClienteTV = findViewById(R.id.DETALLES_dnicliente_view);
        correoClienteTV = findViewById(R.id.DETALLES_correocliente_view);

        fechaReservaLayout = findViewById(R.id.DETALLES_fecha_reserva_LL);
        fechaReservaTV = findViewById(R.id.DETALLES_fecha_reserva_view);
        fechaVentaLayout = findViewById(R.id.DETALLES_fecha_venta_LL);
        fechaVentaTV = findViewById(R.id.DETALLES_fecha_venta_view);

        vistaReservadoPropietarios = findViewById(R.id.DETALLES_datoscliente_reserva);
        reservarBtnLayout = findViewById(R.id.DETALLES_reservar_btn_LL);
        reservarBtn = findViewById(R.id.DETALLES_reservar_BTN);
        venderBtnLayout = findViewById(R.id.DETALLES_vender_btn_LL);
        venderBtn = findViewById(R.id.DETALLES_vender_BTN);
        rechazarBtn = findViewById(R.id.DETALLES_rechazar_reserva_BTN);

        reservarBtn.setOnClickListener(this);
        venderBtn.setOnClickListener(this);
        rechazarBtn.setOnClickListener(this);

        setearVisibilidadSegunDatos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (getIntent().getStringExtra("tipo_usuario").equals("PROPIETARIO") && getIntent().getStringExtra("estado_inmueble").equals("CREADO"))
            getMenuInflater().inflate(R.menu.menu_toolbar_ver_detalles_propietario, menu);
        else if (getIntent().getStringExtra("tipo_usuario").equals("CLIENTE") && getIntent().getStringExtra("estado_inmueble").equals("RESERVADO"))
            getMenuInflater().inflate(R.menu.menu_toolbar_ver_detalles_cliente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                volverAtras();
                return true;
            case R.id.menu_modificar_opt:
                Intent intent = new Intent(VerDetallesInmuebleActivity.this, ModificarInmuebleActivity.class);
                intent.putExtra("id_inmueble", getIntent().getStringExtra("id_inmueble"));
                startActivity(intent);
                break;
            case R.id.menu_eliminar_opt:
                if (getIntent().getStringExtra("tipo_usuario").equals("PROPIETARIO")){
                    eliminarInmueble();
                } else {
                    cancelarReserva();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.DETALLES_reservar_BTN:
                reservarInmueble();
                break;
            case R.id.DETALLES_vender_BTN:
                venderInmueble();
                break;
            case R.id.DETALLES_rechazar_reserva_BTN:
                rechazarReserva();
                break;
        }
    }

    private void setearVisibilidadSegunDatos(){
        //SI EL USUARIO ES PROPIETARIO
        if (getIntent().getStringExtra("tipo_usuario").equals("PROPIETARIO")){
            //SI EL INMUEBLE ESTA RESERVADO
            if (getIntent().getStringExtra("estado_inmueble").equals("RESERVADO")) {
                vistaReservadoPropietarios.setVisibility(View.VISIBLE);
                fechaReservaLayout.setVisibility(View.VISIBLE);
                venderBtnLayout.setVisibility(View.VISIBLE);
                obtenerInfo(0);
            }
            //SI EL INMUEBLE ESTA VENDIDO
            else if (getIntent().getStringExtra("estado_inmueble").equals("VENDIDO")){
                vistaReservadoPropietarios.setVisibility(View.VISIBLE);
                fechaReservaLayout.setVisibility(View.VISIBLE);
                fechaVentaLayout.setVisibility(View.VISIBLE);
                obtenerInfo(1);
            }
            //SI EL INMUEBLE ESTA CREADO
            else {
                obtenerInfo(2);
            }
        }
        //SI EL USUARIO ES CLIENTE
        else {
            if (getIntent().getStringExtra("estado_inmueble").equals("RESERVADO")) {
                fechaReservaLayout.setVisibility(View.VISIBLE);
                obtenerInfo(3);
            }
            //SI EL INMUEBLE ESTA VENDIDO
            else if (getIntent().getStringExtra("estado_inmueble").equals("VENDIDO")){
                fechaReservaLayout.setVisibility(View.VISIBLE);
                fechaVentaLayout.setVisibility(View.VISIBLE);
                obtenerInfo(4);
            }
            //SI EL INMUEBLE ESTA CREADO
            else {
                reservarBtnLayout.setVisibility(View.VISIBLE);
                obtenerInfo(2);
            }
        }
    }

    private void obtenerInfo(int obtenerDatos){

        String idInmueble = getIntent().getStringExtra("id_inmueble");

        databaseFirebase.child("Inmuebles").child(idInmueble).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String descripcion = snapshot.child("descripcion").getValue().toString();
                    String fechaAlta = snapshot.child("fecha_alta").getValue().toString();
                    String fechaReserva = snapshot.child("fecha_reserva").getValue().toString();
                    String fechaVenta = snapshot.child("fecha_venta").getValue().toString();
                    String precio = snapshot.child("precio").getValue().toString();
                    String direccion = snapshot.child("direccion").getValue().toString();
                    String idPropietario = snapshot.child("id_propietario").getValue().toString();
                    String idCliente = snapshot.child("id_cliente").getValue().toString();

                    descripcionTV.setText(descripcion);
                    fechaAltaTV.setText(fechaAlta);
                    precioTV.setText(precio);
                    direccionTV.setText(direccion);

                    databaseFirebase.child("Usuarios").child(idPropietario).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String nombrePropietario = snapshot.child("nombre").getValue().toString();

                                nombrePropietarioTV.setText(nombrePropietario);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });

                    switch(obtenerDatos){
                        case 1:
                            fechaVentaTV.setText(fechaVenta);
                        case 0:
                            databaseFirebase.child("Usuarios").child(idCliente).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        String nombreCliente = snapshot.child("nombre").getValue().toString();
                                        String dniCliente = snapshot.child("dni").getValue().toString();
                                        String correoCliente = snapshot.child("email").getValue().toString();

                                        nombreClienteTV.setText(nombreCliente);
                                        dniClienteTV.setText(dniCliente);
                                        correoClienteTV.setText(correoCliente);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                            fechaReservaTV.setText(fechaReserva);
                            break;
                        case 4:
                            fechaVentaTV.setText(fechaVenta);
                        case 3:
                            fechaReservaTV.setText(fechaReserva);
                            break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void reservarInmueble() {

        String idCliente = firebaseAuth.getCurrentUser().getUid();
        String idInmueble = getIntent().getStringExtra("id_inmueble");
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String fechaReserva = ISO_8601_FORMAT.format(new Date());
        Map<String, Object> mapaValores = new HashMap<>();
        mapaValores.put("id_cliente", idCliente);
        mapaValores.put("fecha_reserva", fechaReserva);
        mapaValores.put("estado", "RESERVADO");

        databaseFirebase.child("Inmuebles").child(idInmueble).updateChildren(mapaValores).addOnCompleteListener(taskDB -> {
            if (taskDB.isSuccessful()){

                databaseFirebase.child("Inmuebles").child(idInmueble).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String idPropietario = snapshot.child("id_propietario").getValue().toString();
                            String tituloNotificacion = "NUEVA RESERVA";
                            String mensajeNotificacion = "Han solicitado una reserva del inmueble " + idInmueble + ".";

                            enviarNotificacion(idPropietario, tituloNotificacion, mensajeNotificacion, "PROPIETARIO", idInmueble);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Intent intentReceiver = new Intent(this, InmuebleReceiver.class);
                intentReceiver.setAction("CADUCIDAD_INMUEBLE");
                intentReceiver.putExtra("ID_INMUEBLE", idInmueble);

                PendingIntent intentAlarm = PendingIntent.getBroadcast(this.getApplicationContext(), 1, intentReceiver, PendingIntent.FLAG_UPDATE_CURRENT);

                final AlarmManager alarma = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                //tiempo para el aviso (60 segs para probar)
                alarma.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+60000, intentAlarm);

                Toast.makeText(getApplicationContext(), "Inmueble reservado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VerDetallesInmuebleActivity.this, InicioClienteActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudo reservar el inmueble", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void venderInmueble() {

        String idInmueble = getIntent().getStringExtra("id_inmueble");
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String fechaVenta = ISO_8601_FORMAT.format(new Date());
        Map<String, Object> mapaValores = new HashMap<>();
        mapaValores.put("fecha_venta", fechaVenta);
        mapaValores.put("estado", "VENDIDO");

        databaseFirebase.child("Inmuebles").child(idInmueble).updateChildren(mapaValores).addOnCompleteListener(taskDB -> {
            if (taskDB.isSuccessful()){

                databaseFirebase.child("Inmuebles").child(idInmueble).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String idCliente = snapshot.child("id_cliente").getValue().toString();
                            String tituloNotificacion = "COMPRA FINALIZADA";
                            String mensajeNotificacion = "El propietario " + nombrePropietarioTV.getText().toString() + " ha aceptado su reserva del inmueble " + idInmueble + ".";

                            enviarNotificacion(idCliente, tituloNotificacion, mensajeNotificacion, "CLIENTE", idInmueble);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast.makeText(getApplicationContext(), "Inmueble vendido", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VerDetallesInmuebleActivity.this, InicioPropietarioActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudo vender el inmueble", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void rechazarReserva(){
        String idInmueble = getIntent().getStringExtra("id_inmueble");

        databaseFirebase.child("Inmuebles").child(idInmueble).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String idCliente = snapshot.child("id_cliente").getValue().toString();
                    String tituloNotificacion = "RESERVA RECHAZADA";
                    String mensajeNotificacion = "El propietario " + nombrePropietarioTV.getText().toString() + " ha rechazado su reserva del inmueble " + idInmueble + ".";

                    enviarNotificacion(idCliente, tituloNotificacion, mensajeNotificacion, "CLIENTE", idInmueble);

                    Log.d("ASD", "Notificacion enviada");

                    Map<String, Object> mapaValores = new HashMap<>();
                    mapaValores.put("fecha_reserva", "");
                    mapaValores.put("estado", "CREADO");
                    mapaValores.put("id_cliente", "");

                    databaseFirebase.child("Inmuebles").child(idInmueble).updateChildren(mapaValores).addOnCompleteListener(taskDB -> {
                        if (taskDB.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Reserva rechazada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se pudo rechazar la reservay", Toast.LENGTH_SHORT).show();
                        }
                    });

                    startActivity(new Intent(VerDetallesInmuebleActivity.this, InicioPropietarioActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void enviarNotificacion(String idReceptor, String titulo, String mensaje, String tipoCliente, String idInmueble){

        databaseFirebase.child("Usuarios").child(idReceptor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String token_fcm = snapshot.child("token_fcm").getValue().toString();

                    mensajesRepository.enviarMensaje(token_fcm, titulo, mensaje, tipoCliente, idInmueble, exito -> {
                        if (exito){
                            Log.d("RESPUESTA_MENSAJE", "Mensaje enviado");
                        } else {
                            Log.d("RESPUESTA_MENSAJE", "Error al enviar mensaje");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("RESPUESTA_MENSAJE", "Error al obtener datos de Firebase Database" + error.toString());
            }
        });
    }

    private void eliminarInmueble(){
        String idInmueble = getIntent().getStringExtra("id_inmueble");

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("ELIMINAR")
                .setMessage("Quieres eliminar el inmueble?")
                .setPositiveButton("ACEPTAR", (dialog, i) -> {
                    databaseFirebase.child("Inmuebles").child(idInmueble).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Inmueble eliminado", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(VerDetallesInmuebleActivity.this, InicioPropietarioActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "No se pudo eliminar el inmueble", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void cancelarReserva(){
        String idInmueble = getIntent().getStringExtra("id_inmueble");

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("CANCELAR")
                .setMessage("Quieres cancelar la reserva?")
                .setPositiveButton("ACEPTAR", (dialog, i) -> {
                    Map<String, Object> mapaValores = new HashMap<>();
                    mapaValores.put("id_cliente", "");
                    mapaValores.put("fecha_reserva", "");
                    mapaValores.put("estado", "CREADO");

                    databaseFirebase.child("Inmuebles").child(idInmueble).updateChildren(mapaValores).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Reserva cancelada", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(VerDetallesInmuebleActivity.this, InicioClienteActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "No se pudo cancelar la reserva", Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    private void volverAtras(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String tipo = snapshot.child("tipo").getValue().toString();

                    if (tipo.equals("PROPIETARIO")){
                        startActivity(new Intent(VerDetallesInmuebleActivity.this, InicioPropietarioActivity.class));
                    } else {
                        startActivity(new Intent(VerDetallesInmuebleActivity.this, InicioClienteActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}