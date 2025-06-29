package com.grupo3.medrem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.grupo3.medrem.R;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.utils.LanguageHelper;

public class SettingsActivity extends AppCompatActivity {

    private ImageView backButton;
    private LinearLayout notificationsContainer;
    private LinearLayout darkModeContainer;
    private LinearLayout languageContainer;
    private LinearLayout privacyContainer;
    private LinearLayout profileContainer;
    private LinearLayout logoutContainer;
    private Switch notificationsSwitch;
    private Switch darkModeSwitch;
    private Spinner languageSpinner;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

        setContentView(R.layout.activity_settings);

        initializeViews();
        loadSavedSettings();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        notificationsContainer = findViewById(R.id.notificationsContainer);
        darkModeContainer = findViewById(R.id.darkModeContainer);
        languageContainer = findViewById(R.id.languageContainer);
        privacyContainer = findViewById(R.id.privacyContainer);
        profileContainer = findViewById(R.id.profileContainer);
        logoutContainer = findViewById(R.id.logoutContainer);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        languageSpinner = findViewById(R.id.languageSpinner);
    }

    private void loadSavedSettings() {
        boolean notificationsEnabled = preferenceManager.isNotificationsEnabled();
        boolean darkModeEnabled = preferenceManager.isDarkModeEnabled();
        String currentLanguage = preferenceManager.getLanguage();

        notificationsSwitch.setChecked(notificationsEnabled);
        darkModeSwitch.setChecked(darkModeEnabled);

        int languageIndex = LanguageHelper.getLanguageIndex(currentLanguage);
        languageSpinner.setSelection(languageIndex);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        notificationsContainer.setOnClickListener(v -> {
            notificationsSwitch.setChecked(!notificationsSwitch.isChecked());
            handleNotificationsToggle(notificationsSwitch.isChecked());
        });

        darkModeContainer.setOnClickListener(v -> {
            darkModeSwitch.setChecked(!darkModeSwitch.isChecked());
            handleDarkModeToggle(darkModeSwitch.isChecked());
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguageCode = LanguageHelper.getLanguageCode(position);
                String currentLanguage = preferenceManager.getLanguage();

                if (!selectedLanguageCode.equals(currentLanguage)) {
                    handleLanguageChange(selectedLanguageCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        privacyContainer.setOnClickListener(v -> {
            Toast.makeText(this, "Configuración de privacidad próximamente", Toast.LENGTH_SHORT).show();
        });

        profileContainer.setOnClickListener(v -> {
            Toast.makeText(this, "Configuración de perfil próximamente", Toast.LENGTH_SHORT).show();
        });

        logoutContainer.setOnClickListener(v -> {
            handleLogout();
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleNotificationsToggle(isChecked);
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            handleDarkModeToggle(isChecked);
        });
    }

    private void handleNotificationsToggle(boolean isEnabled) {
        preferenceManager.setNotificationsEnabled(isEnabled);

        if (isEnabled) {
            Toast.makeText(this, getString(R.string.settings_notifications) + " " + getString(R.string.button_confirm).toLowerCase(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.settings_notifications) + " desactivadas", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDarkModeToggle(boolean isEnabled) {
        preferenceManager.setDarkMode(isEnabled);

        if (isEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, getString(R.string.settings_dark_mode) + " activado", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, getString(R.string.settings_dark_mode) + " desactivado", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLanguageChange(String languageCode) {
        LanguageHelper.changeLanguage(this, languageCode);

        String languageName = languageCode.equals("es") ? "Español" : "English";
        Toast.makeText(this, "Idioma cambiado a " + languageName, Toast.LENGTH_SHORT).show();

        LanguageHelper.restartActivity(this);
    }

    private void handleLogout() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
