package com.grupo3.medrem.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.grupo3.medrem.R;
import com.grupo3.medrem.utils.LanguageHelper;
import com.grupo3.medrem.utils.PreferenceManager;

public class HistoryActivity extends AppCompatActivity {
    private ImageView backButton;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

        setContentView(R.layout.activity_history);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);}

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }
}