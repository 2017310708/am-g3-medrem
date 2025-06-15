package com.grupo3.medrem.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo3.medrem.adapters.ReminderAdapter;

import java.util.ArrayList;
import java.util.List;

import com.grupo3.medrem.R;

public class DashboardActivity extends AppCompatActivity implements ReminderAdapter.OnReminderActionListener {

    private RecyclerView todayRemindersList;
    private RecyclerView futureRemindersList;
    private ReminderAdapter todayAdapter;
    private ReminderAdapter futureAdapter;
    private List<ReminderAdapter.ReminderItem> todayReminders;
    private List<ReminderAdapter.ReminderItem> futureReminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        todayRemindersList = findViewById(R.id.todayRemindersList);
        futureRemindersList = findViewById(R.id.futureRemindersList);

        todayRemindersList.setLayoutManager(new LinearLayoutManager(this));
        futureRemindersList.setLayoutManager(new LinearLayoutManager(this));

        todayReminders = new ArrayList<>();
        futureReminders = new ArrayList<>();

        loadSampleData();

        todayAdapter = new ReminderAdapter(todayReminders, this);
        futureAdapter = new ReminderAdapter(futureReminders, this);

        todayRemindersList.setAdapter(todayAdapter);
        futureRemindersList.setAdapter(futureAdapter);

        FloatingActionButton addReminderFab = findViewById(R.id.addReminderFab);
        addReminderFab.setOnClickListener(v -> {
            // TO-DO
        });
    }

    private void loadSampleData() {
        todayReminders.add(new ReminderAdapter.ReminderItem(
                "Paracetamol",
                "1 pastilla",
                "Hoy, 08:00",
                ReminderAdapter.ESTADO_PENDIENTE));

        todayReminders.add(new ReminderAdapter.ReminderItem(
                "Ibuprofeno",
                "1 pastilla",
                "Hoy, 14:00",
                ReminderAdapter.ESTADO_TOMADO));

        todayReminders.add(new ReminderAdapter.ReminderItem(
                "Vitamina C",
                "1 cápsula",
                "Ayer, 21:00",
                ReminderAdapter.ESTADO_PERDIDO));

        futureReminders.add(new ReminderAdapter.ReminderItem(
                "Amoxicilina",
                "1 cápsula",
                "Mañana, 09:00",
                ReminderAdapter.ESTADO_PENDIENTE));
    }

    @Override
    public void onConfirmReminder(int position) {
        ReminderAdapter.ReminderItem reminder = todayReminders.get(position);
        todayReminders.set(position, new ReminderAdapter.ReminderItem(
                reminder.getNombre_medicamento(),
                reminder.getDosis(),
                reminder.getTexto_tiempo(),
                ReminderAdapter.ESTADO_TOMADO));
        todayAdapter.notifyItemChanged(position);
    }

    @Override
    public void onCancelReminder(int position) {
        ReminderAdapter.ReminderItem reminder = todayReminders.get(position);
        todayReminders.set(position, new ReminderAdapter.ReminderItem(
                reminder.getNombre_medicamento(),
                reminder.getDosis(),
                reminder.getTexto_tiempo(),
                ReminderAdapter.ESTADO_PERDIDO));
        todayAdapter.notifyItemChanged(position);
    }
}