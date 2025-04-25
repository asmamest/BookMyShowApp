package com.example.bookmyshow;

import android.app.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class TicketConfirmationActivity extends AppCompatActivity {

    private Button shareTicketButton;
    private Button addToCalendarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_confirmation);

        setupToolbar();
        initViews();
        setupButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViews() {
        shareTicketButton = findViewById(R.id.shareTicketButton);
        addToCalendarButton = findViewById(R.id.addToCalendarButton);
    }

    private void setupButtons() {
        shareTicketButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Ticket for Rock Concert");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm going to Rock Concert: Summer Tour 2023 on April 15, 2023. Join me!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        addToCalendarButton.setOnClickListener(v -> {
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2023, 3, 15, 20, 0); // April 15, 2023, 8:00 PM
            Calendar endTime = Calendar.getInstance();
            endTime.set(2023, 3, 15, 23, 0); // April 15, 2023, 11:00 PM

            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, "Rock Concert: Summer Tour 2023")
                    .putExtra(CalendarContract.Events.DESCRIPTION, "VIP Ticket")
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "Grand Arena, Paris")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            startActivity(intent);
        });
    }
}
