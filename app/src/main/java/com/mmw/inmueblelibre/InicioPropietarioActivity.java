package com.mmw.inmueblelibre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmw.inmueblelibre.adapter.InmuebleAdapter;
import com.mmw.inmueblelibre.model.InmuebleModel;

import java.util.ArrayList;
import java.util.List;

public class InicioPropietarioActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseFirebase;

    private DrawerLayout drawerLayout;
    private NavigationView menuDrawer;

    RecyclerView.LayoutManager inmueblesLayoutManager;
    RecyclerView listaInmueblesRV;
    InmuebleAdapter adaptadorListaInmuebles;

    private FloatingActionButton agregarInmuebleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_propietario);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        drawerLayout = (DrawerLayout) findViewById(R.id.INICIOPROP_drawer_layout);
        menuDrawer = (NavigationView) findViewById(R.id.INICIOPROP_menu_drawer);

        agregarInmuebleBtn = findViewById(R.id.INICIOPROP_agregar_inmueble_BTN);

        listaInmueblesRV = findViewById(R.id.INICIOPROP_listaInmuebles);
        inmueblesLayoutManager = new LinearLayoutManager(this);
        listaInmueblesRV.setLayoutManager(inmueblesLayoutManager);
        adaptadorListaInmuebles = new InmuebleAdapter(new ArrayList<>());
        listaInmueblesRV.setAdapter(adaptadorListaInmuebles);
        obtenerLista();


        agregarInmuebleBtn.setOnClickListener(this);

        //TODO IMPLEMENTAR ACCIONES DEL MENU DRAWER
        menuDrawer.setNavigationItemSelectedListener(menuItem -> {

            drawerLayout.closeDrawer(menuDrawer);

            switch (menuItem.getItemId()){
                case R.id.MENUPROP_menu_principal_opc:
                    //IR AL MENU PRINCIPAL (Listado de inmuebles del usuario)
                    break;

                case R.id.MENUPROP_listar_reservas_opc:
                    //IR A LISTADO DE RESERVAS
                    break;

                case R.id.MENUPROP_soporte_opc:
                    //IR A SOPORTE
                    break;

                case R.id.MENUPROP_mi_cuenta_opc:
                    //IR A CONFIGURACION DE LA CUENTA
                    break;

                case R.id.MENUPROP_cerrar_sesion_opc:
                    //CIERRA SESION Y DEVUELVE AL MENÚ INICIAL
                    firebaseAuth.signOut();
                    startActivity(new Intent(InicioPropietarioActivity.this, MainActivity.class));
                    finish();
                    break;
            }

            return false;
        });

        obtenerInfoUsuario();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.INICIOPROP_agregar_inmueble_BTN:
                startActivity(new Intent(InicioPropietarioActivity.this, AgregarInmuebleActivity.class));
                break;
        }
    }

    private void obtenerLista(){
        databaseFirebase.child("Inmuebles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<InmuebleModel> listaInmuebles = new ArrayList<>();

                    for (DataSnapshot snap : snapshot.getChildren()){
                        String id = snap.getKey().toString();
                        String precio = snap.child("precio").getValue().toString();
                        String direccion = (snap.child("direccion").getValue().toString().split(": "))[1];
                        String idProp = snap.child("id_propietario").getValue().toString();

                        if (!idProp.equals(firebaseAuth.getCurrentUser().getUid())) return;

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

    private void obtenerInfoUsuario(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String nombre = snapshot.child("nombre").getValue().toString();
                    String correo = snapshot.child("email").getValue().toString();

                    View drawerHeader = menuDrawer.getHeaderView(0);
                    ((TextView) drawerHeader.findViewById(R.id.DRAWERPROP_nombre_TV)).setText(nombre);
                    ((TextView) drawerHeader.findViewById(R.id.DRAWERPROP_correo_TV)).setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}