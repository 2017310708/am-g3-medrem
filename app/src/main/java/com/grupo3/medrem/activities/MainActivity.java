package com.grupo3.medrem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.grupo3.medrem.R;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.utils.LanguageHelper;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(this);
        String savedLanguage = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, savedLanguage);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickOnboarding2(View view){
        Intent intent = new Intent(this, Onboarding2Activity.class);
        startActivity(intent);
    }

    public void onClickSalir(View view) {
        finish();
    }

}