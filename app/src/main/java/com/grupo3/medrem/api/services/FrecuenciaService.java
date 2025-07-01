package com.grupo3.medrem.api.services;

import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.models.Frecuencia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FrecuenciaService {
    @GET("/api/v1/frecuencias")
    Call<ApiResponse<List<Frecuencia>>> listarFrecuencias();
}
