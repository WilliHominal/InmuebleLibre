package com.mmw.inmueblelibre.UI.global;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmw.inmueblelibre.UI.cliente.InicioClienteActivity;
import com.mmw.inmueblelibre.UI.propietario.InicioPropietarioActivity;
import com.mmw.inmueblelibre.R;

import java.util.HashMap;
import java.util.Map;

public class ConfiguracionCuentaActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    TextView nombreCuenta;
    TextView dniCuenta;
    EditText correoCuenta;
    EditText contrasenaCuenta;

    Button guardarCambiosBtn;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_cuenta);

        toolbar = findViewById(R.id.CONFIG_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        nombreCuenta = findViewById(R.id.CONFIG_nombre);
        dniCuenta = findViewById(R.id.CONFIG_dni);
        correoCuenta = findViewById(R.id.CONFIG_email_ET);
        contrasenaCuenta = findViewById(R.id.CONFIG_contrasena_ET);
        guardarCambiosBtn = findViewById(R.id.CONFIG_modificar_usuario_BTN);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        obtenerInfoUsuario();

        guardarCambiosBtn.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                volverAtras();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.CONFIG_modificar_usuario_BTN:
                guardarCambios();
                break;
        }
    }

    private void guardarCambios(){
        String id = firebaseAuth.getCurrentUser().getUid();

        String correo = correoCuenta.getText().toString();
        String contrasena = contrasenaCuenta.getText().toString();

        if (correo.isEmpty() || contrasena.isEmpty()) return;

        Map<String, Object> mapaValores = new HashMap<>();
        mapaValores.put("email", correo);
        mapaValores.put("contrasena", contrasena);

        firebaseAuth.getCurrentUser().updateEmail(correo);
        firebaseAuth.getCurrentUser().updatePassword(contrasena);

        databaseFirebase.child("Usuarios").child(id).updateChildren(mapaValores).addOnCompleteListener(taskDB -> {
            if (taskDB.isSuccessful()){
                Toast.makeText(getApplicationContext(), "Datos modificados exitosamente", Toast.LENGTH_SHORT).show();
                volverAtras();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudieron guardar los cambios", Toast.LENGTH_SHORT).show();
            }
        });;
    }

    private void obtenerInfoUsuario(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre = snapshot.child("nombre").getValue().toString();
                    String dni = snapshot.child("dni").getValue().toString();
                    String correo = snapshot.child("email").getValue().toString();
                    String contrasena = snapshot.child("contrasena").getValue().toString();

                    nombreCuenta.setText(nombre);
                    dniCuenta.setText(dni);
                    correoCuenta.setText(correo);
                    contrasenaCuenta.setText(contrasena);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void volverAtras(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String tipo = snapshot.child("tipo").getValue().toString();

                    if (tipo.equals("PROPIETARIO")){
                        startActivity(new Intent(ConfiguracionCuentaActivity.this, InicioPropietarioActivity.class));
                    } else {
                        startActivity(new Intent(ConfiguracionCuentaActivity.this, InicioClienteActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}