package com.grupo3.medrem.api.services;

import com.grupo3.medrem.api.client.ApiClient;

public class ApiServiceFactory {
    
    public static UserService createUserService(String baseUrl) {
        return ApiClient.getClient(baseUrl).create(UserService.class);
    }

    public static ReminderService createReminderService(String baseUrl) {
        return ApiClient.getClient(baseUrl).create(ReminderService.class);

    }

    public static MedicamentoService createMedicamentoService(String baseUrl) {
        return ApiClient.getClient(baseUrl).create(MedicamentoService.class);
    }

    public static FrecuenciaService createFrecuenciaService(String baseUrl) {
        return ApiClient.getClient(baseUrl).create(FrecuenciaService.class);
    }

    public static DiaRecordatorioService createDiaRecordatorioService(String baseUrl) {
        return ApiClient.getClient(baseUrl).create(DiaRecordatorioService.class);
    }
} 