package com.mmw.inmueblelibre.UI.global;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.mmw.inmueblelibre.UI.cliente.InicioClienteActivity;
import com.mmw.inmueblelibre.UI.propietario.AgregarInmuebleActivity;
import com.mmw.inmueblelibre.UI.propietario.InicioPropietarioActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_detalles_inmueble);

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

        reservarBtn.setOnClickListener(this);
        venderBtn.setOnClickListener(this);

        setearVisibilidadSegunDatos();
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
        }
    }

    private void setearVisibilidadSegunDatos(){
        //SI EL USUARIO ES PROPIETARIO
        if (getIntent().getStringExtra("tipo_usuario").equals("PROPIETARIO")){
            //SI EL INMUEBLE ESTA RESERVADO
            if (getIntent().getStringExtra("estado_inmueble").equals("RESERVADO")) {
                Toast.makeText(getApplicationContext(), "INMUEBLE RESERVADO - PROPIETARIO", Toast.LENGTH_SHORT).show();
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

        databaseFirebase.child("Inmuebles").child(idInmueble).addValueEventListener(new ValueEventListener() {
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

                    databaseFirebase.child("Usuarios").child(idPropietario).addValueEventListener(new ValueEventListener() {
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
                            databaseFirebase.child("Usuarios").child(idCliente).addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(getApplicationContext(), "Inmueble vendido", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VerDetallesInmuebleActivity.this, InicioClienteActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudo vender el inmueble", Toast.LENGTH_SHORT).show();
            }
        });

    }

}