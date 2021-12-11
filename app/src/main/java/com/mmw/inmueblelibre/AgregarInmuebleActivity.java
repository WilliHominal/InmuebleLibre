package com.mmw.inmueblelibre;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mmw.inmueblelibre.model.ListaCiudadesModel;
import com.mmw.inmueblelibre.model.ListaProvinciasModel;
import com.mmw.inmueblelibre.repository.CiudadesRepository;

import java.util.Collections;
import java.util.Comparator;

public class AgregarInmuebleActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private final int LAUNCH_MAPS_ACTIVITY = 123;

    Button seleccionarUbicacionBtn;
    GoogleMap minimapa;
    LatLng ubiSeleccionada;

    Spinner provinciasSpinner;
    Spinner ciudadesSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_inmueble);

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
}