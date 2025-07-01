package com.grupo3.medrem.repositories;

import com.grupo3.medrem.BuildConfig;
import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.api.services.ApiServiceFactory;
import com.grupo3.medrem.api.services.ReminderService;
import com.grupo3.medrem.data.dto.request.NewReminderRequest;
import com.grupo3.medrem.data.dto.response.ReminderResponse;
import com.grupo3.medrem.data.mappers.ReminderMapper;
import com.grupo3.medrem.models.Reminder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderRepository {

    private static final String BASE_URL = BuildConfig.BASE_URL;
    private final ReminderService reminderService;
    public interface AuthCallback {
        void onSuccess(Reminder reminder);
        void onError(String message);
    }

    public ReminderRepository() {
        reminderService = ApiServiceFactory.createReminderService(BASE_URL);
    }

    public void newReminder(NewReminderRequest request, final AuthCallback callback) {
        reminderService.register(request).enqueue(new Callback<ApiResponse<ReminderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReminderResponse>> call, Response<ApiResponse<ReminderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ReminderResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Reminder reminder = ReminderMapper.fromReminderResponse(apiResponse.getData());
                        callback.onSuccess(reminder);
                    } else {
                        callback.onError(apiResponse.getMessage() != null ?
                                apiResponse.getMessage() : "Error en el registro");
                    }
                } else {
                    callback.onError("Error en la conexión con el servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReminderResponse>> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}
