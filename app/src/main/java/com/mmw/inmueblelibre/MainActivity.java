package com.mmw.inmueblelibre;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button botonPruebaIniciarSesion;
    TextView crearCuentaTV;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonPruebaIniciarSesion = findViewById(R.id.prueba_loguearse_BTN);
        crearCuentaTV = findViewById(R.id.MAIN_crear_cuenta_TV);

        botonPruebaIniciarSesion.setOnClickListener(this);
        crearCuentaTV.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prueba_loguearse_BTN:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.MAIN_crear_cuenta_TV:
                startActivity(new Intent(MainActivity.this, RegistrarActivity.class));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null){
            cargarMenuSegunTipoUsuario();
        }
    }

    private void cargarMenuSegunTipoUsuario(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean esProp = snapshot.child("tipo").getValue().toString().equals("PROPIETARIO");

                    if (esProp){
                        startActivity(new Intent(MainActivity.this, InicioPropietarioActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(MainActivity.this, InicioClienteActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}