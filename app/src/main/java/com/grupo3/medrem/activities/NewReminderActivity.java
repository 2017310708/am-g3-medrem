package com.grupo3.medrem.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.grupo3.medrem.R;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.utils.LanguageHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewReminderActivity extends AppCompatActivity {

    private Spinner medicineNameSpinner;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

        setContentView(R.layout.activity_new_reminder);

        initializeViews();
        setupSpinners();
        setupDateTimePickers();
        setupDayButtons();
        setupClickListeners();
    }

    private void initializeViews() {
        medicineNameSpinner = findViewById(R.id.medicineNameSpinner);
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

    private void setupSpinners() {
        String[] medicines = {
            "Ej. Paracetamol",
            "Paracetamol",
            "Ibuprofeno",
            "Aspirina",
            "Amoxicilina",
            "Vitamina C",
            "Vitamina D",
            "Omeprazol"
        };

        ArrayAdapter<String> medicineAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, medicines);
        medicineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicineNameSpinner.setAdapter(medicineAdapter);

        String[] frequencies = {
            "Diariamente",
            "Cada 8 horas",
            "Cada 12 horas",
            "Cada 24 horas",
            "Semanal",
            "Personalizado"
        };

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, frequencies);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(frequencyAdapter);
    }

    private void setupDateTimePickers() {
        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));
        timeEditText.setOnClickListener(v -> showTimePicker());
    }

    private void setupDayButtons() {
        for (int i = 0; i < dayButtons.size(); i++) {
            final int index = i;
            dayButtons.get(i).setOnClickListener(v -> toggleDaySelection(index));
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        cancelButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> saveReminder());
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    editText.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeEditText.setText(time);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void toggleDaySelection(int dayIndex) {
        selectedDays.set(dayIndex, !selectedDays.get(dayIndex));
        TextView button = dayButtons.get(dayIndex);

        if (selectedDays.get(dayIndex)) {
            button.setBackgroundResource(R.drawable.day_button_selected);
            button.setTextColor(getResources().getColor(R.color.white));
        } else {
            button.setBackgroundResource(R.drawable.day_button_unselected);
            button.setTextColor(getResources().getColor(R.color.text_color_secondary));
        }
    }

    private void saveReminder() {
        if (validateForm()) {
            Toast.makeText(this, "Recordatorio guardado exitosamente", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateForm() {
        if (medicineNameSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Por favor selecciona un medicamento", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (startDateEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor selecciona la fecha de inicio", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endDateEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor selecciona la fecha de fin", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (timeEditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor selecciona la hora", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean anyDaySelected = false;
        for (Boolean selected : selectedDays) {
            if (selected) {
                anyDaySelected = true;
                break;
            }
        }

        if (!anyDaySelected) {
            Toast.makeText(this, "Por favor selecciona al menos un día de la semana", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
