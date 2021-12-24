package com.mmw.inmueblelibre.ui.propietario;

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
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmw.inmueblelibre.ui.cliente.InicioClienteActivity;
import com.mmw.inmueblelibre.ui.global.MainActivity;
import com.mmw.inmueblelibre.R;
import com.mmw.inmueblelibre.ui.global.ConfiguracionCuentaActivity;
import com.mmw.inmueblelibre.ui.global.VerDetallesInmuebleActivity;
import com.mmw.inmueblelibre.adapter.InmuebleAdapter;
import com.mmw.inmueblelibre.model.InmuebleModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaReservasPropietarioActivity extends AppCompatActivity {

    Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseFirebase;

    private DrawerLayout drawerLayout;
    private NavigationView menuDrawer;

    RecyclerView.LayoutManager reservasLayoutManager;
    RecyclerView listaReservasRV;
    InmuebleAdapter adaptadorListaReservas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reservas_propietario);

        toolbar = findViewById(R.id.LRP_toolbar);
        toolbar.setTitle("LISTA DE RESERVAS");
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        drawerLayout = (DrawerLayout) findViewById(R.id.LRP_drawer_layout);
        menuDrawer = (NavigationView) findViewById(R.id.LRP_menu_drawer);

        listaReservasRV = findViewById(R.id.LRP_listaInmuebles);
        reservasLayoutManager = new LinearLayoutManager(this);
        listaReservasRV.setLayoutManager(reservasLayoutManager);
        adaptadorListaReservas = new InmuebleAdapter(new ArrayList<>());
        listaReservasRV.setAdapter(adaptadorListaReservas);
        obtenerLista();

        //TODO IMPLEMENTAR ACCIONES DEL MENU DRAWER
        menuDrawer.setNavigationItemSelectedListener(menuItem -> {

            drawerLayout.closeDrawer(menuDrawer);

            switch (menuItem.getItemId()){
                case R.id.MENUPROP_menu_principal_opc:
                    startActivity(new Intent(ListaReservasPropietarioActivity.this, InicioPropietarioActivity.class));
                    break;

                case R.id.MENUPROP_listar_reservas_opc:
                    break;

                case R.id.MENUPROP_listar_vendidos_opc:
                    startActivity(new Intent(ListaReservasPropietarioActivity.this, ListaVendidosPropietarioActivity.class));
                    break;

                case R.id.MENUPROP_mi_cuenta_opc:
                    startActivity(new Intent(ListaReservasPropietarioActivity.this, ConfiguracionCuentaActivity.class));
                    break;

                case R.id.MENUPROP_cerrar_sesion_opc:
                    cerrarSesion();
                    break;
            }

            return false;
        });

        obtenerInfoUsuario();

        adaptadorListaReservas.setOnItemClickListener(new InmuebleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                String tipoUsuario = "PROPIETARIO";
                String idInmueble = adaptadorListaReservas.getInmuebleId(position);

                Intent intent = new Intent(ListaReservasPropietarioActivity.this, VerDetallesInmuebleActivity.class);
                intent.putExtra("tipo_usuario", tipoUsuario);
                intent.putExtra("id_inmueble", idInmueble);
                intent.putExtra("estado_inmueble", "RESERVADO");
                startActivity(intent);
            }
        });
    }

    private void cerrarSesion(){
        String id = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> mapaValores = new HashMap<>();
        mapaValores.put("token_fcm", "");

        databaseFirebase.child("Usuarios").child(id).updateChildren(mapaValores).addOnCompleteListener(taskDB -> {
            if (taskDB.isSuccessful()){
                firebaseAuth.signOut();
                startActivity(new Intent(ListaReservasPropietarioActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudo actualizar el token", Toast.LENGTH_SHORT).show();
            }
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

    private void obtenerLista(){
        databaseFirebase.child("Inmuebles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<InmuebleModel> listaReservas = new ArrayList<>();

                    for (DataSnapshot snap : snapshot.getChildren()){
                        String id = snap.getKey();
                        String precio = snap.child("precio").getValue().toString();
                        String direccion = (snap.child("direccion").getValue().toString().split(": "))[1];
                        String idProp = snap.child("id_propietario").getValue().toString();
                        String estado = snap.child("estado").getValue().toString();

                        if (!idProp.equals(firebaseAuth.getCurrentUser().getUid())) continue;
                        if (!estado.equals("RESERVADO")) continue;

                        InmuebleModel inmTemp = new InmuebleModel(id, direccion, precio);

                        listaReservas.add(inmTemp);

                    }
                    adaptadorListaReservas = new InmuebleAdapter(listaReservas);
                    listaReservasRV.setAdapter(adaptadorListaReservas);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void obtenerInfoUsuario(){
        String id = firebaseAuth.getCurrentUser().getUid();

        databaseFirebase.child("Usuarios").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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