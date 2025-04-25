// NotificationManager.java
package com.example.bookmyshow.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bookmyshow.models.BackendEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationManager {
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String KEY_NOTIFICATION_COUNT = "notificationCount";
    private static final String KEY_NOTIFICATION_IDS = "notificationIds";
    private static final String KEY_NOTIFICATION_TITLES = "notificationTitles";
    private static final String KEY_NOTIFICATION_SEEN = "notificationSeen";

    private Context context;
    private SharedPreferences prefs;

    public NotificationManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void addNotification(BackendEvent event) {
        // Récupérer les données existantes
        int count = prefs.getInt(KEY_NOTIFICATION_COUNT, 0);
        Set<String> ids = new HashSet<>(prefs.getStringSet(KEY_NOTIFICATION_IDS, new HashSet<>()));
        Set<String> titles = new HashSet<>(prefs.getStringSet(KEY_NOTIFICATION_TITLES, new HashSet<>()));

        // Ajouter la nouvelle notification
        ids.add(String.valueOf(event.getId()));
        titles.add(event.getTitle());
        count++;

        // Enregistrer les données mises à jour
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_NOTIFICATION_COUNT, count);
        editor.putStringSet(KEY_NOTIFICATION_IDS, ids);
        editor.putStringSet(KEY_NOTIFICATION_TITLES, titles);
        editor.putBoolean(KEY_NOTIFICATION_SEEN, false);
        editor.apply();
    }

    public int getNotificationCount() {
        return prefs.getInt(KEY_NOTIFICATION_COUNT, 0);
    }

    public boolean hasUnseenNotifications() {
        return !prefs.getBoolean(KEY_NOTIFICATION_SEEN, true);
    }

    public void markNotificationsAsSeen() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_NOTIFICATION_SEEN, true);
        editor.apply();
    }

    public List<String> getNotificationTitles() {
        Set<String> titles = prefs.getStringSet(KEY_NOTIFICATION_TITLES, new HashSet<>());
        return new ArrayList<>(titles);
    }

    public void clearNotifications() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_NOTIFICATION_COUNT, 0);
        editor.putStringSet(KEY_NOTIFICATION_IDS, new HashSet<>());
        editor.putStringSet(KEY_NOTIFICATION_TITLES, new HashSet<>());
        editor.putBoolean(KEY_NOTIFICATION_SEEN, true);
        editor.apply();
    }
}