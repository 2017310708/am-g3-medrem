package com.grupo3.medrem;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.grupo3.medrem.utils.PreferenceManager;
import com.grupo3.medrem.utils.LanguageHelper;

public class MedRemApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceManager preferenceManager = new PreferenceManager(this);

        boolean isDarkMode = preferenceManager.isDarkModeEnabled();

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (!preferenceManager.isLanguageSet()) {
            preferenceManager.setLanguage("es");
        }

        String languageCode = preferenceManager.getLanguage();
        LanguageHelper.setAppLanguage(this, languageCode);
    }
}
