package com.grupo3.medrem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.grupo3.medrem.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText correoEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private CheckBox rememberMeCheckBox;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        preferenceManager = new PreferenceManager(this);
        
        if (preferenceManager.isLoggedIn() && preferenceManager.isRememberMeEnabled()) {
            navigateToDashboard();
            finish();
            return;
        } else if (preferenceManager.isLoggedIn() && !preferenceManager.isRememberMeEnabled()) {
            preferenceManager.clearSession();
        }

        correoEditText = findViewById(R.id.login_correo);
        passwordEditText = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.login_progress);
        rememberMeCheckBox = findViewById(R.id.login_recordar);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getLoginState().observe(this, loginState -> {
            if (loginState.isSuccess()) {
                preferenceManager.saveUser(loginState.getUser());
                preferenceManager.saveToken(loginState.getUser().getToken());
                preferenceManager.setLoggedIn(true);
                
                preferenceManager.setRememberMe(rememberMeCheckBox.isChecked());
                
                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
                navigateToDashboard();
                finish();
            } else if (loginState.getMessage() != null) {
                Toast.makeText(this, loginState.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loginViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onLoginClick(View view) {
        String correo = correoEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        loginViewModel.login(correo, password);
    }

    public void onclickRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    
    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}