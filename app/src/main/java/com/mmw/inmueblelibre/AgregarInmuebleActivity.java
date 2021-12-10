package com.mmw.inmueblelibre;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AgregarInmuebleActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private final int LAUNCH_MAPS_ACTIVITY = 123;

    Button seleccionarUbicacionBtn;
    GoogleMap minimapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_inmueble);

        //Seteo el mapa en el fragmento
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.AI_minimapa);
        mapFragment.getMapAsync(this);

        //Linkeo botones y agrego sus listeners
        seleccionarUbicacionBtn = (Button) findViewById(R.id.AI_seleccionar_ubicacion_BTN);
        seleccionarUbicacionBtn.setOnClickListener(this);
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
                //putextra init_lat, init_lon
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
                minimapa.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));
                minimapa.clear();
                minimapa.addMarker(new MarkerOptions().position(pos));
                break;
        }
    }
}