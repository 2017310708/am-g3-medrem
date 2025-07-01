package com.grupo3.medrem.api.services;

import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.models.Medicamento;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MedicamentoService {
    @GET("/api/v1/medicamentos")
    Call<ApiResponse<List<Medicamento>>> listarMedicamentos();
}
