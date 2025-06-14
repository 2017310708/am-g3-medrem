package com.grupo3.medrem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.grupo3.medrem.R;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.viewmodels.SplashViewModel;

public class SplashActivity extends AppCompatActivity {

    private SplashViewModel splashViewModel;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);

        // Pantalla completa
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_splash);

        // Ocultar navbar y status bar - WIP
        WindowInsetsControllerCompat windowInsetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        splashViewModel.isSplashTimeFinished.observe(this, finished -> {
            if (Boolean.TRUE.equals(finished)) {
                navigateToNextScreen();
            }
        });
        
        // Click anywhere to yeet the splash
        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(v -> splashViewModel.skipSplashScreen());
    }
    
    private void navigateToNextScreen() {
        Intent intent;
        
        // Logged? Skip screens
        if (preferenceManager.isLoggedIn() && preferenceManager.isRememberMeEnabled()) {
            intent = new Intent(SplashActivity.this, DashboardActivity.class);
        } else {
            // Not logged in? Suffer.
            if (preferenceManager.isLoggedIn() && !preferenceManager.isRememberMeEnabled()) {
                preferenceManager.clearSession();
            }
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}