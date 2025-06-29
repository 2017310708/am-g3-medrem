package com.grupo3.medrem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo3.medrem.adapters.ReminderAdapter;

import java.util.ArrayList;
import java.util.List;

import com.grupo3.medrem.R;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.utils.LanguageHelper;

public class DashboardActivity extends AppCompatActivity implements ReminderAdapter.OnReminderActionListener {

    private RecyclerView todayRemindersList;
    private RecyclerView futureRemindersList;
    private ReminderAdapter todayAdapter;
    private ReminderAdapter futureAdapter;
    private List<ReminderAdapter.ReminderItem> todayReminders;
    private List<ReminderAdapter.ReminderItem> futureReminders;

    private LinearLayout homeButton;
    private LinearLayout termsButton;
    private LinearLayout settingsButton;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

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
            Intent intent = new Intent(this, NewReminderActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String savedLanguage = preferenceManager.getLanguage();
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();

        if (!currentLanguage.equals(savedLanguage)) {
            LanguageHelper.setAppLanguage(this, savedLanguage);
            recreate();
            return;
        }
    }

    private void setupBottomNavigation() {
        homeButton = findViewById(R.id.homeButton);
        termsButton = findViewById(R.id.termsButton);
        settingsButton = findViewById(R.id.settingsButton);

        homeButton.setOnClickListener(v -> {
            // No hacer nada ya que estamos en el Dashboard
        });

        termsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TermsActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 1001);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            String savedLanguage = preferenceManager.getLanguage();
            LanguageHelper.setAppLanguage(this, savedLanguage);
            recreate();
        }
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