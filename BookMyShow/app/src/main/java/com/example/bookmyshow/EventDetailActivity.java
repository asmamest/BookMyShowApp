package com.example.bookmyshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bookmyshow.models.BackendEvent;
import com.example.bookmyshow.models.BackendEventSchedule;
import com.example.bookmyshow.services.EventDataService;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    private RecyclerView ticketOptionsRecyclerView;
    private Button bookTicketsButton;
    private Button shareButton;
    private TextView eventTitleText;
    private TextView eventDescriptionText;
    private ImageView eventHeaderImage;
    private TextView eventLocationText;
    private TextView eventDateText;
    private TextView availabilityStatusText;
    private TextView ticketsAvailableText;
    private ImageView venueMapImageView; // Nouvelle ImageView pour la carte du théâtre
    private View venueMapContainer; // Conteneur pour la carte du théâtre

    private EventDataService eventDataService;
    private Long eventId;
    private int dateIndex = -1;
    private BackendEvent currentEvent;
    private boolean isSoldOut = false;
    private String eventLocation; // Pour stocker le lieu de l'événement

    // Prix des billets par catégorie
    private static final double VIP_PRICE = 199.99;
    private static final double PREMIUM_PRICE = 99.99;
    private static final double STANDARD_PRICE = 49.99;

    // Liste des options de billets
    private List<TicketOption> ticketOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventDataService = new EventDataService(this);

        setupToolbar();
        initViews();

        eventId = getIntent().getLongExtra("eventId", -1);
        dateIndex = getIntent().getIntExtra("dateIndex", -1);
        isSoldOut = getIntent().getBooleanExtra("isSoldOut", false);

        Log.d(TAG, "Received eventId: " + eventId + ", dateIndex: " + dateIndex + ", isSoldOut: " + isSoldOut);

        String eventTitle = getIntent().getStringExtra("eventTitle");
        String eventDescription = getIntent().getStringExtra("eventDescription");
        String eventCategory = getIntent().getStringExtra("eventCategory");
        int eventImageResId = getIntent().getIntExtra("eventImageResId", 0);

        String eventDate = getIntent().getStringExtra("eventDate");
        eventLocation = getIntent().getStringExtra("eventLocation");

        if (eventTitle != null && eventDescription != null) {
            eventTitleText.setText(eventTitle);
            eventDescriptionText.setText(eventDescription);

            if (eventImageResId != 0) {
                eventHeaderImage.setImageResource(eventImageResId);
            } else {
                eventHeaderImage.setImageResource(R.drawable.event_detail_header);
            }

            CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
            collapsingToolbarLayout.setTitle(eventTitle);
        }

        if (eventDate != null) {
            eventDateText.setText(eventDate);
        }

        if (eventLocation != null) {
            eventLocationText.setText(eventLocation);
            // Vérifier si le lieu est le Théâtre Municipal de Tunis
            checkAndDisplayVenueMap(eventLocation);
        }

        updateAvailabilityStatus(isSoldOut);

        if (eventId != -1) {
            loadEventDetails();
        }

        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitle("Event Details");
    }

    private void initViews() {
        //ticketOptionsRecyclerView = findViewById(R.id.ticketOptionsRecyclerView);
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


    private void loadEventDetails() {
        eventDataService.loadEventDetails(eventId, new EventDataService.EventDetailCallback() {
            @Override
            public void onEventLoaded(BackendEvent event) {
                currentEvent = event;

                if (eventTitleText.getText().toString().isEmpty()) {
                    eventTitleText.setText(event.getTitle());
                    eventDescriptionText.setText(event.getDescription());

                    String imageName = event.getTitle().toLowerCase().replace(" ", "_");
                    int imageResourceId = getImageResourceId(imageName);

                    if (imageResourceId != 0) {
                        eventHeaderImage.setImageResource(imageResourceId);
                    } else {
                        eventHeaderImage.setImageResource(R.drawable.event_detail_header);
                    }

                    CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
                    collapsingToolbarLayout.setTitle(event.getTitle());
                }

                loadEventScheduleDetails();
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

            @Override
            public void onDataNotAvailable(String errorMessage) {
                Log.e(TAG, "Erreur: " + errorMessage);
                Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventScheduleDetails() {
        dateIndex = getIntent().getIntExtra("dateIndex", -1);

        String eventDate = getIntent().getStringExtra("eventDate");
        eventLocation = getIntent().getStringExtra("eventLocation");
        boolean isSoldOut = getIntent().getBooleanExtra("isSoldOut", false);

        if (eventDate != null && eventLocation != null) {
            eventDateText.setText(eventDate);
            eventLocationText.setText(eventLocation);

            // Vérifier si le lieu est le Théâtre Municipal de Tunis
            checkAndDisplayVenueMap(eventLocation);
        }

        updateAvailabilityStatus(isSoldOut);

        eventDataService.loadEventSchedules(eventId, new EventDataService.EventSchedulesCallback() {
            @Override
            public void onSchedulesLoaded(List<BackendEventSchedule> schedules) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

                if (dateIndex >= 0 && dateIndex < schedules.size()) {
                    BackendEventSchedule specificSchedule = schedules.get(dateIndex);

                    if (specificSchedule.getDateTime() != null) {
                        if (eventDate == null) {
                            String formattedDate = specificSchedule.getDateTime().format(dateFormatter);
                            String time = specificSchedule.getDateTime().format(timeFormatter);
                            eventDateText.setText(formattedDate + " | " + time);
                        }

                        if (eventLocation == null && eventLocationText != null && specificSchedule.getLieuName() != null) {
                            eventLocation = specificSchedule.getLieuName();
                            eventLocationText.setText(eventLocation);

                            // Vérifier si le lieu est le Théâtre Municipal de Tunis
                            checkAndDisplayVenueMap(eventLocation);
                        }

                        boolean isSoldOutFromApi = specificSchedule.isSoldOut();
                        updateAvailabilityStatus(isSoldOutFromApi);
                    }
                }
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                Log.e(TAG, "Erreur: " + errorMessage);
            }
        });
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
                if (dateIndex != -1) {
                    intent.putExtra("dateIndex", dateIndex);
                }
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

            String latitude = "48.8566";
            String longitude = "2.3522";

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

                Toast.makeText(this, "Google Maps n'est pas installé", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    static class TicketOptionsAdapter extends RecyclerView.Adapter<TicketOptionsAdapter.TicketOptionViewHolder> {

        private List<TicketOption> ticketOptions;
        private int selectedPosition = -1;

        public TicketOptionsAdapter(List<TicketOption> ticketOptions) {
            this.ticketOptions = ticketOptions;
        }

        @NonNull
        @Override
        public TicketOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_ticket_option, parent, false
            );
            return new TicketOptionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TicketOptionViewHolder holder, int position) {
            holder.bind(ticketOptions.get(position), position == selectedPosition);

            holder.radioButton.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
            });
        }

        @Override
        public int getItemCount() {
            return ticketOptions.size();
        }

        static class TicketOptionViewHolder extends RecyclerView.ViewHolder {
            private TextView typeTextView;
            private TextView descriptionTextView;
            private TextView priceTextView;
            private RadioButton radioButton;

            public TicketOptionViewHolder(@NonNull View itemView) {
                super(itemView);
                typeTextView = itemView.findViewById(R.id.ticketTypeTextView);
                descriptionTextView = itemView.findViewById(R.id.ticketDescriptionTextView);
                priceTextView = itemView.findViewById(R.id.ticketPriceTextView);
                radioButton = itemView.findViewById(R.id.ticketRadioButton);
            }

            void bind(TicketOption ticketOption, boolean isSelected) {
                typeTextView.setText(ticketOption.getType());
                descriptionTextView.setText(ticketOption.getDescription());
                priceTextView.setText(String.format("$%.2f", ticketOption.getPrice()));
                radioButton.setChecked(isSelected);
            }
        }
    }

    static class TicketOption {
        private String type;
        private String description;
        private double price;

        public TicketOption(String type, String description, double price) {
            this.type = type;
            this.description = description;
            this.price = price;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public double getPrice() {
            return price;
        }
    }
}