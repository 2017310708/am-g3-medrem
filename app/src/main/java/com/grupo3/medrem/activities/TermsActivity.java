package com.grupo3.medrem.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.grupo3.medrem.R;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.utils.LanguageHelper;

public class TermsActivity extends AppCompatActivity {

    private ImageView backButton;
    private MaterialButton acceptButton;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

        setContentView(R.layout.activity_terms);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        acceptButton = findViewById(R.id.acceptButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        acceptButton.setOnClickListener(v -> {
            handleAcceptTerms();
        });
    }

    private void handleAcceptTerms() {
        Toast.makeText(this, "Términos y condiciones aceptados", Toast.LENGTH_SHORT).show();
        finish();
    }
}
