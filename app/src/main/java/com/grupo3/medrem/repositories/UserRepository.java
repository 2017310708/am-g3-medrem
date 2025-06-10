package com.grupo3.medrem.repositories;

import com.grupo3.medrem.BuildConfig;
import com.grupo3.medrem.api.ApiClient;
import com.grupo3.medrem.api.ApiResponse;
import com.grupo3.medrem.models.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String BASE_URL = BuildConfig.BASE_URL;

    public interface LoginCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void login(String correo, String password, final LoginCallback callback) {
        LoginRequest loginRequest = new LoginRequest(correo, password);

        ApiClient.getUserService(BASE_URL).login(loginRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getMessage());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else if (response.code() == 401) {
                    callback.onError("Credenciales incorrectas");
                } else {
                    callback.onError("Error en la conexión con el servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}