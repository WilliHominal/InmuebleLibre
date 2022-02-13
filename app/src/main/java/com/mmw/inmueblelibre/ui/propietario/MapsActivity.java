package com.mmw.inmueblelibre.ui.propietario;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mmw.inmueblelibre.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mapa;
    private FusedLocationProviderClient fusedLocationClient;
    private int cantidadMarcadores;
    private Marker marcador;
    private Button seleccionarUbicacionBTN;

    private final int CODIGO_PERMISOS_UBICACION = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Obtengo el Mapa y lo asigno al fragmento
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MA_mapa);
        mapFragment.getMapAsync(this);

        //Inicializo en 0 la cantidad de marcadores
        cantidadMarcadores = 0;
        //Inicializo el cliente para poder obtener la ubicacion actual
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Linkeo el botón seleccionar ubicación y le asigno el listener
        seleccionarUbicacionBTN = (Button) findViewById(R.id.MA_seleccionar_ubicacion_BTN);
        seleccionarUbicacionBTN.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Al cargar el mapa, lo asigno a la variable map, y le doy el tipo hibrido
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Bloquear inclinación y rotación
        mapa.getUiSettings().setTiltGesturesEnabled(false);
        mapa.getUiSettings().setRotateGesturesEnabled(false);

        //Habilitar botones zoom y deshabilitar botones del marcador
        mapa.getUiSettings().setZoomControlsEnabled(true);
        mapa.getUiSettings().setMapToolbarEnabled(false);

        //Seteo la ubicación central del mapa en las coords pasadas como extras (init_lat, init_lon) al intent
        LatLng ubicacionInicial = new LatLng(getIntent().getDoubleExtra("init_lat", 0), getIntent().getDoubleExtra("init_lon", 0));
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionInicial, 16));

        //Asigno el listener al mantener presionado por más de un segundo
        mapa.setOnMapLongClickListener(latLng -> {
            if (cantidadMarcadores == 0){
                Toast.makeText(getApplicationContext(), "Marcador agregado", Toast.LENGTH_SHORT).show();
                marcador = mapa.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("UBI")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
                cantidadMarcadores++;
                return;
            }
            Toast.makeText(getApplicationContext(), "Ya hay un marcador existente, arrástralo a la nueva ubicación", Toast.LENGTH_SHORT).show();
        });

        actualizarMapa();
    }

    @SuppressLint("MissingPermission")
    private void actualizarMapa() {
        //Chequeo si faltan los permisos necesarios de ubicacion y los pido si hace falta
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, CODIGO_PERMISOS_UBICACION);
                return;
        }

        //Agrego la última ubicación disponible como ubicación central para el mapa si no recibió lat/lon iniciales.
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            Double lat = getIntent().getDoubleExtra("init_lat", 0);
            Double lon = getIntent().getDoubleExtra("init_lon", 0);
            if (location != null && lat == 0 && lon == 0){
                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
            }
        });

        //Agrego el botón para ir a la ubicación actual (por si no se detectó automáticamente)
        mapa.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case CODIGO_PERMISOS_UBICACION:
                //Si se otorgaron permisos, actualizo el mapa.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    actualizarMapa();
                } else {
                    //Si no se otorgaron permisos muestro mensajes de que son necesarios
                    Toast.makeText(getApplicationContext(), "Debes darnos permisos para continuar!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.MA_seleccionar_ubicacion_BTN:
                //Si no hay marcadores, pedir que se agregue alguno
                if (cantidadMarcadores == 0){
                    Toast.makeText(getApplicationContext(), "Mantén presionado en el mapa para añadir un marcador en la ubicación correspondiente.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Creo intent para devolverlo con latitud/longitud del marcador
                Intent returnIntent = new Intent();
                returnIntent.putExtra("latitud",marcador.getPosition().latitude);
                returnIntent.putExtra("longitud",marcador.getPosition().longitude);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
        }
    }
}