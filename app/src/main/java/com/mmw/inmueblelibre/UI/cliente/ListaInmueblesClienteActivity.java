package com.mmw.inmueblelibre.UI.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmw.inmueblelibre.UI.global.MainActivity;
import com.mmw.inmueblelibre.R;
import com.mmw.inmueblelibre.UI.global.ConfiguracionCuentaActivity;
import com.mmw.inmueblelibre.adapter.InmuebleAdapter;
import com.mmw.inmueblelibre.model.InmuebleModel;

import java.util.ArrayList;
import java.util.List;

public class ListaInmueblesClienteActivity extends AppCompatActivity {

    Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseFirebase;

    private DrawerLayout drawerLayout;
    private NavigationView menuDrawer;

    RecyclerView.LayoutManager inmueblesLayoutManager;
    RecyclerView listaInmueblesRV;
    InmuebleAdapter adaptadorListaInmuebles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_inmuebles_cliente);

        toolbar = findViewById(R.id.LIC_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.LIC_drawer_layout);
        menuDrawer = (NavigationView) findViewById(R.id.LIC_menu_drawer);

        listaInmueblesRV = findViewById(R.id.LIC_listaInmuebles);
        inmueblesLayoutManager = new LinearLayoutManager(this);
        listaInmueblesRV.setLayoutManager(inmueblesLayoutManager);
        adaptadorListaInmuebles = new InmuebleAdapter(new ArrayList<>());
        listaInmueblesRV.setAdapter(adaptadorListaInmuebles);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        obtenerLista();

        obtenerInfoUsuario();

        menuDrawer.setNavigationItemSelectedListener(menuItem -> {

            drawerLayout.closeDrawer(menuDrawer);

            switch (menuItem.getItemId()) {
                case R.id.MENUCLI_menu_principal_opc:
                    startActivity(new Intent(ListaInmueblesClienteActivity.this, InicioClienteActivity.class));
                    break;

                case R.id.MENUCLI_listar_inmuebles_opc:
                    break;

                case R.id.MENUCLI_listar_comprados_opc:
                    startActivity(new Intent(ListaInmueblesClienteActivity.this, ListaCompradosClienteActivity.class));
                    break;

                case R.id.MENUCLI_mi_cuenta_opc:
                    startActivity(new Intent(ListaInmueblesClienteActivity.this, ConfiguracionCuentaActivity.class));
                    break;

                case R.id.MENUCLI_cerrar_sesion_opc:
                    //CIERRA SESION Y DEVUELVE AL MENÚ INICIAL
                    firebaseAuth.signOut();
                    startActivity(new Intent(ListaInmueblesClienteActivity.this, MainActivity.class));
                    finish();
                    break;
            }

            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_opciones_opt) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void obtenerInfoUsuario(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre = snapshot.child("nombre").getValue().toString();
                    String correo = snapshot.child("email").getValue().toString();

                    View drawerHeader = menuDrawer.getHeaderView(0);
                    ((TextView) drawerHeader.findViewById(R.id.DRAWERCLI_nombre_TV)).setText(nombre);
                    ((TextView) drawerHeader.findViewById(R.id.DRAWERCLI_correo_TV)).setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void obtenerLista(){
        databaseFirebase.child("Inmuebles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<InmuebleModel> listaInmuebles = new ArrayList<>();

                    for (DataSnapshot snap : snapshot.getChildren()){
                        String id = snap.getKey();

                        String estado = snap.child("estado").getValue().toString();
                        String precio = snap.child("precio").getValue().toString();
                        String direccion = (snap.child("direccion").getValue().toString().split(": "))[1];

                        if (!estado.equals("CREADO")) continue;

                        InmuebleModel inmTemp = new InmuebleModel(id, direccion, precio);

                        listaInmuebles.add(inmTemp);
                    }

                    adaptadorListaInmuebles = new InmuebleAdapter(listaInmuebles);
                    listaInmueblesRV.setAdapter(adaptadorListaInmuebles);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}