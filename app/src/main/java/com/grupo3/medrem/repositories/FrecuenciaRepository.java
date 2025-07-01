package com.grupo3.medrem.repositories;

import com.grupo3.medrem.BuildConfig;
import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.api.services.ApiServiceFactory;
import com.grupo3.medrem.api.services.FrecuenciaService;
import com.grupo3.medrem.models.Frecuencia;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrecuenciaRepository {
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private final FrecuenciaService frecuenciaService;

    public interface LoadCallback {
        void onSuccess(List<Frecuencia> frecuencias);
        void onError(String message);
    }

    public FrecuenciaRepository() {
        frecuenciaService = ApiServiceFactory.createFrecuenciaService(BASE_URL);
    }

    public void listarFrecuencias(final LoadCallback callback) {
        frecuenciaService.listarFrecuencias().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Frecuencia>>> call, Response<ApiResponse<List<Frecuencia>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Frecuencia>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Frecuencia> frecuencias = apiResponse.getData();
                        callback.onSuccess(frecuencias);
                    } else {
                        callback.onError(apiResponse.getMessage() != null ?
                                apiResponse.getMessage() : "Error al obtener frecuencias");
                    }
                }
                else {
                    callback.onError("Error al conectar con el servidor");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Frecuencia>>> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}
