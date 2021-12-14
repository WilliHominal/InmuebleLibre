package com.mmw.inmueblelibre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InicioPropietarioActivity extends AppCompatActivity implements View.OnClickListener {

    private Button cerrarSesionBTN;
    private TextView nombreUsuarioTV;
    private TextView correoUsuarioTV;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_propietario);

        cerrarSesionBTN = (Button) findViewById(R.id.INICIOPROP_cerrar_sesion_BTN);
        nombreUsuarioTV = (TextView) findViewById(R.id.INICIOPROP_bienvenido_TV);
        correoUsuarioTV = (TextView) findViewById(R.id.INICIOPROP_correo_TV);

        cerrarSesionBTN.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        obtenerInfoUsuario();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.INICIOPROP_cerrar_sesion_BTN:
                firebaseAuth.signOut();
                startActivity(new Intent(InicioPropietarioActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void obtenerInfoUsuario(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre = snapshot.child("nombre").getValue().toString();
                    String correo = snapshot.child("email").getValue().toString();

                    nombreUsuarioTV.setText("Bienvenido propietario " + nombre);
                    correoUsuarioTV.setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}