package com.grupo3.medrem.api;

import com.grupo3.medrem.models.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("api/v1/usuarios/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);
}