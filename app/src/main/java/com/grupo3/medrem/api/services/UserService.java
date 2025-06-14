package com.grupo3.medrem.api.services;

import com.grupo3.medrem.api.response.ApiResponse;
import com.grupo3.medrem.data.dto.request.LoginRequest;
import com.grupo3.medrem.data.dto.request.RegisterRequest;
import com.grupo3.medrem.data.dto.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("/api/v1/usuarios/login")
    Call<ApiResponse<UserResponse>> login(@Body LoginRequest loginRequest);
    
    @POST("/api/v1/usuarios")
    Call<ApiResponse<UserResponse>> register(@Body RegisterRequest registerRequest);
} 