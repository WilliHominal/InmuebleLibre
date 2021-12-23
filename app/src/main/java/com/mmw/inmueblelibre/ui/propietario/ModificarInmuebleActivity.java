package com.mmw.inmueblelibre.ui.propietario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mmw.inmueblelibre.R;
import com.mmw.inmueblelibre.model.ListaCiudadesModel;
import com.mmw.inmueblelibre.model.ListaProvinciasModel;
import com.mmw.inmueblelibre.repository.CiudadesRepository;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ModificarInmuebleActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private final int LAUNCH_MAPS_ACTIVITY = 123;

    Toolbar toolbar;

    Button seleccionarUbicacionBtn;
    GoogleMap minimapa;
    LatLng ubiSeleccionada;

    Spinner provinciasSpinner;
    Spinner ciudadesSpinner;

    EditText descripcionInmuebleET;
    EditText precioInmuebleET;
    Button modificarInmuebleBtn;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseFirebase;

    CiudadesRepository repoCiudades;

    private boolean seleccionCargaDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_inmueble);

        seleccionCargaDatos = false;

        toolbar = (Toolbar) findViewById(R.id.MI_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseDatabase.getInstance().getReference();

        //Seteo el mapa en el fragmento
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MI_minimapa);
        mapFragment.getMapAsync(this);

        //Inicializo la ubiSeleccionada en 0,0
        ubiSeleccionada = new LatLng(0, 0);

        //Linkeo elementos de la UI y agrego sus listeners
        repoCiudades = new CiudadesRepository();
        seleccionarUbicacionBtn = (Button) findViewById(R.id.MI_seleccionar_ubicacion_BTN);
        provinciasSpinner = (Spinner) findViewById(R.id.MI_provincia_SP);
        ciudadesSpinner = (Spinner) findViewById(R.id.MI_ciudad_SP);
        descripcionInmuebleET = findViewById(R.id.MI_descripcion_ET);
        precioInmuebleET = findViewById(R.id.MI_precio_ET);
        modificarInmuebleBtn = findViewById(R.id.MI_modificar_inmueble_BTN);

        modificarInmuebleBtn.setOnClickListener(this);
        seleccionarUbicacionBtn.setOnClickListener(this);
        provinciasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (seleccionCargaDatos){
                    seleccionCargaDatos = false;
                    return;
                }

                repoCiudades.getCiudades(provinciasSpinner.getSelectedItem().toString(), (exito, ciudades) -> {
                    Collections.sort(ciudades.getMunicipios(), (c1, c2) -> c1.getNombre().compareTo(c2.getNombre()));
                    ArrayAdapter<ListaCiudadesModel.CiudadModel> adaptadorCiudades = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, ciudades.getMunicipios());
                    ciudadesSpinner.setAdapter(adaptadorCiudades);
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        cargarDatos();
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
    public void onMapReady(GoogleMap googleMap) {
        minimapa = googleMap;
        minimapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        minimapa.getUiSettings().setAllGesturesEnabled(false);
        minimapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 1));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.MI_seleccionar_ubicacion_BTN:
                Intent intent = new Intent (ModificarInmuebleActivity.this, MapsActivity.class);
                intent.putExtra("init_lat", ubiSeleccionada.latitude);
                intent.putExtra("init_lon", ubiSeleccionada.longitude);
                startActivityForResult(intent, LAUNCH_MAPS_ACTIVITY);
                break;
            case R.id.MI_modificar_inmueble_BTN:
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
        mapaValores.put("id_propietario", id);
        mapaValores.put("id_cliente", "");
        mapaValores.put("descripcion", descripcion);
        mapaValores.put("precio", precio);
        mapaValores.put("provincia", provinciasSpinner.getSelectedItem().toString());
        mapaValores.put("ciudad", ciudadesSpinner.getSelectedItem().toString());
        mapaValores.put("direccion", ubiSeleccionada.toString());
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        String fechaAlta = ISO_8601_FORMAT.format(new Date());
        mapaValores.put("fecha_alta", fechaAlta);
        mapaValores.put("fecha_reserva", "");
        mapaValores.put("fecha_venta", "");
        mapaValores.put("estado", "CREADO");

        databaseFirebase.child("Inmuebles").push().setValue(mapaValores).addOnCompleteListener(taskDB -> {
            if (taskDB.isSuccessful()){
                Toast.makeText(getApplicationContext(), "Inmueble registrado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ModificarInmuebleActivity.this, InicioPropietarioActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudieron crear los datos correctamente", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cargarDatos() {
        String idInmueble = getIntent().getStringExtra("id_inmueble");

        databaseFirebase.child("Inmuebles").child(idInmueble).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String descripcion = snapshot.child("descripcion").getValue().toString();
                    String precio = snapshot.child("precio").getValue().toString();
                    String provincia = snapshot.child("provincia").getValue().toString();
                    String ciudad = snapshot.child("ciudad").getValue().toString();
                    String direccion = snapshot.child("direccion").getValue().toString();

                    descripcionInmuebleET.setText(descripcion);
                    precioInmuebleET.setText(precio);

                    ListaProvinciasModel lpm = new ListaProvinciasModel(null);
                    ListaProvinciasModel.ProvinciaModel prov = lpm.new ProvinciaModel(provincia);

                    repoCiudades.getProvincias((exito, provincias) -> {
                        Collections.sort(provincias.getProvincias(), (p1, p2) -> p1.getNombre().compareTo(p2.getNombre()));

                        int posProvincia = 0;

                        for (int posActual = 0; posActual < provincias.getProvincias().size(); posActual++){
                            if (provincias.getProvincias().get(posActual).getNombre().equals(provincia)){
                                posProvincia = posActual;
                            }
                        }

                        ArrayAdapter<ListaProvinciasModel.ProvinciaModel> adaptadorProvincias = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, provincias.getProvincias());
                        provinciasSpinner.setAdapter(adaptadorProvincias);
                        seleccionCargaDatos = true;
                        provinciasSpinner.setSelection(posProvincia);

                        repoCiudades.getCiudades(provincia, (exito2, ciudades) -> {
                            Collections.sort(ciudades.getMunicipios(), (c1, c2) -> c1.getNombre().compareTo(c2.getNombre()));

                            int posCiudad = 0;

                            for (int posActual = 0; posActual < ciudades.getMunicipios().size(); posActual++){
                                Log.d("PROBLEMAS", posActual + ": " + ciudades.getMunicipios().get(posActual).getNombre());
                                if (ciudades.getMunicipios().get(posActual).getNombre().equals(ciudad)){
                                    posCiudad = posActual;
                                }
                            }

                            ArrayAdapter<ListaCiudadesModel.CiudadModel> adaptadorCiudades = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, ciudades.getMunicipios());
                            ciudadesSpinner.setAdapter(adaptadorCiudades);
                            ciudadesSpinner.setSelection(posCiudad);
                        });


                    });

                    String latlngaux = direccion.split("\\(")[1];
                    String latlng = latlngaux.split("\\)")[0];
                    Double lat = Double.valueOf(latlng.split(",")[0]);
                    Double lon = Double.valueOf(latlng.split(",")[1]);
                    minimapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon),  18));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}