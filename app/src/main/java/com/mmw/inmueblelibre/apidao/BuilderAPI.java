package com.mmw.inmueblelibre.apidao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuilderAPI {
    private static BuilderAPI _INSTANCIA;
    private CiudadesAPI ciudadesAPI;

    private void iniciarRetrofit(){
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient cliente = new OkHttpClient.Builder()
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.datos.gob.ar/georef/api/")
                .client(cliente)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ciudadesAPI = retrofit.create(CiudadesAPI.class);
    }

    public static BuilderAPI getInstancia(){
        if (_INSTANCIA == null){
            _INSTANCIA = new BuilderAPI();
        }
        return _INSTANCIA;
    }

    public CiudadesAPI getCiudadesAPI(){
        if (ciudadesAPI == null) {
            this.iniciarRetrofit();
        }
        return ciudadesAPI;
    }
}
