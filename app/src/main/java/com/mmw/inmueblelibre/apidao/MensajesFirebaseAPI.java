package com.mmw.inmueblelibre.apidao;

import com.mmw.inmueblelibre.BuildConfig;
import com.mmw.inmueblelibre.model.MensajeFirebaseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MensajesFirebaseAPI {
    String key = BuildConfig.SERVER_KEY;

    @Headers({
            "Content-Type: application/json",
            "Authorization: key="+key
    })
    @POST("send")
    Call<MensajeFirebaseModel> enviarMensajeFirebase(@Body MensajeFirebaseModel mensaje);
}
