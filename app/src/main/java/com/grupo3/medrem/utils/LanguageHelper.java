package com.grupo3.medrem.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LanguageHelper {
    
    public static final String LANGUAGE_SPANISH = "es";
    public static final String LANGUAGE_ENGLISH = "en";
    
    public static Context applyLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        
        return context.createConfigurationContext(configuration);
    }
    
    public static void setAppLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            configuration.setLocales(new android.os.LocaleList(locale));
        } else {
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration);
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
    
    public static String getCurrentLanguage(Context context) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        return preferenceManager.getLanguage();
    }
    
    public static void changeLanguage(Context context, String languageCode) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setLanguage(languageCode);
        setAppLanguage(context, languageCode);
    }
    
    public static int getLanguageIndex(String languageCode) {
        switch (languageCode) {
            case LANGUAGE_SPANISH:
                return 0;
            case LANGUAGE_ENGLISH:
                return 1;
            default:
                return 0; // Español por defecto
        }
    }
    
    public static String getLanguageCode(int index) {
        switch (index) {
            case 0:
                return LANGUAGE_SPANISH;
            case 1:
                return LANGUAGE_ENGLISH;
            default:
                return LANGUAGE_SPANISH; // Español por defecto
        }
    }
    
    public static void restartActivity(Activity activity) {
        activity.recreate();
    }
}
