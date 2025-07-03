package com.grupo3.medrem.data.mappers;

import com.grupo3.medrem.data.dto.response.ReminderResponse;
import com.grupo3.medrem.models.Reminder;

public class ReminderMapper {

    public static Reminder fromReminderResponse(ReminderResponse reminderResponse) {
        if (reminderResponse == null) {
            return null;
        }

        Reminder reminder = new Reminder();
        reminder.setIdRecordatorio(reminderResponse.getIdRecordatorio());
        reminder.setIdUsuario(reminderResponse.getIdUsuario());
        reminder.setIdMedicamento(reminderResponse.getIdMedicamento());
        reminder.setIdFrecuencia(reminderResponse.getIdFrecuencia());
        reminder.setFechaInicio(reminderResponse.getFechaInicio());
        reminder.setFechaFin(reminderResponse.getFechaFin());
        reminder.setHora(reminderResponse.getHora());
        reminder.setNotas(reminderResponse.getNotas());

        return reminder;
    }
}