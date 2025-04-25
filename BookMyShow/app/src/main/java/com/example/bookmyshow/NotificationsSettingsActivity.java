package com.example.bookmyshow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

public class NotificationsSettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_settings);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification Settings");

        // Add the preferences fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new NotificationsPreferenceFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Preference Fragment for notifications
    public static class NotificationsPreferenceFragment extends androidx.preference.PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.notifications_preferences, rootKey);

            // Get preferences
            SwitchPreferenceCompat allNotifications = findPreference("pref_all_notifications");
            SwitchPreferenceCompat promotions = findPreference("pref_promotions");
            SwitchPreferenceCompat bookingReminders = findPreference("pref_booking_reminders");
            SwitchPreferenceCompat priceAlerts = findPreference("pref_price_alerts");
            SwitchPreferenceCompat newReleases = findPreference("pref_new_releases");

            // Setup listeners
            if (allNotifications != null) {
                allNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enabled = (Boolean) newValue;

                    // Enable/disable all other notification preferences
                    if (promotions != null) promotions.setEnabled(enabled);
                    if (bookingReminders != null) bookingReminders.setEnabled(enabled);
                    if (priceAlerts != null) priceAlerts.setEnabled(enabled);
                    if (newReleases != null) newReleases.setEnabled(enabled);

                    // Show toast
                    String message = enabled ? "All notifications enabled" : "All notifications disabled";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    return true;
                });
            }

            // Initialize state based on all_notifications preference
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
            boolean allEnabled = prefs.getBoolean("pref_all_notifications", true);

            if (promotions != null) promotions.setEnabled(allEnabled);
            if (bookingReminders != null) bookingReminders.setEnabled(allEnabled);
            if (priceAlerts != null) priceAlerts.setEnabled(allEnabled);
            if (newReleases != null) newReleases.setEnabled(allEnabled);
        }
    }
}