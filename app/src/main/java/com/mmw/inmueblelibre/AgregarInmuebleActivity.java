package com.mmw.inmueblelibre;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mmw.inmueblelibre.model.ListaCiudadesModel;
import com.mmw.inmueblelibre.model.ListaProvinciasModel;
import com.mmw.inmueblelibre.repository.CiudadesRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AgregarInmuebleActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private final int LAUNCH_MAPS_ACTIVITY = 123;

    Button seleccionarUbicacionBtn;
    GoogleMap minimapa;
    LatLng ubiSeleccionada;

    Spinner provinciasSpinner;
    Spinner ciudadesSpinner;

    EditText descripcionInmuebleET;
    EditText precioInmuebleET;
    Button registrarInmuebleBtn;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_inmueble);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        //Seteo el mapa en el fragmento
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.AI_minimapa);
        mapFragment.getMapAsync(this);

        //Inicializo la ubiSeleccionada en 0,0
        ubiSeleccionada = new LatLng(0, 0);

        //Linkeo elementos de la UI y agrego sus listeners
        CiudadesRepository repoCiudades = new CiudadesRepository();
        seleccionarUbicacionBtn = (Button) findViewById(R.id.AI_seleccionar_ubicacion_BTN);
        provinciasSpinner = (Spinner) findViewById(R.id.AI_provincia_SP);
        ciudadesSpinner = (Spinner) findViewById(R.id.AI_ciudad_SP);
        descripcionInmuebleET = findViewById(R.id.AI_descripcion_ET);
        precioInmuebleET = findViewById(R.id.AI_precio_ET);
        registrarInmuebleBtn = findViewById(R.id.AI_registrar_inmueble_BTN);

        registrarInmuebleBtn.setOnClickListener(this);
        seleccionarUbicacionBtn.setOnClickListener(this);
        provinciasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                repoCiudades.getCiudades(provinciasSpinner.getSelectedItem().toString(), (exito, ciudades) -> {
                    Collections.sort(ciudades.getMunicipios(), (c1, c2) -> c1.getNombre().compareTo(c2.getNombre()));
                    ArrayAdapter<ListaCiudadesModel.CiudadModel> adaptadorCiudades = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, ciudades.getMunicipios());
                    ciudadesSpinner.setAdapter(adaptadorCiudades);
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        repoCiudades.getProvincias((exito, provincias) -> {
            Collections.sort(provincias.getProvincias(), (p1, p2) -> p1.getNombre().compareTo(p2.getNombre()));
            ArrayAdapter<ListaProvinciasModel.ProvinciaModel> adaptadorProvincias = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, provincias.getProvincias());
            provinciasSpinner.setAdapter(adaptadorProvincias);
        });
        repoCiudades.getCiudades("Buenos Aires", (exito, ciudades) -> {
            Collections.sort(ciudades.getMunicipios(), (c1, c2) -> c1.getNombre().compareTo(c2.getNombre()));
            ArrayAdapter<ListaCiudadesModel.CiudadModel> adaptadorCiudades = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, ciudades.getMunicipios());
            ciudadesSpinner.setAdapter(adaptadorCiudades);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        minimapa = googleMap;
        minimapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        minimapa.getUiSettings().setAllGesturesEnabled(false);
        minimapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 1));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.AI_seleccionar_ubicacion_BTN:
                Intent intent = new Intent (AgregarInmuebleActivity.this, MapsActivity.class);
                intent.putExtra("init_lat", ubiSeleccionada.latitude);
                intent.putExtra("init_lon", ubiSeleccionada.longitude);
                startActivityForResult(intent, LAUNCH_MAPS_ACTIVITY);
                break;
            case R.id.AI_registrar_inmueble_BTN:
                guardarInmueble();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case LAUNCH_MAPS_ACTIVITY:
                if (data == null) return;
                LatLng pos = new LatLng(data.getDoubleExtra("latitud", 0), data.getDoubleExtra("longitud", 0));
                minimapa.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));
                minimapa.clear();
                minimapa.addMarker(new MarkerOptions().position(pos));
                ubiSeleccionada = pos;
                break;
        }
    }

    private void guardarInmueble() {
        String descripcion = descripcionInmuebleET.getText().toString();
        String precio = precioInmuebleET.getText().toString();

        if (precio.isEmpty() || descripcion.isEmpty()){
            Toast.makeText(getApplicationContext(), "Completa los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ubiSeleccionada.latitude == 0 && ubiSeleccionada.longitude == 0){
            Toast.makeText(getApplicationContext(), "Selecciona la ubicación", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = firebaseAuth.getCurrentUser().getUid();
        Map<String, Object> mapaValores = new HashMap<>();
        mapaValores.put("descripcion", descripcion);
        mapaValores.put("precio", precio);
        mapaValores.put("provincia", provinciasSpinner.getSelectedItem().toString());
        mapaValores.put("ciudad", ciudadesSpinner.getSelectedItem().toString());
        mapaValores.put("direccion", ubiSeleccionada.toString());

        databaseFirebase.child("Usuarios").child(id).child("Inmuebles").push().setValue(mapaValores).addOnCompleteListener(taskDB -> {
            if (taskDB.isSuccessful()){
                Toast.makeText(getApplicationContext(), "Inmueble registrado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudieron crear los datos correctamente", Toast.LENGTH_SHORT).show();
            }
        });

    }
}