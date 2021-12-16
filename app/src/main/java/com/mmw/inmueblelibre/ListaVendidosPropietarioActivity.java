package com.mmw.inmueblelibre;

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
import com.mmw.inmueblelibre.adapter.InmuebleAdapter;
import com.mmw.inmueblelibre.model.InmuebleModel;

import java.util.ArrayList;
import java.util.List;

public class ListaVendidosPropietarioActivity extends AppCompatActivity {

    Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseFirebase;

    private DrawerLayout drawerLayout;
    private NavigationView menuDrawer;

    RecyclerView.LayoutManager vendidosLayoutManager;
    RecyclerView listaVendidosRV;
    InmuebleAdapter adaptadorListaVendidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_vendidos_propietario);

        toolbar = findViewById(R.id.LVP_toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        drawerLayout = (DrawerLayout) findViewById(R.id.LVP_drawer_layout);
        menuDrawer = (NavigationView) findViewById(R.id.LVP_menu_drawer);

        listaVendidosRV = findViewById(R.id.LVP_listaInmuebles);
        vendidosLayoutManager = new LinearLayoutManager(this);
        listaVendidosRV.setLayoutManager(vendidosLayoutManager);
        adaptadorListaVendidos = new InmuebleAdapter(new ArrayList<>());
        listaVendidosRV.setAdapter(adaptadorListaVendidos);
        obtenerLista();

        //TODO IMPLEMENTAR ACCIONES DEL MENU DRAWER
        menuDrawer.setNavigationItemSelectedListener(menuItem -> {

            drawerLayout.closeDrawer(menuDrawer);

            switch (menuItem.getItemId()){
                case R.id.MENUPROP_menu_principal_opc:
                    startActivity(new Intent(ListaVendidosPropietarioActivity.this, InicioPropietarioActivity.class));
                    break;

                case R.id.MENUPROP_listar_reservas_opc:
                    startActivity(new Intent(ListaVendidosPropietarioActivity.this, ListaReservasPropietarioActivity.class));
                    break;

                case R.id.MENUPROP_listar_vendidos_opc:
                    break;

                case R.id.MENUPROP_mi_cuenta_opc:
                    startActivity(new Intent(ListaVendidosPropietarioActivity.this, ConfiguracionCuentaActivity.class));
                    break;

                case R.id.MENUPROP_cerrar_sesion_opc:
                    firebaseAuth.signOut();
                    startActivity(new Intent(ListaVendidosPropietarioActivity.this, MainActivity.class));
                    finish();
                    break;
            }

            return false;
        });

        obtenerInfoUsuario();
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

    private void obtenerLista(){
        databaseFirebase.child("Inmuebles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<InmuebleModel> listaVendidos = new ArrayList<>();

                    for (DataSnapshot snap : snapshot.getChildren()){
                        String id = snap.getKey();
                        String precio = snap.child("precio").getValue().toString();
                        String direccion = (snap.child("direccion").getValue().toString().split(": "))[1];
                        String idProp = snap.child("id_propietario").getValue().toString();
                        String estado = snap.child("estado").getValue().toString();

                        if (!idProp.equals(firebaseAuth.getCurrentUser().getUid())) continue;
                        if (!estado.equals("VENDIDO")) continue;

                        InmuebleModel inmTemp = new InmuebleModel(id, direccion, precio);

                        listaVendidos.add(inmTemp);

                    }
                    adaptadorListaVendidos = new InmuebleAdapter(listaVendidos);
                    listaVendidosRV.setAdapter(adaptadorListaVendidos);
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