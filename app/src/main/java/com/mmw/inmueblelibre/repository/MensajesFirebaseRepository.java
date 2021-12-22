package com.mmw.inmueblelibre.repository;

import android.util.Log;

import com.mmw.inmueblelibre.apidao.BuilderAPI;
import com.mmw.inmueblelibre.apidao.MensajesFirebaseAPI;
import com.mmw.inmueblelibre.model.MensajeFirebaseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MensajesFirebaseRepository {

    MensajesFirebaseAPI mensajesAPI;

    public MensajesFirebaseRepository (){
        mensajesAPI = BuilderAPI.getInstancia().getMensajesFirebaseAPI();
    }

    public void enviarMensaje (String receptor, String titulo, String texto, String tipoCliente, String idInmueble, EnviarMensajeCallback callback){
        MensajeFirebaseModel mensaje = new MensajeFirebaseModel(receptor, titulo, texto, tipoCliente, idInmueble);

        Call<MensajeFirebaseModel> llamada = mensajesAPI.enviarMensajeFirebase(mensaje);
        llamada.enqueue(new Callback<MensajeFirebaseModel>() {
            @Override
            public void onResponse(Call<MensajeFirebaseModel> call, Response<MensajeFirebaseModel> response) {
                callback.resultado(true);
                Log.d("RESPUESTA_MENSAJE", response.toString());
            }

            @Override
            public void onFailure(Call<MensajeFirebaseModel> call, Throwable t) {
                callback.resultado(false);
            }
        });
    }

    public interface EnviarMensajeCallback {
        void resultado(final boolean exito);
    }
}
