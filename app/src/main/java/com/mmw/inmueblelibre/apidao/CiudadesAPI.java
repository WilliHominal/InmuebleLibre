package com.mmw.inmueblelibre.apidao;

import com.mmw.inmueblelibre.model.ListaCiudadesModel;
import com.mmw.inmueblelibre.model.ListaProvinciasModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CiudadesAPI {
    @GET("municipios?max=500&campos=nombre, centroide")
    Call<ListaCiudadesModel> getCiudadesByProvincia(@Query("provincia") String provincia);

    @GET("provincias?campos=nombre&max=24")
    Call<ListaProvinciasModel> getProvincias();
}
