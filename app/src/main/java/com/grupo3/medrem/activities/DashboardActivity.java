package com.grupo3.medrem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grupo3.medrem.adapters.ReminderAdapter;
import com.grupo3.medrem.data.dto.response.ReminderDetailResponse;
import com.grupo3.medrem.models.User;
import com.grupo3.medrem.viewmodels.ReminderViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private LinearLayout historyButton;
    private LinearLayout termsButton;
    private LinearLayout settingsButton;
    private PreferenceManager preferenceManager;
    private ReminderViewModel reminderViewModel;
    private SimpleDateFormat dateFormatter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Rate limit para no DOSear a la API
    private long lastRefreshTime = 0;
    private static final long REFRESH_RATE_LIMIT_MS = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

        todayRemindersList = findViewById(R.id.todayRemindersList);
        futureRemindersList = findViewById(R.id.futureRemindersList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        todayRemindersList.setLayoutManager(new LinearLayoutManager(this));
        futureRemindersList.setLayoutManager(new LinearLayoutManager(this));

        todayReminders = new ArrayList<>();
        futureReminders = new ArrayList<>();

        setupSwipeRefresh();
        setupObservers();
        loadUserReminders();

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
        historyButton = findViewById(R.id.historyButton);
        termsButton = findViewById(R.id.termsButton);
        settingsButton = findViewById(R.id.settingsButton);

        homeButton.setOnClickListener(v -> {
            // No hacer nada ya que estamos en el Dashboard
        });

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
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

    private void setupObservers() {
        reminderViewModel.getRemindersListState().observe(this, state -> {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (state != null) {
                Log.d("DashboardActivity", "Observer triggered - Success: " + state.isSuccess());
                if (state.isSuccess() && state.getReminders() != null) {
                    Log.d("DashboardActivity", "Processing " + state.getReminders().size() + " reminders");
                    processReminders(state.getReminders());
                } else if (!state.isSuccess()) {
                    Log.e("DashboardActivity", "Error loading reminders: " + state.getMessage());
                    loadSampleData();
                }
            } else {
                Log.d("DashboardActivity", "Observer state is null");
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastRefreshTime < REFRESH_RATE_LIMIT_MS) {
                long remainingTime = (REFRESH_RATE_LIMIT_MS - (currentTime - lastRefreshTime)) / 1000;
                Log.d("DashboardActivity", "Rate limit active. Wait " + remainingTime + " seconds");

                String message = getString(R.string.dashboard_refresh_rate_limit, remainingTime);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            lastRefreshTime = currentTime;
            Log.d("DashboardActivity", "Pull to refresh triggered");

            loadUserReminders();
        });

        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );
    }

    private void loadUserReminders() {
        User currentUser = preferenceManager.getUser();
        Log.d("DashboardActivity", "Current user: " + (currentUser != null ? currentUser.getIdUsuario() : "null"));

        if (currentUser != null) {
            Log.d("DashboardActivity", "Loading reminders for user ID: " + currentUser.getIdUsuario());
            reminderViewModel.loadRemindersByUser(currentUser.getIdUsuario());
        } else {
            Log.d("DashboardActivity", "No user found, loading sample data");
            loadSampleData();
        }
    }

    private void processReminders(List<ReminderDetailResponse> reminders) {
        Log.d("DashboardActivity", "Processing " + reminders.size() + " reminders");
        todayReminders.clear();
        futureReminders.clear();

        Calendar today = Calendar.getInstance();
        String todayStr = dateFormatter.format(today.getTime());
        Log.d("DashboardActivity", "Today date: " + todayStr);

        for (ReminderDetailResponse reminder : reminders) {
            Log.d("DashboardActivity", "Processing reminder: " + reminder.getIdRecordatorio() +
                  ", start: " + reminder.getFechaInicio() + ", end: " + reminder.getFechaFin());

            if (isReminderForToday(reminder, todayStr)) {
                Log.d("DashboardActivity", "Adding to today reminders");
                todayReminders.add(createReminderItem(reminder, true));
            } else if (isReminderForFuture(reminder, todayStr)) {
                Log.d("DashboardActivity", "Adding to future reminders");
                futureReminders.add(createReminderItem(reminder, false));
            } else {
                Log.d("DashboardActivity", "Reminder not added to any category");
            }
        }

        Log.d("DashboardActivity", "Today reminders: " + todayReminders.size() +
              ", Future reminders: " + futureReminders.size());

        if (todayAdapter != null) {
            todayAdapter.notifyDataSetChanged();
        }
        if (futureAdapter != null) {
            futureAdapter.notifyDataSetChanged();
        }
    }

    private boolean isReminderForToday(ReminderDetailResponse reminder, String todayStr) {
        try {
            String startDateStr = reminder.getFechaInicio().substring(0, 10);
            String endDateStr = reminder.getFechaFin().substring(0, 10);

            Date startDate = dateFormatter.parse(startDateStr);
            Date endDate = dateFormatter.parse(endDateStr);
            Date today = dateFormatter.parse(todayStr);

            Log.d("DashboardActivity", "Checking reminder " + reminder.getIdRecordatorio() +
                  ": start=" + startDateStr + ", end=" + endDateStr + ", today=" + todayStr);

            boolean inDateRange = (today.equals(startDate) || today.after(startDate)) &&
                                 (today.equals(endDate) || today.before(endDate));
            boolean activeToday = isReminderActiveToday(reminder);

            Log.d("DashboardActivity", "Date range check: " + inDateRange + ", Active today: " + activeToday);

            return inDateRange && activeToday;
        } catch (ParseException e) {
            Log.e("DashboardActivity", "Error parsing dates", e);
            return false;
        }
    }

    private boolean isReminderForFuture(ReminderDetailResponse reminder, String todayStr) {
        try {
            String startDateStr = reminder.getFechaInicio().substring(0, 10);
            Date startDate = dateFormatter.parse(startDateStr);
            Date today = dateFormatter.parse(todayStr);

            return startDate.after(today);
        } catch (ParseException e) {
            Log.e("DashboardActivity", "Error parsing future date", e);
            return false;
        }
    }

    private boolean isReminderActiveToday(ReminderDetailResponse reminder) {
        String frecuenciaNombre = reminder.getFrecuencia().getNombre();
        Log.d("DashboardActivity", "Checking frequency: " + frecuenciaNombre);

        if (frecuenciaNombre.equals("Todos los Días")) {
            Log.d("DashboardActivity", "Frequency is 'Todos los Días' - returning true");
            return true;
        } else if (frecuenciaNombre.equals("Días Específicos")) {
            Calendar today = Calendar.getInstance();
            int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
            int ourDayFormat = (dayOfWeek == 1) ? 7 : dayOfWeek - 1;

            Log.d("DashboardActivity", "Today is day: " + ourDayFormat);

            if (reminder.getDiasRecordatorio() != null) {
                Log.d("DashboardActivity", "Checking " + reminder.getDiasRecordatorio().size() + " specific days");
                for (ReminderDetailResponse.DiaRecordatorioResponse dia : reminder.getDiasRecordatorio()) {
                    Log.d("DashboardActivity", "Checking day: " + dia.getDia());
                    if (dia.getDia() == ourDayFormat) {
                        return true;
                    }
                }
            } else {
                Log.d("DashboardActivity", "No specific days found");
            }
        }
        Log.d("DashboardActivity", "Reminder not active today");
        return false;
    }

    private ReminderAdapter.ReminderItem createReminderItem(ReminderDetailResponse reminder, boolean isToday) {
        String medicamentoNombre = reminder.getMedicamento().getNombre();
        String dosis = reminder.getMedicamento().getDosis_cantidad() + " " +
                      reminder.getMedicamento().getUnidadDosis().getNombre();

        String tiempoTexto;
        if (isToday) {
            tiempoTexto = "Hoy, " + reminder.getHora().substring(0, 5);
        } else {
            String fechaInicio = reminder.getFechaInicio().substring(0, 10);
            tiempoTexto = fechaInicio + ", " + reminder.getHora().substring(0, 5);
        }

        return new ReminderAdapter.ReminderItem(
                medicamentoNombre,
                dosis,
                tiempoTexto,
                ReminderAdapter.ESTADO_PENDIENTE
        );
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

        if (todayAdapter != null) {
            todayAdapter.notifyDataSetChanged();
        }
        if (futureAdapter != null) {
            futureAdapter.notifyDataSetChanged();
        }
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