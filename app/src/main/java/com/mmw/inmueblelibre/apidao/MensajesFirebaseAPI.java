package com.mmw.inmueblelibre.apidao;

import com.mmw.inmueblelibre.model.MensajeFirebaseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MensajesFirebaseAPI {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAONc-zwA:APA91bHI7VG5IkZIjVsV_ltfaMCbsAj0IZv2XD48mIWifUR1gF6o9NnM7tOsfAI-nbgl_vUGfxJRNrHpezBTB-E8GbRgOg-NGscmLj6Xpx29G4JihWubbdx_Q-07Wb3ArFN9hrhmQESX"
    })
    @POST("send")
    Call<MensajeFirebaseModel> enviarMensajeFirebase(@Body MensajeFirebaseModel mensaje);
}
