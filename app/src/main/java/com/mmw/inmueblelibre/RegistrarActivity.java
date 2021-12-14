package com.mmw.inmueblelibre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrarActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nombreET;
    private EditText emailET;
    private EditText contrasenaET;
    private Button registrarseBTN;
    private RadioGroup tipoUsuarioRG;

    private String nombre;
    private String email;
    private String contrasena;
    private String tipo;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        nombreET = (EditText) findViewById(R.id.REGISTRAR_nombre_ET);
        emailET = (EditText) findViewById(R.id.REGISTRAR_email_ET);
        contrasenaET = (EditText) findViewById(R.id.REGISTRAR_contrasena_ET);
        registrarseBTN = (Button) findViewById(R.id.REGISTRAR_registrarse_BTN);
        tipoUsuarioRG = (RadioGroup) findViewById(R.id.REGISTRAR_tipo_usuario_RG);

        registrarseBTN.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.REGISTRAR_registrarse_BTN:
                nombre = nombreET.getText().toString();
                email = emailET.getText().toString();
                contrasena = contrasenaET.getText().toString();
                tipo = tipoUsuarioRG.getCheckedRadioButtonId() == R.id.REGISTRAR_propietario_RB ? "PROPIETARIO" : "CLIENTE";

                if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()){
                    Toast.makeText(RegistrarActivity.this, "Completa los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (contrasena.length() < 6){
                    Toast.makeText(RegistrarActivity.this, "La contraseña debe tener 6 caracteres", Toast.LENGTH_SHORT).show();
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

               String id = firebaseAuth.getCurrentUser().getUid();

               databaseFirebase.child("Usuarios").child(id).setValue(mapaValores).addOnCompleteListener(taskDB -> {
                   if (taskDB.isSuccessful()){
                       startActivity(new Intent(RegistrarActivity.this, MainActivity.class));
                       finish();
                   } else {
                       Toast.makeText(getApplicationContext(), "No se pudieron crear los datos correctamente", Toast.LENGTH_SHORT).show();
                   }
               });

           } else {
               Toast.makeText(getApplicationContext(), "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
           }
        });
    }
}