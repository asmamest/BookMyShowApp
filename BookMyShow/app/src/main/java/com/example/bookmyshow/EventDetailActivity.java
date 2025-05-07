package com.example.bookmyshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.bookmyshow.models.BackendEvent;
import com.example.bookmyshow.models.BackendEventSchedule;
import com.example.bookmyshow.services.EventDataService;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    private Button bookTicketsButton;
    private Button shareButton;
    private TextView eventTitleText;
    private TextView eventDescriptionText;
    private ImageView eventHeaderImage;
    private TextView eventLocationText;
    private TextView eventDateText;
    private TextView availabilityStatusText;
    private TextView ticketsAvailableText;
    private ImageView venueMapImageView;
    private View venueMapContainer;

    private EventDataService eventDataService;
    private Long eventId;
    private BackendEvent currentEvent;
    private BackendEventSchedule currentSchedule;
    private boolean isSoldOut = false;
    private String eventLocation;

    // Prix des billets par catégorie
    private static final double VIP_PRICE = 199.99;
    private static final double PREMIUM_PRICE = 99.99;
    private static final double STANDARD_PRICE = 49.99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventDataService = new EventDataService(this);

        setupToolbar();
        initViews();

        // Récupérer l'ID de l'événement
        eventId = getIntent().getLongExtra("eventId", -1);
        Log.d(TAG, "Received eventId: " + eventId);

        if (eventId != -1) {
            // Charger les détails de l'événement et son horaire
            loadEventWithSchedule();
        } else {
            Log.e(TAG, "Aucun ID d'événement reçu!");
            Toast.makeText(this, "Erreur: Impossible de charger les détails de l'événement", Toast.LENGTH_SHORT).show();
        }

        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitle("Détails de l'événement");
    }

    private void initViews() {
        bookTicketsButton = findViewById(R.id.bookTicketsButton);
        shareButton = findViewById(R.id.shareButton);
        eventTitleText = findViewById(R.id.eventTitleText);
        eventDescriptionText = findViewById(R.id.eventDescriptionText);
        eventHeaderImage = findViewById(R.id.eventHeaderImage);
        eventLocationText = findViewById(R.id.eventLocationText);
        eventDateText = findViewById(R.id.eventDateText);
        availabilityStatusText = findViewById(R.id.availabilityStatusText);
        ticketsAvailableText = findViewById(R.id.ticketsAvailableText);

        // Initialiser les vues pour la carte du théâtre
        venueMapImageView = findViewById(R.id.venueMapImageView);
        venueMapContainer = findViewById(R.id.venueMapContainer);
    }

    private void loadEventWithSchedule() {
        // Afficher un indicateur de chargement si nécessaire
        // showLoading();

        // Étape 1: Charger les détails de l'événement
        eventDataService.loadEventDetails(eventId, new EventDataService.EventDetailCallback() {
            @Override
            public void onEventLoaded(BackendEvent event) {
                currentEvent = event;

                // Mettre à jour l'interface avec les détails de l'événement
                updateEventDetails(event);

                // Étape 2: Charger l'horaire de l'événement
                loadEventSchedule();
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                // hideLoading();
                Log.e(TAG, "Erreur: " + errorMessage);
                Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEventDetails(BackendEvent event) {
        if (event.getTitle() != null) {
            eventTitleText.setText(event.getTitle());
            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
            collapsingToolbarLayout.setTitle(event.getTitle());
        }

        if (event.getDescription() != null) {
            eventDescriptionText.setText(event.getDescription());
        }

        // Charger l'image en fonction du titre de l'événement
        String imageName = event.getTitle().toLowerCase().replace(" ", "_");
        int imageResourceId = getImageResourceId(imageName);
        if (imageResourceId != 0) {
            eventHeaderImage.setImageResource(imageResourceId);
        } else {
            eventHeaderImage.setImageResource(R.drawable.event_detail_header);
        }
    }

    private int getImageResourceId(String imageName) {
        imageName = imageName.replace("&", "_and_");

        if (imageName.matches("^[0-9].*")) {
            imageName = "i" + imageName;
        }

        imageName = imageName.toLowerCase().replace(" ", "_");

        String packageName = getApplicationContext().getPackageName();
        int resId = getResources().getIdentifier(imageName, "drawable", packageName);

        if (resId == 0) {
            resId = R.drawable.event_1;
        }
        return resId;
    }

    private void loadEventSchedule() {
        eventDataService.loadEventSchedules(eventId, new EventDataService.EventSchedulesCallback() {
            @Override
            public void onSchedulesLoaded(List<BackendEventSchedule> schedules) {
                // hideLoading();

                if (schedules.isEmpty()) {
                    Log.e(TAG, "Aucun horaire trouvé pour l'événement");
                    Toast.makeText(EventDetailActivity.this, "Aucun horaire disponible", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prendre le premier horaire (puisque chaque événement n'a qu'un seul horaire)
                currentSchedule = schedules.get(0);
                updateScheduleDetails(currentSchedule);
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                // hideLoading();
                Log.e(TAG, "Erreur: " + errorMessage);
                Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateScheduleDetails(BackendEventSchedule schedule) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        if (schedule.getDateTime() != null) {
            String formattedDate = schedule.getDateTime().format(dateFormatter);
            String time = schedule.getDateTime().format(timeFormatter);
            eventDateText.setText(formattedDate + " | " + time);
        }

        if (schedule.getLieuName() != null) {
            eventLocation = schedule.getLieuName();
            eventLocationText.setText(eventLocation);
            checkAndDisplayVenueMap(eventLocation);
        }

        // Mettre à jour le statut de disponibilité
        isSoldOut = schedule.isSoldOut();
        updateAvailabilityStatus(isSoldOut);
    }

    private void checkAndDisplayVenueMap(String location) {
        // Vérifier si le lieu contient "Théâtre Municipal" et "Tunis"
        if (location != null && location.contains("Théâtre municipal de Tunis") && location.contains("Tunis")) {
            // Afficher la carte du théâtre
            venueMapContainer.setVisibility(View.VISIBLE);
            venueMapImageView.setImageResource(R.drawable.theatre_municipal_tunis_map);

            // Vous pouvez également ajouter un texte explicatif
            TextView venueMapTitle = findViewById(R.id.venueMapTitle);
            if (venueMapTitle != null) {
                venueMapTitle.setText("Plan du Théâtre Municipal de Tunis");
            }
        } else {
            // Masquer la carte si ce n'est pas le Théâtre Municipal de Tunis
            venueMapContainer.setVisibility(View.GONE);
        }
    }

    private void updateAvailabilityStatus(boolean isSoldOut) {
        if (availabilityStatusText != null) {
            if (isSoldOut) {
                availabilityStatusText.setText("COMPLET");
                availabilityStatusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                if (bookTicketsButton != null) {
                    bookTicketsButton.setEnabled(false);
                    bookTicketsButton.setText("Complet");
                    bookTicketsButton.setAlpha(0.5f);
                }

                if (ticketsAvailableText != null) {
                    ticketsAvailableText.setVisibility(View.GONE);
                }
            } else {
                availabilityStatusText.setText("DISPONIBLE");
                availabilityStatusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                if (bookTicketsButton != null) {
                    bookTicketsButton.setEnabled(true);
                    bookTicketsButton.setText("Réserver");
                    bookTicketsButton.setAlpha(1.0f);
                }

                if (ticketsAvailableText != null) {
                    ticketsAvailableText.setVisibility(View.VISIBLE);
                    int availableTickets = (int) (Math.random() * 100) + 1;
                    ticketsAvailableText.setText(availableTickets + " billets disponibles");
                }
            }
        }
    }

    private void setupClickListeners() {
        bookTicketsButton.setOnClickListener(v -> {
            if (isSoldOut) {
                Toast.makeText(this, "Désolé, cet événement est complet", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(EventDetailActivity.this, PaymentActivity.class);

            // Passer les informations de l'événement
            if (eventId != -1) {
                intent.putExtra("eventId", eventId);
            }

            // Passer les informations communes de l'événement
            intent.putExtra("eventTitle", eventTitleText.getText().toString());
            intent.putExtra("eventDate", eventDateText.getText().toString());
            intent.putExtra("eventLocation", eventLocation);

            // Passer les prix des billets
            intent.putExtra("vipPrice", VIP_PRICE);
            intent.putExtra("premiumPrice", PREMIUM_PRICE);
            intent.putExtra("standardPrice", STANDARD_PRICE);

            startActivity(intent);
        });

        shareButton.setOnClickListener(v -> {
            String shareText = eventTitleText.getText() + "\n" + eventDateText.getText() + "\n" + eventLocationText.getText();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Partager via"));
        });

        eventLocationText.setOnClickListener(v -> {
            String locationName = eventLocationText.getText().toString();

            // Coordonnées par défaut (à remplacer par les vraies coordonnées si disponibles)
            String latitude = "36.8065";  // Coordonnées de Tunis
            String longitude = "10.1815";

            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(locationName));

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Uri browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query="
                        + Uri.encode(locationName));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}