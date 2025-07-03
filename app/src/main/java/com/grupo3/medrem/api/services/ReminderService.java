package com.grupo3.medrem.api.services;

import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.data.dto.request.NewReminderRequest;
import com.grupo3.medrem.data.dto.response.ReminderResponse;
import com.grupo3.medrem.data.dto.response.ReminderDetailResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;
import java.util.Map;

public interface ReminderService {
    @POST("/api/v1/recordatorios")
    Call<ApiResponse<Map<String, Integer>>> register(@Body NewReminderRequest request);

    @GET("/api/v1/recordatorios/usuario/{idUsuario}")
    Call<ApiResponse<List<ReminderDetailResponse>>> getRemindersByUser(@Path("idUsuario") int idUsuario);
}
