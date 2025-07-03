package com.grupo3.medrem.api.services;

import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.data.dto.request.DiaRecordatorioRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DiaRecordatorioService {
    @POST("/api/v1/dias-recordatorio")
    Call<ApiResponse<Void>> guardarDiaRecordatorio(@Body DiaRecordatorioRequest request);
}
