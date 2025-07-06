package com.grupo3.medrem.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo3.medrem.R;
import com.grupo3.medrem.adapters.ReminderAdapter;
import com.grupo3.medrem.data.dto.response.ReminderDetailResponse;
import com.grupo3.medrem.models.User;
import com.grupo3.medrem.utils.LanguageHelper;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.viewmodels.ReminderViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerHistory;
    private ReminderAdapter adapter;
    private List<ReminderAdapter.ReminderItem> historyList;
    private ReminderViewModel reminderViewModel;
    private PreferenceManager preferenceManager;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        historyList = new ArrayList<>();
        adapter = new ReminderAdapter(historyList, null); // No se necesita listener aquí
        recyclerHistory.setAdapter(adapter);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        observeReminderData();

        loadRemindersForUser();
    }

    private void observeReminderData() {
        reminderViewModel.getRemindersListState().observe(this, state -> {
            if (state != null && state.isSuccess() && state.getReminders() != null) {
                processHistoryReminders(state.getReminders());
            } else if (state != null) {
                Toast.makeText(this, "Error: " + state.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRemindersForUser() {
        User user = preferenceManager.getUser();
        if (user != null) {
            reminderViewModel.loadRemindersByUser(user.getIdUsuario());
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void processHistoryReminders(List<ReminderDetailResponse> reminders) {
        historyList.clear();

        SimpleDateFormat fullDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date now = new Date();

        for (ReminderDetailResponse reminder : reminders) {
            String fechaInicio = reminder.getFechaInicio();
            String hora = reminder.getHora();

            if (fechaInicio == null || hora == null || reminder.getMedicamento() == null) {
                Log.e("HistoryActivity", "Recordatorio inválido: campos nulos");
                continue;
            }

            String medicamento = reminder.getMedicamento().getNombre();
            String dosis = reminder.getMedicamento().getDosis_cantidad() + " " +
                    reminder.getMedicamento().getUnidadDosis().getNombre();

            String fechaCompleta = fechaInicio.substring(0, 10) + " " + hora.substring(0, 5);
            int estado;

            try {
                Date reminderDate = fullDateTimeFormat.parse(fechaCompleta);
                if (reminderDate != null && reminderDate.before(now)) {
                    estado = ReminderAdapter.ESTADO_PERDIDO;
                } else {
                    estado = ReminderAdapter.ESTADO_PENDIENTE;
                }
            } catch (ParseException e) {
                Log.e("HistoryActivity", "Error al parsear la fecha: " + fechaCompleta, e);
                estado = ReminderAdapter.ESTADO_PENDIENTE;
            }

            historyList.add(new ReminderAdapter.ReminderItem(
                    medicamento,
                    dosis,
                    fechaCompleta,
                    estado
            ));
        }
        adapter.notifyDataSetChanged();
    }

}