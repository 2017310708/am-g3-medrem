package com.grupo3.medrem.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.material.snackbar.Snackbar;
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
                if (state.isSuccess() && state.getReminders() != null) {
                    processReminders(state.getReminders());
                } else if (!state.isSuccess()) {
                    Log.e("DashboardActivity", "Error loading reminders: " + state.getMessage());
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastRefreshTime < REFRESH_RATE_LIMIT_MS) {
                long remainingTime = (REFRESH_RATE_LIMIT_MS - (currentTime - lastRefreshTime)) / 1000;
                Toast.makeText(this, getString(R.string.dashboard_refresh_rate_limit, remainingTime), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            lastRefreshTime = currentTime;
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
        if (currentUser != null) {
            reminderViewModel.loadRemindersByUser(currentUser.getIdUsuario());
        }
    }

    private void processReminders(List<ReminderDetailResponse> reminders) {
        todayReminders.clear();
        futureReminders.clear();

        Calendar now = Calendar.getInstance();
        String todayStr = dateFormatter.format(now.getTime());

        for (ReminderDetailResponse reminder : reminders) {
            int idRecordatorio = reminder.getIdRecordatorio();
            int estadoGuardadoHoy = obtenerEstadoGuardado(idRecordatorio, todayStr);

            boolean inDateRangeToday = isReminderForToday(reminder, todayStr);
            boolean activeToday = isReminderActiveToday(reminder);

            boolean agregadoAHoy = false;

            if (inDateRangeToday && activeToday) {
                if (estadoGuardadoHoy == ReminderAdapter.ESTADO_TOMADO || estadoGuardadoHoy == ReminderAdapter.ESTADO_PERDIDO) {
                } else if (isTimeRemainingToday(reminder, now)) {
                    todayReminders.add(createReminderItem(reminder, true, todayStr));
                    agregadoAHoy = true;
                } else {
                    ReminderAdapter.ReminderItem item = createReminderItem(reminder, true, todayStr);
                    item.marcarComoPerdido(this, todayStr);
                }
            }

            if (!agregadoAHoy) {
                if (isReminderForFuture(reminder, todayStr) && isReminderActiveOnFutureDay(reminder)) {
                    String startDate = reminder.getFechaInicio().substring(0, 10);
                    futureReminders.add(createReminderItem(reminder, false, startDate));
                } else if (hasFutureDates(reminder, todayStr)) {
                    String nextDate = getNextValidDate(reminder, todayStr);
                    futureReminders.add(createReminderItem(reminder, false, nextDate));
                }
            }
        }

        sortRemindersByDateTime(todayReminders);
        sortRemindersByDateTime(futureReminders);

        todayAdapter.notifyDataSetChanged();
        futureAdapter.notifyDataSetChanged();
    }

    private void sortRemindersByDateTime(List<ReminderAdapter.ReminderItem> reminders) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault());
        reminders.sort((r1, r2) -> {
            try {
                Date d1 = dateTimeFormat.parse(r1.getTexto_tiempo().replace("Hoy", dateFormatter.format(new Date())));
                Date d2 = dateTimeFormat.parse(r2.getTexto_tiempo().replace("Hoy", dateFormatter.format(new Date())));
                return d1.compareTo(d2);
            } catch (ParseException e) {
                Log.e("DashboardActivity", "Error sorting reminders", e);
                return 0;
            }
        });
    }

    private boolean hasFutureDates(ReminderDetailResponse reminder, String todayStr) {
        try {
            String endDateStr = reminder.getFechaFin().substring(0, 10);
            Date endDate = dateFormatter.parse(endDateStr);
            Date today = dateFormatter.parse(todayStr);
            return endDate.after(today);
        } catch (ParseException e) {
            Log.e("DashboardActivity", "Error parsing end date", e);
            return false;
        }
    }

    private int obtenerEstadoGuardado(int idRecordatorio, String fecha) {
        SharedPreferences prefs = getSharedPreferences("recordatorio_estados", MODE_PRIVATE);
        return prefs.getInt("estado_" + idRecordatorio + "_" + fecha, ReminderAdapter.ESTADO_PENDIENTE);
    }

    private boolean isReminderForToday(ReminderDetailResponse reminder, String todayStr) {
        try {
            String startDateStr = reminder.getFechaInicio().substring(0, 10);
            String endDateStr = reminder.getFechaFin().substring(0, 10);

            Date startDate = dateFormatter.parse(startDateStr);
            Date endDate = dateFormatter.parse(endDateStr);
            Date today = dateFormatter.parse(todayStr);

            boolean inDateRange = (today.equals(startDate) || today.after(startDate)) &&
                    (today.equals(endDate) || today.before(endDate));
            boolean activeToday = isReminderActiveToday(reminder);

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

    private boolean isTimeRemainingToday(ReminderDetailResponse reminder, Calendar now) {
        try {
            String[] parts = reminder.getHora().split(":");
            int reminderHour = Integer.parseInt(parts[0]);
            int reminderMinute = Integer.parseInt(parts[1]);

            Calendar reminderTime = (Calendar) now.clone();
            reminderTime.set(Calendar.HOUR_OF_DAY, reminderHour);
            reminderTime.set(Calendar.MINUTE, reminderMinute);
            reminderTime.set(Calendar.SECOND, 0);
            reminderTime.set(Calendar.MILLISECOND, 0);

            // Tolerancia para que el rec. sea checkeado
            reminderTime.add(Calendar.MINUTE, 15);

            return now.before(reminderTime) || now.equals(reminderTime);
        } catch (Exception e) {
            Log.e("DashboardActivity", "Error comparing times", e);
            return false;
        }
    }

    private boolean isReminderActiveToday(ReminderDetailResponse reminder) {
        String frecuenciaNombre = reminder.getFrecuencia().getNombre();

        if (frecuenciaNombre.equals("Todos los Días")) {
            return true;
        } else if (frecuenciaNombre.equals("Días Específicos")) {
            Calendar today = Calendar.getInstance();
            int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
            int ourDayFormat = (dayOfWeek + 5) % 7; // Mapea Domingo=6, Lunes=0

            if (reminder.getDiasRecordatorio() != null) {
                for (ReminderDetailResponse.DiaRecordatorioResponse dia : reminder.getDiasRecordatorio()) {
                    if (dia.getDia() == ourDayFormat) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isReminderActiveOnFutureDay(ReminderDetailResponse reminder) {
        String frecuenciaNombre = reminder.getFrecuencia().getNombre();

        if (frecuenciaNombre.equals("Todos los Días")) {
            return true;
        } else if (frecuenciaNombre.equals("Días Específicos") && reminder.getDiasRecordatorio() != null) {
            return !reminder.getDiasRecordatorio().isEmpty();
        }
        return false;
    }

    private ReminderAdapter.ReminderItem createReminderItem(ReminderDetailResponse reminder, boolean isToday, String fecha) {
        String medicamentoNombre = reminder.getMedicamento().getNombre();
        String dosis = reminder.getMedicamento().getDosis_cantidad() + " " +
                reminder.getMedicamento().getUnidadDosis().getNombre();

        String tiempoTexto;
        if (isToday) {
            tiempoTexto = "Hoy, " + reminder.getHora().substring(0, 5);
        } else {
            tiempoTexto = fecha + ", " + reminder.getHora().substring(0, 5);
        }

        return new ReminderAdapter.ReminderItem(
                reminder.getIdRecordatorio(),
                medicamentoNombre,
                dosis,
                tiempoTexto,
                ReminderAdapter.ESTADO_PENDIENTE,
                isToday
        );
    }


    private String getNextValidDate(ReminderDetailResponse reminder, String todayStr) {
        try {
            Calendar today = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date todayDate = sdf.parse(todayStr);

            Calendar candidate = Calendar.getInstance();
            candidate.setTime(todayDate);
            candidate.add(Calendar.DATE, 1); // empezar con mañana

            Date endDate = sdf.parse(reminder.getFechaFin().substring(0, 10));
            String frecuenciaNombre = reminder.getFrecuencia().getNombre();

            while (!candidate.getTime().after(endDate)) {
                if (frecuenciaNombre.equals("Todos los Días")) {
                    return dateFormatter.format(candidate.getTime());
                } else if (frecuenciaNombre.equals("Días Específicos")) {
                    int dayOfWeek = candidate.get(Calendar.DAY_OF_WEEK);
                    int ourDayFormat = (dayOfWeek + 5) % 7;
                    if (reminder.getDiasRecordatorio() != null) {
                        for (ReminderDetailResponse.DiaRecordatorioResponse dia : reminder.getDiasRecordatorio()) {
                            if (dia.getDia() == ourDayFormat) {
                                return dateFormatter.format(candidate.getTime());
                            }
                        }
                    }
                }
                candidate.add(Calendar.DATE, 1);
            }

            // Si no encontró ninguno, usa la fechaFin como fallback
            return dateFormatter.format(endDate);
        } catch (Exception e) {
            Log.e("DashboardActivity", "Error calculating next valid date", e);
            return reminder.getFechaFin().substring(0, 10);
        }
    }

    @Override
    public void onConfirmReminder(int position) {
        ReminderAdapter.ReminderItem reminder = todayReminders.get(position);
        String fecha = reminder.extraerFecha();
        reminder.marcarComoTomado(this, fecha);

        ReminderAdapter.ReminderItem backup = reminder;
        todayReminders.remove(position);
        todayAdapter.notifyItemRemoved(position);

        Snackbar.make(todayRemindersList, "✅ Medicamento Tomado!!!", Snackbar.LENGTH_LONG)
                .setAction("Deshacer", v -> {
                    backup.deshacerEstado(this, fecha);
                    todayReminders.add(position, backup);
                    todayAdapter.notifyItemInserted(position);
                }).show();
    }

    @Override
    public void onCancelReminder(int position) {
        ReminderAdapter.ReminderItem reminder = todayReminders.get(position);
        String fecha = reminder.extraerFecha();
        reminder.marcarComoPerdido(this, fecha);

        ReminderAdapter.ReminderItem backup = reminder;
        todayReminders.remove(position);
        todayAdapter.notifyItemRemoved(position);

        Snackbar.make(todayRemindersList, "⚠️ Recordatorio Perdido", Snackbar.LENGTH_LONG)
                .setAction("Deshacer", v -> {
                    backup.deshacerEstado(this, fecha);
                    todayReminders.add(position, backup);
                    todayAdapter.notifyItemInserted(position);
                }).show();
    }
}