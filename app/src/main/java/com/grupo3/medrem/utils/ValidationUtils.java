package com.grupo3.medrem.utils;

import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ValidationUtils {

    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 3;
    }

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return true;
        }
        
        String digitsOnly = phone.replaceAll("\\D", "");
        return digitsOnly.length() >= 7 && digitsOnly.length() <= 9;
    }

    public static String formatPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return null;
        }
        
        String digitsOnly = phone.replaceAll("\\D", "");
        
        if (digitsOnly.length() == 7) {
            return "01" + digitsOnly;
        }
        
        return digitsOnly;
    }

    public static boolean isValidBirthDate(String birthDateStr) {
        if (birthDateStr == null || birthDateStr.isEmpty()) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            Date birthDate = sdf.parse(birthDateStr);
            
            // You gotta be alive to use this app, not "in progress"
            if (birthDate.after(new Date())) {
                return false;
            }
            
            // Are you even alive at this point?
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -150);
            Date minDate = calendar.getTime();
            
            return birthDate.after(minDate);
        } catch (ParseException e) {
            return false;
        }
    }
} 