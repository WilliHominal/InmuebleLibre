package com.mmw.inmueblelibre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    private EditText emailET;
    private EditText contrasenaET;
    private Button iniciarSesionBTN;

    private String email;
    private String contrasena;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.LOGIN_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        emailET = (EditText) findViewById(R.id.LOGIN_email_ET);
        contrasenaET = (EditText) findViewById(R.id.LOGIN_contrasena_ET);
        iniciarSesionBTN = (Button) findViewById(R.id.LOGIN_iniciar_sesion_BTN);

        iniciarSesionBTN.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.LOGIN_iniciar_sesion_BTN:
                email = emailET.getText().toString();
                contrasena = contrasenaET.getText().toString();

                if (email.isEmpty() || contrasena.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Completa los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                loguearUsuario();
                break;
        }
    }

    private void loguearUsuario(){
        firebaseAuth.signInWithEmailAndPassword(email, contrasena).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               cargarMenuSegunTipoUsuario();
           } else {
               Toast.makeText(getApplicationContext(), "Datos no válidos", Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void cargarMenuSegunTipoUsuario(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean esProp = snapshot.child("tipo").getValue().toString().equals("PROPIETARIO");

                    if (esProp){
                        startActivity(new Intent(LoginActivity.this, InicioPropietarioActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(LoginActivity.this, InicioClienteActivity.class));
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