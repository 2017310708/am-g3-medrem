package com.grupo3.medrem.repositories;

import com.grupo3.medrem.BuildConfig;
import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.api.services.ApiServiceFactory;
import com.grupo3.medrem.api.services.ReminderService;
import com.grupo3.medrem.data.dto.request.NewReminderRequest;
import com.grupo3.medrem.data.dto.response.ReminderResponse;
import com.grupo3.medrem.data.dto.response.ReminderDetailResponse;
import com.grupo3.medrem.data.mappers.ReminderMapper;
import com.grupo3.medrem.models.Reminder;

import java.util.List;
import java.util.Map;

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
    public interface ReminderIdCallback {
        void onSuccess(int idRecordatorio);
        void onError(String message);
    }
    public interface LoadRemindersCallback {
        void onSuccess(List<ReminderDetailResponse> reminders);
        void onError(String message);
    }

    public ReminderRepository() {
        reminderService = ApiServiceFactory.createReminderService(BASE_URL);
    }

    public void newReminder(NewReminderRequest request, final ReminderIdCallback callback) {
        reminderService.register(request).enqueue(new Callback<ApiResponse<Map<String, Integer>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Integer>>> call, Response<ApiResponse<Map<String, Integer>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Map<String, Integer>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null && apiResponse.getData().containsKey("idRecordatorio")) {
                        int id = apiResponse.getData().get("idRecordatorio");
                        callback.onSuccess(id);
                    } else {
                        callback.onError("ID de recordatorio no recibido");
                    }
                } else {
                    callback.onError("Error en la conexión con el servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Integer>>> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    public void getRemindersByUser(int idUsuario, final LoadRemindersCallback callback) {
        reminderService.getRemindersByUser(idUsuario).enqueue(new Callback<ApiResponse<List<ReminderDetailResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReminderDetailResponse>>> call, Response<ApiResponse<List<ReminderDetailResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ReminderDetailResponse>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage() != null ?
                                apiResponse.getMessage() : "Error al obtener recordatorios");
                    }
                } else {
                    callback.onError("Error en la conexión con el servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReminderDetailResponse>>> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}
