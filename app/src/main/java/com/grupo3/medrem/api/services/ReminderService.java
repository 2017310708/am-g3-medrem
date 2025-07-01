package com.grupo3.medrem.api.services;

import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.data.dto.request.NewReminderRequest;
import com.grupo3.medrem.data.dto.response.ReminderResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReminderService {
    @POST("/api/v1/recordatorios")
    Call<ApiResponse<ReminderResponse>> register(@Body NewReminderRequest newReminderRequest);
}
