package com.mmw.inmueblelibre.repository;

import android.util.Log;

import com.mmw.inmueblelibre.apidao.BuilderAPI;
import com.mmw.inmueblelibre.apidao.CiudadesAPI;
import com.mmw.inmueblelibre.model.ListaCiudadesModel;
import com.mmw.inmueblelibre.model.ListaProvinciasModel;

import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CiudadesRepository {
    private CiudadesAPI ciudadesApi;

    public CiudadesRepository(){
        ciudadesApi = BuilderAPI.getInstancia().getCiudadesAPI();
    }

    public void getProvincias(GetProvinciasCallback callback){
        Call<ListaProvinciasModel> call = ciudadesApi.getProvincias();
        call.enqueue(new Callback<ListaProvinciasModel>() {
            @Override
            public void onResponse(Call<ListaProvinciasModel> call, Response<ListaProvinciasModel> response) {
                callback.resultado(true, response.body());
            }

            @Override
            public void onFailure(Call<ListaProvinciasModel> call, Throwable t) {
                Log.println(Log.ERROR, "API GET PROVINCIAS ERROR", t.toString());
                callback.resultado(false, null);
            }
        });
    }

    public void getCiudades(String nombreProvincia, GetCiudadesCallback callback){
        Call<ListaCiudadesModel> call = ciudadesApi.getCiudadesByProvincia(nombreProvincia);
        call.enqueue(new Callback<ListaCiudadesModel>() {
            @Override
            public void onResponse(Call<ListaCiudadesModel> call, Response<ListaCiudadesModel> response) {
                callback.resultado(true, response.body());
            }

            @Override
            public void onFailure(Call<ListaCiudadesModel> call, Throwable t) {
                Log.println(Log.ERROR, "API GET CIUDADES ERROR", t.toString());
                callback.resultado(false, null);
            }
        });
    }

    public interface GetProvinciasCallback {
        void resultado (final boolean exito, ListaProvinciasModel provincias);
    }
    public interface GetCiudadesCallback {
        void resultado (final boolean exito, ListaCiudadesModel ciudades);
    }
}
