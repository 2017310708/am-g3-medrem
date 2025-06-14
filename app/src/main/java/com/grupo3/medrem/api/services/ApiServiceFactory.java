package com.grupo3.medrem.api.services;

import com.grupo3.medrem.api.client.ApiClient;

public class ApiServiceFactory {
    
    public static UserService createUserService(String baseUrl) {
        return ApiClient.getClient(baseUrl).create(UserService.class);
    }
} 