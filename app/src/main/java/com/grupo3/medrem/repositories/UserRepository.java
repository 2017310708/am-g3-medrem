package com.grupo3.medrem.repositories;

import com.grupo3.medrem.BuildConfig;
import com.grupo3.medrem.api.services.ApiServiceFactory;
import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.api.services.UserService;
import com.grupo3.medrem.data.dto.request.LoginRequest;
import com.grupo3.medrem.data.dto.request.RegisterRequest;
import com.grupo3.medrem.data.dto.response.UserResponse;
import com.grupo3.medrem.data.mappers.UserMapper;
import com.grupo3.medrem.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private final UserService userService;

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public UserRepository() {
        userService = ApiServiceFactory.createUserService(BASE_URL);
    }

    public void login(String correo, String password, final AuthCallback callback) {
        LoginRequest loginRequest = new LoginRequest(correo, password);

        userService.login(loginRequest).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User user = UserMapper.fromUserResponse(apiResponse.getData());
                        callback.onSuccess(user);
                    } else {
                        callback.onError(apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "Error en la autenticación");
                    }
                } else if (response.code() == 401) {
                    callback.onError("Credenciales incorrectas");
                } else {
                    callback.onError("Error en la conexión con el servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    public void register(String nombre, String apellidoPaterno, String apellidoMaterno, String correo, String password, String telefono, String fechaNacimiento, final AuthCallback callback) {
        RegisterRequest registerRequest = new RegisterRequest(nombre, apellidoPaterno, apellidoMaterno, correo, password, telefono, fechaNacimiento);

        userService.register(registerRequest).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User user = UserMapper.fromUserResponse(apiResponse.getData());
                        callback.onSuccess(user);
                    } else {
                        callback.onError(apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "Error en el registro");
                    }
                } else if (response.code() == 400) {
                    callback.onError("Datos de registro inválidos");
                } else if (response.code() == 409) {
                    callback.onError("El correo ya está registrado");
                } else {
                    callback.onError("Error en la conexión con el servidor");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }
}