// HomeActivity.java (modifications)
package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.bookmyshow.dialogs.NotificationsDialog;
import com.example.bookmyshow.utils.NotificationManager;import com.bumptech.glide.Glide;
import com.example.bookmyshow.models.BackendEvent;
import com.example.bookmyshow.services.EventDataService;
import com.example.bookmyshow.dialogs.LocationSearchDialog;
import com.example.bookmyshow.utils.LocationManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private ViewPager2 featuredEventsViewPager;
    private LinearLayout featuredEventsIndicator;
    private RecyclerView trendingRecyclerView;
    private RecyclerView nearbyEventsRecyclerView;
    private RecyclerView suggestionsRecyclerView;
    private ChipGroup filterChipGroup;
    private TextView mapViewButton;
    private TextView locationText;
    private TextView radiusText;
    private FrameLayout loadingOverlay;
    private EventDataService eventDataService;
    private LocationManager locationManager;
    private NotificationManager notificationManager;
    private ImageView notificationIcon;
    private TextView notificationBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialiser le service de données
        eventDataService = new EventDataService(this);

        initViews();
        notificationManager = new NotificationManager(this);
        updateNotificationBadge();
        locationManager = new LocationManager(this);
        updateLocationDisplay();
        loadData();
        setupFilterChips();
        setupBottomNavigation();
        setupClickListeners();
        //checkLoginStatus();
    }

    private void initViews() {
        featuredEventsViewPager = findViewById(R.id.featuredEventsViewPager);
        featuredEventsIndicator = findViewById(R.id.featuredEventsIndicator);
        trendingRecyclerView = findViewById(R.id.trendingRecyclerView);
        nearbyEventsRecyclerView = findViewById(R.id.nearbyEventsRecyclerView);
        suggestionsRecyclerView = findViewById(R.id.suggestionsRecyclerView);
        filterChipGroup = findViewById(R.id.filterChipGroup);
        mapViewButton = findViewById(R.id.mapViewButton);
        locationText = findViewById(R.id.locationText);
        radiusText = findViewById(R.id.radiusText);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        notificationIcon = findViewById(R.id.notificationIcon);
        notificationBadge = findViewById(R.id.notificationBadge);

        // Initialiser les RecyclerViews avec des adaptateurs vides pour éviter les erreurs "No adapter attached"
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trendingRecyclerView.setAdapter(new EventAdapter(new ArrayList<>(), true));

        nearbyEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        nearbyEventsRecyclerView.setAdapter(new EventAdapter(new ArrayList<>(), false));

        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionsRecyclerView.setAdapter(new EventAdapter(new ArrayList<>(), false));

        // Initialiser le ViewPager avec un adaptateur vide
        featuredEventsViewPager.setAdapter(new FeaturedEventsAdapter(new ArrayList<>()));
        setupFeaturedEventsIndicators(0);

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        searchEditText.setFocusable(false);
        searchEditText.setClickable(true);
    }
    private void updateLocationDisplay() {
        locationText.setText(locationManager.getLocationName());
        radiusText.setText(locationManager.getRadius() + " km");
    }
    private void loadData() {
        // Charger les événements en vedette
        loadFeaturedEvents();

        // Charger les événements tendance
        loadTrendingEvents();

        // Charger les événements à proximité
        loadNearbyEvents();

        // Charger les suggestions
        loadSuggestions();
    }

    private void loadFeaturedEvents() {
        eventDataService.loadFeaturedEvents(new EventDataService.FeaturedEventsCallback() {
            @Override
            public void onFeaturedEventsLoaded(List<FeaturedEvent> featuredEvents) {
                if (featuredEvents.isEmpty()) {
                    featuredEvents = getDemoFeaturedEvents();
                }

                for (FeaturedEvent event : featuredEvents) {

                    String shortDescription = getShortDescriptionForCategory(event.getCategory());
                    event.setShortDescription(shortDescription);

                    String imageName = event.getTitle().toLowerCase().replace(" ", "_");
                    int imageResourceId = getImageResourceId(imageName);
                    event.setImageResId(imageResourceId);
                }

                FeaturedEventsAdapter adapter = new FeaturedEventsAdapter(featuredEvents);
                featuredEventsViewPager.setAdapter(adapter);

                setupFeaturedEventsIndicators(featuredEvents.size());

                featuredEventsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        setCurrentFeaturedEventIndicator(position);
                    }
                });
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                Log.e(TAG, "Erreur: " + errorMessage);
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                List<FeaturedEvent> demoEvents = getDemoFeaturedEvents();
                FeaturedEventsAdapter adapter = new FeaturedEventsAdapter(demoEvents);
                featuredEventsViewPager.setAdapter(adapter);

                setupFeaturedEventsIndicators(demoEvents.size());
            }
        });
    }


    private String getShortDescriptionForCategory(String category) {
        // Vérifier si la catégorie est null ou vide
        if (category == null || category.isEmpty()) {
            return "Un événement passionnant à ne pas manquer !";
        }

        switch (category) {
            case "MUSIC":
                return "L'événement musical ultime de l'année. ";
            case "THEATRE":
                return "Une pièce captivante mise en scène par des acteurs talentueux.";
            case "DANCE":
                return "Un spectacle de danse dynamique, alliant énergie et élégance.";
            case "FESTIVAL":
                return "Un festival coloré célébrant la musique, la culture et l'art.";
            case "COMEDY":
                return "Des spectacles de comédie hilarants avec les meilleurs humoristes.";
            case "OPERA":
                return "Une soirée d'arias éblouissantes interprétées par des voix de classe mondiale.";
            default:
                return "Un événement passionnant à ne pas manquer !";
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



    private List<FeaturedEvent> getDemoFeaturedEvents() {
        List<FeaturedEvent> demoEvents = new ArrayList<>();
        demoEvents.add(new FeaturedEvent("Summer Music Festival", "The biggest music event of the year", R.drawable.event_1));
        demoEvents.add(new FeaturedEvent("Swan Lake Ballet", "Classical ballet performance", R.drawable.event_1));
        demoEvents.add(new FeaturedEvent("Comedy Night Live", "Stand-up comedy with top comedians", R.drawable.event_1));
        return demoEvents;
    }

    private void loadTrendingEvents() {
        eventDataService.loadTrendingEvents(new EventDataService.EventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                if (events.isEmpty()) {
                    events = getDemoTrendingEvents();
                }

                for (Event event : events) {
                    String imageName = event.getTitle().toLowerCase().replace(" ", "_");
                    int imageResourceId = getImageResourceId(imageName);
                    event.setImageResId(imageResourceId);
                }

                EventAdapter adapter = new EventAdapter(events, true);
                trendingRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                trendingRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                Log.e(TAG, "Erreur: " + errorMessage);
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                // Charger des données de démonstration en cas d'échec
                List<Event> demoEvents = getDemoTrendingEvents();
                EventAdapter adapter = new EventAdapter(demoEvents, true);
                trendingRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                trendingRecyclerView.setAdapter(adapter);
            }
        });
    }

    private List<Event> getDemoTrendingEvents() {
        List<Event> demoEvents = new ArrayList<>();
        demoEvents.add(new Event("Rock Concert", "Grand Arena", "Apr 15", R.drawable.event_5));
        demoEvents.add(new Event("Jazz Night", "Blue Note Club", "Apr 18", R.drawable.event_1));
        demoEvents.add(new Event("Theatre Play", "City Theatre", "Apr 20", R.drawable.event_1));
        demoEvents.add(new Event("DJ Party", "Skyline Club", "Apr 22", R.drawable.event_1));
        return demoEvents;
    }

    private void loadNearbyEvents() {
        eventDataService.loadNearbyEvents(new EventDataService.EventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                if (events.isEmpty()) {
                    // Si aucun événement n'est disponible, ajouter des événements de démonstration
                    events = getDemoNearbyEvents();
                }

                // Dynamically assign images to events based on title
                for (Event event : events) {
                    String imageName = event.getTitle().toLowerCase().replace(" ", "_");
                    int imageResourceId = getImageResourceId(imageName);
                    event.setImageResId(imageResourceId);
                }

                EventAdapter adapter = new EventAdapter(events, false);
                nearbyEventsRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                nearbyEventsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                Log.e(TAG, "Erreur: " + errorMessage);
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                // Charger des données de démonstration en cas d'échec
                List<Event> demoEvents = getDemoNearbyEvents();
                EventAdapter adapter = new EventAdapter(demoEvents, false);
                nearbyEventsRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                nearbyEventsRecyclerView.setAdapter(adapter);
            }
        });
    }

    private List<Event> getDemoNearbyEvents() {
        List<Event> demoEvents = new ArrayList<>();
        demoEvents.add(new Event("Local Band Show", "Community Center", "Today", R.drawable.event_1));
        demoEvents.add(new Event("Art Exhibition", "Modern Gallery", "Tomorrow", R.drawable.event_1));
        demoEvents.add(new Event("Food Festival", "City Park", "This Weekend", R.drawable.event_1));
        return demoEvents;
    }

    private void loadSuggestions() {
        eventDataService.loadSuggestions(new EventDataService.EventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                if (events.isEmpty()) {
                    // Si aucun événement n'est disponible, ajouter des événements de démonstration
                    events = getDemoSuggestions();
                }

                // Dynamically assign images to events based on title
                for (Event event : events) {
                    String imageName = event.getTitle().toLowerCase().replace(" ", "_");
                    int imageResourceId = getImageResourceId(imageName);
                    event.setImageResId(imageResourceId);
                }

                EventAdapter adapter = new EventAdapter(events, false);
                suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                suggestionsRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                Log.e(TAG, "Erreur: " + errorMessage);
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                // Charger des données de démonstration en cas d'échec
                List<Event> demoEvents = getDemoSuggestions();
                EventAdapter adapter = new EventAdapter(demoEvents, false);
                suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                suggestionsRecyclerView.setAdapter(adapter);
            }
        });
    }

    private List<Event> getDemoSuggestions() {
        List<Event> demoEvents = new ArrayList<>();
        demoEvents.add(new Event("Classical Concert", "Symphony Hall", "Next Week", R.drawable.event_1));
        demoEvents.add(new Event("Stand-up Comedy", "Laugh Factory", "Apr 25", R.drawable.event_1));
        demoEvents.add(new Event("Ballet Performance", "Opera House", "May 2", R.drawable.event_1));
        return demoEvents;
    }

    private void setupFeaturedEventsIndicators(int count) {
        featuredEventsIndicator.removeAllViews();

        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageResource(R.drawable.indicator_inactive);
            indicators[i].setLayoutParams(layoutParams);
            featuredEventsIndicator.addView(indicators[i]);
        }

        setCurrentFeaturedEventIndicator(0);
    }

    private void setCurrentFeaturedEventIndicator(int position) {
        int childCount = featuredEventsIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) featuredEventsIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageResource(R.drawable.onboarding_indicator_active);
            } else {
                imageView.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }

    private void setupFilterChips() {
        // Ajouter un listener pour les chips de filtre
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                // Aucun filtre sélectionné, afficher tous les événements
                loadData();
            } else {
                Chip chip = findViewById(checkedIds.get(0));
                String filter = chip.getText().toString();

                // Convertir le nom du filtre en catégorie
                String category;
                switch (filter.toUpperCase()) {
                    case "ALL":
                        loadData();
                        return;
                    case "MUSIC":
                        category = "MUSIC";
                        break;
                    case "THEATRE":
                        category = "THEATRE";
                        break;
                    case "DANCE":
                        category = "DANCE";
                        break;
                    case "OPERA":
                        category = "OPERA";
                        break;
                    case "FESTIVALS":
                        category = "FESTIVAL";
                        break;
                    default:
                        category = filter.toUpperCase();
                        break;
                }

                // Filtrer les événements par catégorie
                filterEventsByCategory(category);
            }
        });
    }
    private void filterEventsByCategory(String category) {
        // Afficher l'indicateur de chargement
        showLoading();

        eventDataService.filterEventsByCategory(category, new EventDataService.EventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                hideLoading();

                // Mettre à jour les événements tendance avec les résultats filtrés
                EventAdapter adapter = new EventAdapter(events, true);
                trendingRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                trendingRecyclerView.setAdapter(adapter);

                // Mettre à jour les événements à proximité avec les résultats filtrés
                EventAdapter nearbyAdapter = new EventAdapter(events, false);
                nearbyEventsRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                nearbyEventsRecyclerView.setAdapter(nearbyAdapter);

                Toast.makeText(HomeActivity.this, "Filtrage par: " + category, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                hideLoading();
                Log.e(TAG, "Erreur de filtrage: " + errorMessage);
                Toast.makeText(HomeActivity.this, "Erreur lors du filtrage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }
    private void refreshEvents(String filter) {
        if (filter == null) {
            // Recharger tous les événements
            loadData();
            return;
        }

        eventDataService.filterEventsByCategory(filter.toUpperCase(), new EventDataService.EventsCallback() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                EventAdapter adapter = new EventAdapter(events, true);
                trendingRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                trendingRecyclerView.setAdapter(adapter);

                Toast.makeText(HomeActivity.this, "Filtrage par: " + filter, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataNotAvailable(String errorMessage) {
                Log.e(TAG, "Erreur de filtrage: " + errorMessage);
                Toast.makeText(HomeActivity.this, "Erreur lors du filtrage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_explore) {
                Intent intent = new Intent(HomeActivity.this, DiscoverActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_tickets) {
                Intent intent = new Intent(HomeActivity.this, MyTicketsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        mapViewButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MapActivity.class);
            startActivity(intent);
        });

        locationText.setOnClickListener(v -> {
            showLocationSearchDialog();
        });

        radiusText.setOnClickListener(v -> {
            showLocationSearchDialog();
        });
        notificationIcon.setOnClickListener(v -> {
            showNotificationsDialog();
        });
    }
    private void showLocationSearchDialog() {
        LocationSearchDialog dialog = new LocationSearchDialog(this, locationManager,
                (locationName, latitude, longitude, radius) -> {
                    updateLocationDisplay();

                    loadData();

                    Toast.makeText(this, "Localisation mise à jour: " + locationName, Toast.LENGTH_SHORT).show();
                });
        dialog.show();
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("BookMyShowPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        // If not logged in, redirect to login
        if (!isLoggedIn) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void updateNotificationBadge() {
        if (notificationManager.hasUnseenNotifications() && notificationManager.getNotificationCount() > 0) {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(String.valueOf(notificationManager.getNotificationCount()));
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    private void showNotificationsDialog() {
        NotificationsDialog dialog = new NotificationsDialog(this, notificationManager);
        dialog.setOnDismissListener(dialogInterface -> updateNotificationBadge());
        dialog.show();
    }

    // Adapter for Featured Events
    static class FeaturedEventsAdapter extends RecyclerView.Adapter<FeaturedEventsAdapter.FeaturedEventViewHolder> {

        private List<FeaturedEvent> featuredEvents;

        public FeaturedEventsAdapter(List<FeaturedEvent> featuredEvents) {
            this.featuredEvents = featuredEvents;
        }

        @NonNull
        @Override
        public FeaturedEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_featured_event, parent, false
            );
            return new FeaturedEventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeaturedEventViewHolder holder, int position) {
            holder.bind(featuredEvents.get(position));
        }

        @Override
        public int getItemCount() {
            return featuredEvents.size();
        }

        static class FeaturedEventViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private TextView titleTextView;
            private TextView descriptionTextView;

            public FeaturedEventViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.featuredImageView);
                titleTextView = itemView.findViewById(R.id.featuredTitleTextView);
                descriptionTextView = itemView.findViewById(R.id.featuredDescriptionTextView);

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), EventDetailActivity.class);
                    // Passer l'ID de l'événement si disponible
                    FeaturedEvent event = (FeaturedEvent) itemView.getTag();
                    if (event != null && event.getId() != null) {
                        intent.putExtra("eventId", event.getId());
                    }
                    itemView.getContext().startActivity(intent);
                });
            }

            void bind(FeaturedEvent featuredEvent) {
                itemView.setTag(featuredEvent);

                imageView.setImageResource(featuredEvent.getImageResId());
                titleTextView.setText(featuredEvent.getTitle());
                descriptionTextView.setText(featuredEvent.getDescription());
            }
        }
    }

    // Adapter for Events (Trending, Nearby, Suggestions)
    static class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        private List<Event> events;
        private boolean isHorizontal;

        public EventAdapter(List<Event> events, boolean isHorizontal) {
            this.events = events;
            this.isHorizontal = isHorizontal;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutId = isHorizontal ? R.layout.item_event_horizontal : R.layout.item_event_vertical;
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            holder.bind(events.get(position));
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        static class EventViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private TextView titleTextView;
            private TextView venueTextView;
            private TextView dateTextView;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.eventImageView);
                titleTextView = itemView.findViewById(R.id.eventTitleTextView);
                venueTextView = itemView.findViewById(R.id.eventVenueTextView);
                dateTextView = itemView.findViewById(R.id.eventDateTextView);

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), EventDetailActivity.class);
                    // Passer l'ID de l'événement si disponible
                    Event event = (Event) itemView.getTag();
                    if (event != null && event.getId() != null) {
                        intent.putExtra("eventId", event.getId());
                    }
                    itemView.getContext().startActivity(intent);
                });
            }

            void bind(Event event) {
                itemView.setTag(event);

                imageView.setImageResource(event.getImageResId());
                titleTextView.setText(event.getTitle());
                venueTextView.setText(event.getVenue());
                dateTextView.setText(event.getDate());
            }
        }
    }

    // Model classes
    public static class FeaturedEvent {
        private Long id;
        private String title;
        private String description;
        private int imageResId;
        private String category;
        private String shortDescription;
        // Constructor with category and shortDescription
        public FeaturedEvent(Long id, String title, String description, int imageResId, String category) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.imageResId = imageResId;
            this.category = category;
        }

        // Constructor without category and shortDescription (for backwards compatibility)
        public FeaturedEvent(String title, String description, int imageResId) {
            this.title = title;
            this.description = description;
            this.imageResId = imageResId;
        }

        // Getters and Setters for new fields

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        // Existing getters and setters for other fields
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getImageResId() {
            return imageResId;
        }

        public void setImageResId(int imageResId){
            this.imageResId = imageResId;
        }
    }


    public static class Event {
        private Long id;
        private String title;
        private String venue;
        private String date;
        private int imageResId;

        public Event(String title, String venue, String date, int imageResId) {
            this.title = title;
            this.venue = venue;
            this.date = date;
            this.imageResId = imageResId;
        }

        public Event(Long id, String title, String venue, String date, int imageResId) {
            this.id = id;
            this.title = title;
            this.venue = venue;
            this.date = date;
            this.imageResId = imageResId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public String getVenue() {
            return venue;
        }

        public String getDate() {
            return date;
        }

        public int getImageResId() {
            return imageResId;
        }
        public void setImageResId(int imageResId){
            this.imageResId=imageResId;
        }
    }
}