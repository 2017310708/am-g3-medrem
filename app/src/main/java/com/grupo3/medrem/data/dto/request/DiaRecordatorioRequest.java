package com.grupo3.medrem.data.dto.request;

import com.grupo3.medrem.models.Reminder;

public class DiaRecordatorioRequest {
    private Reminder recordatorio;
    private int dia;

    public DiaRecordatorioRequest(Reminder recordatorio, int dia) {
        this.recordatorio = recordatorio;
        this.dia = dia;
    }
}
