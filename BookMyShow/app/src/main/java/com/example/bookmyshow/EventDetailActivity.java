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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";

    private RecyclerView scheduleRecyclerView;
    private RecyclerView ticketOptionsRecyclerView;
    private Button bookTicketsButton;
    private Button shareButton;
    private TextView eventTitleText;
    private TextView eventDescriptionText;
    private ImageView eventHeaderImage;
    private TextView eventLocationText;

    private EventDataService eventDataService;
    private Long eventId;
    private BackendEvent currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialiser le service de données
        eventDataService = new EventDataService(this);

        setupToolbar();
        initViews();

        // Récupérer l'ID de l'événement depuis l'intent
        eventId = getIntent().getLongExtra("eventId", -1);
        Log.d(TAG, "Received eventId: " + eventId);

        if (eventId != -1) {
            loadEventDetails();
        } else {
            // Si aucun ID n'est fourni, charger des données de démonstration
            Log.w(TAG, "No eventId provided, loading demo data");
            setupSchedule();
            setupTicketOptions();
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
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        ticketOptionsRecyclerView = findViewById(R.id.ticketOptionsRecyclerView);
        bookTicketsButton = findViewById(R.id.bookTicketsButton);
        shareButton = findViewById(R.id.shareButton);
        eventTitleText = findViewById(R.id.eventTitleText);
        eventDescriptionText = findViewById(R.id.eventDescriptionText);
        eventHeaderImage = findViewById(R.id.eventHeaderImage);
        eventLocationText = findViewById(R.id.eventLocationText);
    }

    private void loadEventDetails() {
        eventDataService.loadEventDetails(eventId, new EventDataService.EventDetailCallback() {
            @Override
            public void onEventLoaded(BackendEvent event) {
                currentEvent = event;

                // Mettre à jour l'interface utilisateur avec les détails de l'événement
                eventTitleText.setText(event.getTitle());
                eventDescriptionText.setText(event.getDescription());

                // Charger l'image de l'événement si disponible
                String imageName = event.getTitle().toLowerCase().replace(" ", "_");
                int imageResourceId = getImageResourceId(imageName);

                if (imageResourceId != 0) {
                    eventHeaderImage.setImageResource(imageResourceId);
                }
                else {
                    eventHeaderImage.setImageResource(R.drawable.event_detail_header);
                }

                // Mettre à jour le titre de la toolbar
                CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
                collapsingToolbarLayout.setTitle(event.getTitle());

                // Charger les horaires de l'événement
                loadEventSchedules();

                // Configurer les options de billets (statique pour l'instant)
                setupTicketOptions();
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

                // Charger des données de démonstration en cas d'échec
                setupSchedule();
                setupTicketOptions();
            }
        });
    }

    private void loadEventSchedules() {
        eventDataService.loadEventSchedules(eventId, new EventDataService.EventSchedulesCallback() {
            @Override
            public void onSchedulesLoaded(List<BackendEventSchedule> schedules) {
                List<ScheduleItem> scheduleItems = new ArrayList<>();
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

                for (BackendEventSchedule schedule : schedules) {
                    if (schedule.getDateTime() != null) {
                        String time = schedule.getDateTime().format(timeFormatter);
                        String description = schedule.getEventTitle() != null ?
                                schedule.getEventTitle() : currentEvent.getTitle();
                        description += " à " + (schedule.getLieuName() != null ?
                                schedule.getLieuName() : "Lieu à confirmer");

                        scheduleItems.add(new ScheduleItem(time, description));

                        // Mettre à jour le lieu de l'événement pour le clic sur la carte
                        if (eventLocationText != null && schedule.getLieuName() != null) {
                            eventLocationText.setText(schedule.getLieuName());
                        }
                    }
                }

                if (scheduleItems.isEmpty()) {
                    // Si aucun horaire n'est disponible, ajouter un horaire de démonstration
                    scheduleItems.add(new ScheduleItem("8:00 PM", "Opening Act: The Rockers"));
                }

                ScheduleAdapter adapter = new ScheduleAdapter(scheduleItems);
                scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(EventDetailActivity.this));
                scheduleRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                Log.e(TAG, "Erreur: " + errorMessage);

                // Charger des horaires de démonstration en cas d'échec
                setupSchedule();
            }
        });
    }

    private void setupSchedule() {
        List<ScheduleItem> scheduleItems = new ArrayList<>();
        scheduleItems.add(new ScheduleItem("8:00 PM", "Opening Act: The Rockers"));

        ScheduleAdapter adapter = new ScheduleAdapter(scheduleItems);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(adapter);
    }

    private void setupTicketOptions() {
        List<TicketOption> ticketOptions = new ArrayList<>();

        // Add sample ticket options
        ticketOptions.add(new TicketOption("VIP", "Front row seats + Backstage access", 199.99));
        ticketOptions.add(new TicketOption("Premium", "Reserved seating in premium section", 99.99));
        ticketOptions.add(new TicketOption("Standard", "General admission", 49.99));

        TicketOptionsAdapter adapter = new TicketOptionsAdapter(ticketOptions);
        ticketOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ticketOptionsRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        bookTicketsButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, PaymentActivity.class);
            if (eventId != -1) {
                intent.putExtra("eventId", eventId);
            }
            startActivity(intent);
        });

        bookTicketsButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, PaymentActivity.class);

            if (eventId != -1) {
                intent.putExtra("eventId", eventId);
                intent.putExtra("eventTitle", eventTitleText); // le titre de l'event
                intent.putExtra("ticketType", ticketOptionsRecyclerView); // ex: "VIP Ticket"
                intent.putExtra("eventDateTime", eventDateTime); // ex: "April 15, 2023 | 8:00 PM"
                intent.putExtra("eventVenue", eventVenue); // ex: "Grand Arena, Paris"
                intent.putExtra("ticketPrice", ticketPrice); // ex: 199.99
            }

            startActivity(intent);
        });

        // Gestionnaire d'événements pour le clic sur le lieu
        eventLocationText.setOnClickListener(v -> {
            // Récupérer les informations du lieu depuis l'événement
            String locationName = eventLocationText.getText().toString();

            // Coordonnées de l'événement (à remplacer par les coordonnées réelles de votre événement)
            String latitude = "48.8566";
            String longitude = "2.3522";

            // Créer l'URI pour Google Maps
            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(locationName));

            // Créer l'intent pour ouvrir Google Maps
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Vérifier si Google Maps est installé
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Si Google Maps n'est pas installé, ouvrir dans le navigateur
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

    // Adapter for Schedule
    static class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

        private List<ScheduleItem> scheduleItems;

        public ScheduleAdapter(List<ScheduleItem> scheduleItems) {
            this.scheduleItems = scheduleItems;
        }

        @NonNull
        @Override
        public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_schedule, parent, false
            );
            return new ScheduleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
            holder.bind(scheduleItems.get(position));
        }

        @Override
        public int getItemCount() {
            return scheduleItems.size();
        }

        static class ScheduleViewHolder extends RecyclerView.ViewHolder {
            private TextView timeTextView;
            private TextView descriptionTextView;
            private TextView checkinTextView; // Nouveau TextView pour l'heure de check-in

            public ScheduleViewHolder(@NonNull View itemView) {
                super(itemView);
                timeTextView = itemView.findViewById(R.id.scheduleTimeTextView);
                descriptionTextView = itemView.findViewById(R.id.scheduleDescriptionTextView);
                checkinTextView = itemView.findViewById(R.id.scheduleCheckinTextView);
            }

            void bind(ScheduleItem scheduleItem) {
                timeTextView.setText(scheduleItem.getTime());
                descriptionTextView.setText(scheduleItem.getDescription());

                // Afficher l'heure de check-in (3 heures avant l'événement)
                if (checkinTextView != null) {
                    checkinTextView.setText("Check-in: " + scheduleItem.getCheckinTime());
                }
            }
        }
    }

    // Modification de la classe ScheduleItem pour inclure l'heure de check-in
    static class ScheduleItem {
        private String time;
        private String description;
        private String checkinTime;

        public ScheduleItem(String time, String description) {
            this.time = time;
            this.description = description;

            // Calculer l'heure de check-in (3 heures avant l'heure de l'événement)
            try {
                // Supposons que le format de l'heure est "HH:MM AM/PM"
                String[] parts = time.split(" ");
                String[] timeParts = parts[0].split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                String amPm = parts[1];

                // Convertir en format 24 heures pour le calcul
                if (amPm.equals("PM") && hour < 12) {
                    hour += 12;
                } else if (amPm.equals("AM") && hour == 12) {
                    hour = 0;
                }

                // Soustraire 3 heures
                hour -= 3;
                if (hour < 0) {
                    hour += 24;
                    amPm = amPm.equals("AM") ? "PM" : "AM";
                }

                // Convertir en format 12 heures pour l'affichage
                if (hour > 12) {
                    hour -= 12;
                    amPm = "PM";
                } else if (hour == 0) {
                    hour = 12;
                    amPm = "AM";
                } else if (hour == 12) {
                    amPm = "PM";
                }

                this.checkinTime = String.format("%d:%02d %s", hour, minute, amPm);
            } catch (Exception e) {
                this.checkinTime = "3 hours before event";
            }
        }

        public String getTime() {
            return time;
        }

        public String getDescription() {
            return description;
        }

        public String getCheckinTime() {
            return checkinTime;
        }
    }
    // Adapter for Ticket Options
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