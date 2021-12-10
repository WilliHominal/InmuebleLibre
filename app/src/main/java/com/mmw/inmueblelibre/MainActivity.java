package com.mmw.inmueblelibre;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button botonPruebaAgregarInmueble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonPruebaAgregarInmueble = (Button) findViewById(R.id.prueba_agregar_inmueble_BTN);

        botonPruebaAgregarInmueble.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prueba_agregar_inmueble_BTN:
                Intent intent = new Intent(MainActivity.this, AgregarInmuebleActivity.class);
                startActivity(intent);
        }
    }
}