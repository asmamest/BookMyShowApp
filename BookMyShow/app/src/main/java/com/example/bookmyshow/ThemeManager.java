package com.example.bookmyshow;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    /**
     * Set the app theme based on user preference
     * @param isDarkMode true for dark mode, false for light mode
     */
    public static void setTheme(boolean isDarkMode) {
        int mode = isDarkMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO;

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * Initialize theme from saved preferences
     * @param context Application context
     */
    public static void initTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("BookMyShowPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("darkMode", isSystemInDarkMode(context));
        setTheme(isDarkMode);
    }

    /**
     * Check if system is in dark mode
     * @param context Application context
     * @return true if system is in dark mode
     */
    public static boolean isSystemInDarkMode(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Apply theme to activity
     * @param activity Activity to apply theme to
     */
    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("BookMyShowPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("darkMode", isSystemInDarkMode(activity));

        if (isDarkMode) {
            activity.setTheme(R.style.Theme_BookMyShow_Dark);
        } else {
            activity.setTheme(R.style.Theme_BookMyShow_Light);
        }
    }
}
