package com.grupo3.medrem.repositories;

import android.util.Log;

import com.grupo3.medrem.BuildConfig;
import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.api.services.ApiServiceFactory;
import com.grupo3.medrem.api.services.DiaRecordatorioService;
import com.grupo3.medrem.data.dto.request.DiaRecordatorioRequest;
import com.grupo3.medrem.models.Reminder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiaRecordatorioRepository {
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private final DiaRecordatorioService diaRecordatorioService;;

    public DiaRecordatorioRepository() {
        diaRecordatorioService = ApiServiceFactory.createDiaRecordatorioService(BASE_URL);
    }

    public void guardarDiasRecordatorio(List<Integer> dias, int idRecordatorio) {
        for (int dia : dias) {
            Reminder reminder = new Reminder(idRecordatorio);
            DiaRecordatorioRequest request = new DiaRecordatorioRequest(reminder, dia);
            diaRecordatorioService.guardarDiaRecordatorio(request).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (!response.isSuccessful()) {
                        Log.e("DíaRecordatorio", "Error al guardar día " + dia);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Log.e("DíaRecordatorio", "Error de red: " + t.getMessage());
                }
            });
        }
    }
}
