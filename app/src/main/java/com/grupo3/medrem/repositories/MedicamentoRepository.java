package com.grupo3.medrem.repositories;

import com.grupo3.medrem.BuildConfig;
import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.api.services.ApiServiceFactory;
import com.grupo3.medrem.api.services.MedicamentoService;
import com.grupo3.medrem.models.Medicamento;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicamentoRepository {
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private final MedicamentoService medicamentoService;

    public interface LoadCallback {
        void onSuccess(List<Medicamento> medicamentos);
        void onError(String message);
    }

    public MedicamentoRepository() {
        medicamentoService = ApiServiceFactory.createMedicamentoService(BASE_URL);
    }

    public void listarMedicamentos(final LoadCallback callback) {
        medicamentoService.listarMedicamentos().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Medicamento>>> call, Response<ApiResponse<List<Medicamento>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Medicamento>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Medicamento> medicamentos = apiResponse.getData();

                        Collections.sort(medicamentos, (m1, m2) -> m1.getNombre().compareToIgnoreCase(m2.getNombre()));
                        callback.onSuccess(medicamentos);
                    } else {
                        callback.onError(apiResponse.getMessage() != null ?
                                apiResponse.getMessage() : "Error al obtener medicamentos");
                    }
                } else {
                    callback.onError("Error al conectar con el servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Medicamento>>> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}
