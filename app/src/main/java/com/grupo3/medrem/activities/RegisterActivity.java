package com.grupo3.medrem.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.grupo3.medrem.R;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.viewmodels.RegisterViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private EditText nombreEditText;
    private EditText apellidoPaternoEditText;
    private EditText apellidoMaternoEditText;
    private EditText correoEditText;
    private EditText passwordEditText;
    private EditText telefonoEditText;
    private EditText fechaNacimientoEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private CheckBox termsCheckBox;
    private PreferenceManager preferenceManager;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        preferenceManager = new PreferenceManager(this);
        
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        nombreEditText = findViewById(R.id.register_name);
        apellidoPaternoEditText = findViewById(R.id.register_apellido_paterno);
        apellidoMaternoEditText = findViewById(R.id.register_apellido_materno);
        correoEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        telefonoEditText = findViewById(R.id.register_telefono);
        fechaNacimientoEditText = findViewById(R.id.register_fecha_nacimiento);
        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.register_progress);
        termsCheckBox = findViewById(R.id.register_acept);

        setupDatePicker();

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        registerViewModel.getRegisterState().observe(this, registerState -> {
            if (registerState.isSuccess()) {
                preferenceManager.saveUser(registerState.getUser());
                preferenceManager.saveToken(registerState.getUser().getToken());
                preferenceManager.setLoggedIn(true);
                String welcomeMessage = "Bienvenido, " + registerState.getUser().getNombreCompleto();
                Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show();
                // Redirigir al login/dashboard
                navigateToDashboard();
                // navigateToLogin();
                finish();
            } else if (registerState.getMessage() != null) {
                Toast.makeText(this, registerState.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        registerViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            registerButton.setEnabled(!isLoading);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupDatePicker() {
        fechaNacimientoEditText.setFocusable(false);
        fechaNacimientoEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        fechaNacimientoEditText.setText(dateFormatter.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            
            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.YEAR, -150);
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            
            datePickerDialog.show();
        });
    }

    public void onRegisterClick(View view) {
        if (!termsCheckBox.isChecked()) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones para registrarse", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String nombre = nombreEditText.getText().toString().trim();
        String apellidoPaterno = apellidoPaternoEditText.getText().toString().trim();
        String apellidoMaterno = apellidoMaternoEditText.getText().toString().trim();
        String correo = correoEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String telefono = telefonoEditText.getText().toString().trim();
        String fechaNacimiento = fechaNacimientoEditText.getText().toString().trim();

        registerViewModel.register(nombre, apellidoPaterno, apellidoMaterno, correo, password, telefono, fechaNacimiento);
    }

    public void onBackClick(View view) {
        onBackPressed();
    }
    
    public void onclickLogin(View view) {
        finish();
    }
    
    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}