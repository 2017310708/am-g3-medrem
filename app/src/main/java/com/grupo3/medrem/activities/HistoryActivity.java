package com.grupo3.medrem.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
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
    private TextView tabTaken, tabMissed;
    private int currentFilter = -1;

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
        adapter = new ReminderAdapter(historyList, null);
        recyclerHistory.setAdapter(adapter);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        tabTaken = findViewById(R.id.tabTaken);
        tabMissed = findViewById(R.id.tabMissed);

        tabTaken.setOnClickListener(v -> {
            currentFilter = ReminderAdapter.ESTADO_TOMADO;
            updateFilterUI();
            loadRemindersForUser();
        });

        tabMissed.setOnClickListener(v -> {
            currentFilter = ReminderAdapter.ESTADO_PERDIDO;
            updateFilterUI();
            loadRemindersForUser();
        });

        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
        observeReminderData();

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

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
            int idRecordatorio = reminder.getIdRecordatorio();
            int estadoGuardado = obtenerEstadoGuardado(idRecordatorio);

            if (estadoGuardado != ReminderAdapter.ESTADO_TOMADO && estadoGuardado != ReminderAdapter.ESTADO_PERDIDO) {
                continue;
            }

            if (currentFilter == ReminderAdapter.ESTADO_TOMADO && estadoGuardado != ReminderAdapter.ESTADO_TOMADO) {
                continue;
            } else if (currentFilter == ReminderAdapter.ESTADO_PERDIDO && estadoGuardado != ReminderAdapter.ESTADO_PERDIDO) {
                continue;
            }

            String medicamento = reminder.getMedicamento().getNombre();
            String dosis = reminder.getMedicamento().getDosis_cantidad() + " " +
                    reminder.getMedicamento().getUnidadDosis().getNombre();
            String fechaCompleta = reminder.getFechaInicio().substring(0, 10) + " " + reminder.getHora().substring(0, 5);



            historyList.add(new ReminderAdapter.ReminderItem(
                    reminder.getIdRecordatorio(),
                    medicamento,
                    dosis,
                    fechaCompleta,
                    estadoGuardado,
                    false
            ));
        }
        adapter.notifyDataSetChanged();

    }
    private int obtenerEstadoGuardado(int idRecordatorio) {
        SharedPreferences prefs = getSharedPreferences("recordatorio_estados", MODE_PRIVATE);
        return prefs.getInt("estado_" + idRecordatorio, ReminderAdapter.ESTADO_PENDIENTE);
    }
    private void updateFilterUI() {
        int selectedBackground = getResources().getColor(R.color.background_secondary);
        int unselectedBackground = getResources().getColor(R.color.background_primary);

        tabTaken.setBackgroundColor(currentFilter == ReminderAdapter.ESTADO_TOMADO ? selectedBackground : unselectedBackground);
        tabMissed.setBackgroundColor(currentFilter == ReminderAdapter.ESTADO_PERDIDO ? selectedBackground : unselectedBackground);
    }
}