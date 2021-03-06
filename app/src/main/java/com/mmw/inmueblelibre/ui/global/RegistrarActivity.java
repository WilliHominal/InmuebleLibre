package com.mmw.inmueblelibre.ui.global;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mmw.inmueblelibre.R;

import java.util.HashMap;
import java.util.Map;

public class RegistrarActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    private EditText nombreET;
    private EditText emailET;
    private EditText contrasenaET;
    private EditText dniET;
    private Button registrarseBTN;
    private RadioGroup tipoUsuarioRG;

    private String nombre;
    private String email;
    private String contrasena;
    private String dni;
    private String tipo;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseFirebase;
    FirebaseMessaging firebaseMessaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        toolbar = findViewById(R.id.REGISTRAR_toolbar);
        toolbar.setTitle("REGISTRO DE USUARIO");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        nombreET = (EditText) findViewById(R.id.REGISTRAR_nombre_ET);
        emailET = (EditText) findViewById(R.id.REGISTRAR_email_ET);
        contrasenaET = (EditText) findViewById(R.id.REGISTRAR_contrasena_ET);
        dniET = (EditText) findViewById(R.id.REGISTRAR_dni_ET);
        registrarseBTN = (Button) findViewById(R.id.REGISTRAR_registrarse_BTN);
        tipoUsuarioRG = (RadioGroup) findViewById(R.id.REGISTRAR_tipo_usuario_RG);

        registrarseBTN.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();
        firebaseMessaging = FirebaseMessaging.getInstance();
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
        switch (view.getId()){
            case R.id.REGISTRAR_registrarse_BTN:
                nombre = nombreET.getText().toString();
                email = emailET.getText().toString();
                contrasena = contrasenaET.getText().toString();
                dni = dniET.getText().toString();
                tipo = tipoUsuarioRG.getCheckedRadioButtonId() == R.id.REGISTRAR_propietario_RB ? "PROPIETARIO" : "CLIENTE";

                if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()){
                    Toast.makeText(RegistrarActivity.this, "Completa los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (contrasena.length() < 6){
                    Toast.makeText(RegistrarActivity.this, "La contrase??a debe tener 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }
                registrarUsuario();
                break;
        }
    }

    private void registrarUsuario(){
        firebaseAuth.createUserWithEmailAndPassword(email, contrasena).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               Map<String, Object> mapaValores = new HashMap<>();
               mapaValores.put("nombre", nombre);
               mapaValores.put("email", email);
               mapaValores.put("contrasena", contrasena);
               mapaValores.put("tipo", tipo);
               mapaValores.put("dni", dni);
               mapaValores.put("token_fcm", "");

               String id = firebaseAuth.getCurrentUser().getUid();

               databaseFirebase.child("Usuarios").child(id).setValue(mapaValores).addOnCompleteListener(taskDB -> {
                   if (taskDB.isSuccessful()){
                       startActivity(new Intent(RegistrarActivity.this, LoginActivity.class));
                       finish();
                   } else {
                       Toast.makeText(getApplicationContext(), "No se pudieron crear los datos correctamente", Toast.LENGTH_SHORT).show();
                   }
               });

               updatearToken();

           } else {
               Toast.makeText(getApplicationContext(), "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void updatearToken(){
        String id = firebaseAuth.getCurrentUser().getUid();

        firebaseMessaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("Error FCM token", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                Map<String, Object> mapaValores = new HashMap<>();

                String token = task.getResult();

                mapaValores.put("token_fcm", token);

                databaseFirebase.child("Usuarios").child(id).updateChildren(mapaValores).addOnCompleteListener(taskDB -> {
                    if (!taskDB.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "No se pudo actualizar el token", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}