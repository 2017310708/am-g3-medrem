package com.grupo3.medrem.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.grupo3.medrem.R;
import com.grupo3.medrem.data.dto.request.NewReminderRequest;
import com.grupo3.medrem.models.Frecuencia;
import com.grupo3.medrem.models.Medicamento;
import com.grupo3.medrem.models.User;
import com.grupo3.medrem.repositories.FrecuenciaRepository;
import com.grupo3.medrem.repositories.MedicamentoRepository;
import com.grupo3.medrem.utils.LanguageHelper;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.viewmodels.ReminderViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewReminderActivity extends AppCompatActivity {

    private ReminderViewModel reminderViewModel;
    private AutoCompleteTextView medicineNameAutoComplete;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private EditText timeEditText;
    private Spinner frequencySpinner;
    private EditText additionalNotesEditText;
    private Button saveButton;
    private Button cancelButton;
    private ImageView backButton;
    private TextView mondayButton, tuesdayButton, wednesdayButton, thursdayButton;
    private TextView fridayButton, saturdayButton, sundayButton;
    private List<TextView> dayButtons;
    private List<Boolean> selectedDays;
    private PreferenceManager preferenceManager;
    private boolean isUpdatingSpinnerProgrammatically = false;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private List<Medicamento> medicamentoList = new ArrayList<>();
    private Medicamento selectedMedicamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

        setContentView(R.layout.activity_new_reminder);

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

        initializeViews();
        loadMedicamentos();
        loadFrecuencias();
        setupDatePicker();
        setupTimePicker();
        setupDayButtons();
        setupClickListeners();
    }

    private void initializeViews() {
        medicineNameAutoComplete = findViewById(R.id.medicineNameAutoComplete);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        frequencySpinner = findViewById(R.id.frequencySpinner);
        additionalNotesEditText = findViewById(R.id.additionalNotesEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        backButton = findViewById(R.id.backButton);

        mondayButton = findViewById(R.id.mondayButton);
        tuesdayButton = findViewById(R.id.tuesdayButton);
        wednesdayButton = findViewById(R.id.wednesdayButton);
        thursdayButton = findViewById(R.id.thursdayButton);
        fridayButton = findViewById(R.id.fridayButton);
        saturdayButton = findViewById(R.id.saturdayButton);
        sundayButton = findViewById(R.id.sundayButton);

        dayButtons = new ArrayList<>();
        dayButtons.add(mondayButton);
        dayButtons.add(tuesdayButton);
        dayButtons.add(wednesdayButton);
        dayButtons.add(thursdayButton);
        dayButtons.add(fridayButton);
        dayButtons.add(saturdayButton);
        dayButtons.add(sundayButton);

        selectedDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            selectedDays.add(false);
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        cancelButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> onSaveClick());
    }

    private void setupDatePicker() {
        startDateEditText.setFocusable(false);
        endDateEditText.setFocusable(false);

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));
    }

    private void showDatePicker(EditText target) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    target.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupTimePicker() {
        timeEditText.setFocusable(false);
        timeEditText.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    NewReminderActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:00", selectedHour, selectedMinute);
                        timeEditText.setText(formattedTime);
                    },
                    hour,
                    minute,
                    true
            );
            timePickerDialog.show();
        });
    }

    private void setupDayButtons() {
        for (int i = 0; i < dayButtons.size(); i++) {
            final int index = i;
            dayButtons.get(i).setOnClickListener(v -> toggleDaySelection(index));
        }
    }

    private void toggleDaySelection(int dayIndex) {
        selectedDays.set(dayIndex, !selectedDays.get(dayIndex));
        updateDayButtonAppearance(dayIndex);

        boolean allDaysSelected = true;
        for (Boolean selected : selectedDays) {
            if (!selected) {
                allDaysSelected = false;
                break;
            }
        }

        if (!allDaysSelected) {
            changeToSpecificDays();
        }
    }

    private void updateDayButtonAppearance(int dayIndex) {
        TextView button = dayButtons.get(dayIndex);
        if (selectedDays.get(dayIndex)) {
            button.setBackgroundResource(R.drawable.day_button_selected);
            button.setTextColor(getResources().getColor(R.color.white));
        } else {
            button.setBackgroundResource(R.drawable.day_button_unselected);
            button.setTextColor(getResources().getColor(R.color.text_color_secondary));
        }
    }

    private void changeToSpecificDays() {
        isUpdatingSpinnerProgrammatically = true;
        for (int i = 0; i < frequencySpinner.getCount(); i++) {
            Frecuencia frecuencia = (Frecuencia) frequencySpinner.getItemAtPosition(i);
            if (frecuencia.getNombre().equals("Días Específicos")) {
                frequencySpinner.setSelection(i);
                break;
            }
        }
        isUpdatingSpinnerProgrammatically = false;
    }

    private void loadFrecuencias() {
        FrecuenciaRepository frecuenciaRepository = new FrecuenciaRepository();
        frecuenciaRepository.listarFrecuencias(new FrecuenciaRepository.LoadCallback() {
            @Override
            public void onSuccess(List<Frecuencia> frecuencias) {
                ArrayAdapter<Frecuencia> adapter = new ArrayAdapter<>(
                        NewReminderActivity.this,
                        android.R.layout.simple_spinner_item,
                        frecuencias
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                frequencySpinner.setAdapter(adapter);
                setupFrequencySpinnerListener();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(NewReminderActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                Log.e("FrecuenciaRepo", message);
            }
        });
    }

    private void setupFrequencySpinnerListener() {
        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUpdatingSpinnerProgrammatically) {
                    Frecuencia selected = (Frecuencia) parent.getItemAtPosition(position);
                    if (selected.getNombre().equals("Todos los Días")) {
                        selectAllDays();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void selectAllDays() {
        for (int i = 0; i < selectedDays.size(); i++) {
            selectedDays.set(i, true);
            updateDayButtonAppearance(i);
        }
    }

    private void loadMedicamentos() {
        MedicamentoRepository medicamentoRepository = new MedicamentoRepository();
        medicamentoRepository.listarMedicamentos(new MedicamentoRepository.LoadCallback() {
            @Override
            public void onSuccess(List<Medicamento> medicamentos) {
                medicamentoList.clear();
                medicamentoList.addAll(medicamentos);

                ArrayAdapter<Medicamento> adapter = new ArrayAdapter<>(
                        NewReminderActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        medicamentoList
                );

                medicineNameAutoComplete.setAdapter(adapter);
                medicineNameAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                    selectedMedicamento = (Medicamento) parent.getItemAtPosition(position);
                });
            }

            @Override
            public void onError(String message) {
                Toast.makeText(NewReminderActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                Log.e("MedicamentoRepo", message);
            }
        });
    }

    private void onSaveClick() {
        User usuarioActual = preferenceManager.getUser();
        if (usuarioActual == null || usuarioActual.getIdUsuario() == 0) {
            Toast.makeText(this, "Usuario no encontrado en sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMedicamento == null) {
            Toast.makeText(this, "Por favor selecciona un medicamento", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaInicio = startDateEditText.getText().toString().trim();
        String fechaFin = endDateEditText.getText().toString().trim();
        if (fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa la fecha de inicio y fin", Toast.LENGTH_SHORT).show();
            return;
        }

        String hora = timeEditText.getText().toString().trim();
        if (hora.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa la hora", Toast.LENGTH_SHORT).show();
            return;
        }

        Frecuencia frecuenciaSeleccionada = (Frecuencia) frequencySpinner.getSelectedItem();
        if (frecuenciaSeleccionada == null) {
            Toast.makeText(this, "Por favor selecciona la frecuencia", Toast.LENGTH_SHORT).show();
            return;
        }

        String notas = additionalNotesEditText.getText().toString().trim();

        NewReminderRequest request = new NewReminderRequest(
                new User(usuarioActual.getIdUsuario()),
                new Medicamento(selectedMedicamento.getIdMedicamento()),
                new Frecuencia(frecuenciaSeleccionada.getIdFrecuencia()),
                fechaInicio, fechaFin, hora, notas
        );

        reminderViewModel.saveReminder(request);
        Toast.makeText(this, "Recordatorio programado exitosamente", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(NewReminderActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}