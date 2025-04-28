package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookmyshow.api.ApiClient;
import com.example.bookmyshow.api.ApiService;
import com.example.bookmyshow.enums.CategoryEvent;
import com.example.bookmyshow.models.BackendEvent;
import com.example.bookmyshow.models.BackendEventSchedule;
import com.example.bookmyshow.models.BackendLieu;
import com.example.bookmyshow.models.GeoPoint;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverActivity extends AppCompatActivity {

    private static final String TAG = "DiscoverActivity";

    private RecyclerView tourShowsRecyclerView;
    private TourShowAdapter tourShowAdapter;
    private ApiService apiService;
    private View loadingView;
    private TextView errorTextView;

    // Définir des villes par défaut pour les lieux (puisque BackendLieu n'a plus de city/country)
    private Map<String, String[]> defaultLocations = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // Initialiser l'API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialiser les emplacements par défaut (nom du lieu -> [ville, pays])
        initializeDefaultLocations();

        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialiser les RecyclerViews
        setupRecyclerViews();

        // Configurer les filtres
        setupFilters();

        // Charger les données depuis le backend
        loadTourShowsFromBackend();
    }

    private void initializeDefaultLocations() {
        // Associer des noms de lieux à des villes et pays
        // Format: "nom du lieu" -> ["ville", "pays"]
        defaultLocations.put("Théâtre National", new String[]{"Tunis", "Tunisie"});
        defaultLocations.put("Théâtre Municipal", new String[]{"Tunis", "Tunisie"});
        defaultLocations.put("Opéra de Tunis", new String[]{"Tunis", "Tunisie"});
        defaultLocations.put("Parc des expositions", new String[]{"Tunis", "Tunisie"});

        defaultLocations.put("Comédie-Française", new String[]{"Paris", "France"});
        defaultLocations.put("Théâtre du Châtelet", new String[]{"Paris", "France"});
        defaultLocations.put("Opéra Bastille", new String[]{"Paris", "France"});
        defaultLocations.put("La Défense Arena", new String[]{"Paris", "France"});

        defaultLocations.put("Théâtre Maisonneuve", new String[]{"Montréal", "Canada"});
        defaultLocations.put("Place des Arts", new String[]{"Montréal", "Canada"});
        defaultLocations.put("Centre Bell", new String[]{"Montréal", "Canada"});
    }

    private String[] getLocationForLieu(BackendLieu lieu) {
        // Essayer de trouver la ville et le pays à partir du nom du lieu
        if (lieu != null && lieu.getName() != null && defaultLocations.containsKey(lieu.getName())) {
            return defaultLocations.get(lieu.getName());
        }

        // Si le lieu n'est pas dans notre map, essayer de déterminer à partir des coordonnées
        // (Ceci est une simplification - dans un cas réel, vous pourriez utiliser un service de géocodage inverse)
        if (lieu != null && lieu.getMapPosition() != null) {
            GeoPoint position = lieu.getMapPosition();
            // Exemple très simplifié de détermination de la ville par coordonnées
            if (position.getLatitude() > 36 && position.getLatitude() < 37 &&
                    position.getLongitude() > 10 && position.getLongitude() < 11) {
                return new String[]{"Tunis", "Tunisie"};
            } else if (position.getLatitude() > 48 && position.getLatitude() < 49 &&
                    position.getLongitude() > 2 && position.getLongitude() < 3) {
                return new String[]{"Paris", "France"};
            } else if (position.getLatitude() > 45 && position.getLatitude() < 46 &&
                    position.getLongitude() > -74 && position.getLongitude() < -73) {
                return new String[]{"Montréal", "Canada"};
            }
        }

        // Valeur par défaut si on ne peut pas déterminer la ville
        return new String[]{"Ville inconnue", "Pays inconnu"};
    }

    private void setupRecyclerViews() {
        // RecyclerView pour les spectacles en tournée
        tourShowsRecyclerView = findViewById(R.id.tourShowsRecyclerView);
        tourShowsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tourShowAdapter = new TourShowAdapter(this, new ArrayList<>());
        tourShowsRecyclerView.setAdapter(tourShowAdapter);

    }

    private void setupFilters() {
        ChipGroup filterChipGroup = findViewById(R.id.filterChipGroup);
        ChipGroup locationChipGroup = findViewById(R.id.locationChipGroup);

        // Configurer les écouteurs pour les filtres de catégorie
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Filtrer les spectacles en fonction des catégories sélectionnées
            filterShows();
        });

        // Configurer les écouteurs pour les filtres de localisation
        locationChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Filtrer les spectacles en fonction des localisations sélectionnées
            filterShows();
        });

        // Configurer le bouton "Plus" pour les localisations
        Chip moreLocationsChip = findViewById(R.id.moreLocationsChip);
        moreLocationsChip.setOnClickListener(v -> {
            // Afficher une boîte de dialogue avec plus d'options de localisation
            showMoreLocationsDialog();
        });
    }

    private void filterShows() {
        showLoading();

        // Récupérer la catégorie sélectionnée
        ChipGroup filterChipGroup = findViewById(R.id.filterChipGroup);
        String selectedCategory = getSelectedCategory(filterChipGroup);

        // Récupérer la localisation sélectionnée
        ChipGroup locationChipGroup = findViewById(R.id.locationChipGroup);
        String selectedLocation = getSelectedLocation(locationChipGroup);

        // Recharger les données avec les filtres
        loadTourShowsFromBackend(selectedCategory, selectedLocation);
    }

    private String getSelectedCategory(ChipGroup chipGroup) {
        int checkedChipId = chipGroup.getCheckedChipId();
        if (checkedChipId == View.NO_ID) {
            return null;
        }

        Chip chip = findViewById(checkedChipId);
        if (chip != null) {
            String category = chip.getText().toString().toUpperCase();
            if (category.equals("TOUS")) {
                return null;
            }
            try {
                CategoryEvent.valueOf(category);
                return category;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return null;
    }

    private String getSelectedLocation(ChipGroup chipGroup) {
        int checkedChipId = chipGroup.getCheckedChipId();
        if (checkedChipId == View.NO_ID || checkedChipId == R.id.allLocationsChip) {
            return null;
        } else if (checkedChipId == R.id.tunisChip) {
            return "Tunis";
        } else if (checkedChipId == R.id.parisChip) {
            return "Paris";
        } else if (checkedChipId == R.id.montrealChip) {
            return "Montréal";
        }
        return null;
    }

    private void showMoreLocationsDialog() {
        // Afficher une boîte de dialogue avec plus d'options de localisation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_more_locations, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadTourShowsFromBackend() {
        loadTourShowsFromBackend(null, null);
    }

    private void loadTourShowsFromBackend(String category, String location) {
        showLoading();

        // Étape 1: Charger tous les événements (ou filtrés par catégorie)
        Call<List<BackendEvent>> call;
        if (category != null) {
            call = apiService.getEventsByCategory(category);
        } else {
            call = apiService.getAllEvents();
        }

        call.enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> events = response.body();

                    // Étape 2: Charger tous les horaires pour ces événements
                    loadSchedulesForEvents(events, location, tourShowAdapter);
                } else {
                    showError("Erreur lors du chargement des événements");
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                showError("Erreur de connexion");
                hideLoading();
            }
        });
    }

    private void loadEventsForSchedules(List<Integer> eventIds, List<BackendEventSchedule> schedules, TourShowAdapter adapter) {
        apiService.getAllEvents().enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> allEvents = response.body();

                    // Filtrer les événements par IDs
                    List<BackendEvent> filteredEvents = new ArrayList<>();
                    for (BackendEvent event : allEvents) {
                        if (eventIds.contains(event.getId().intValue())) {
                            filteredEvents.add(event);
                        }
                    }

                    // Charger les lieux pour ces événements
                    loadLieuxForEvents(filteredEvents, schedules, adapter);
                } else {
                    Log.e(TAG, "Erreur lors du chargement des événements");
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
            }
        });
    }

    private void loadSchedulesForEvents(List<BackendEvent> events, String locationFilter, TourShowAdapter adapter) {
        apiService.getAllEventSchedules().enqueue(new Callback<List<BackendEventSchedule>>() {
            @Override
            public void onResponse(Call<List<BackendEventSchedule>> call, Response<List<BackendEventSchedule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEventSchedule> schedules = response.body();

                    // Charger les lieux pour ces événements
                    loadLieuxForEvents(events, schedules, locationFilter, adapter);
                } else {
                    showError("Erreur lors du chargement des horaires");
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<List<BackendEventSchedule>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                showError("Erreur de connexion");
                hideLoading();
            }
        });
    }

    private void loadLieuxForEvents(List<BackendEvent> events, List<BackendEventSchedule> schedules, TourShowAdapter adapter) {
        loadLieuxForEvents(events, schedules, null, adapter);
    }

    private void loadLieuxForEvents(List<BackendEvent> events, List<BackendEventSchedule> schedules, String locationFilter, TourShowAdapter adapter) {
        apiService.getAllLieux().enqueue(new Callback<List<BackendLieu>>() {
            @Override
            public void onResponse(Call<List<BackendLieu>> call, Response<List<BackendLieu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendLieu> lieux = response.body();

                    // Construire les objets TourShow
                    List<TourShow> tourShows = buildTourShows(events, schedules, lieux, locationFilter);

                    // Mettre à jour l'adaptateur
                    adapter.setTourShows(tourShows);
                    adapter.notifyDataSetChanged();
                    hideLoading();
                } else {
                    showError("Erreur lors du chargement des lieux");
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<List<BackendLieu>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                showError("Erreur de connexion");
                hideLoading();
            }
        });
    }

    private List<TourShow> buildTourShows(List<BackendEvent> events, List<BackendEventSchedule> schedules, List<BackendLieu> lieux, String locationFilter) {
        // Créer une map pour accéder rapidement aux lieux par ID
        Map<Long, BackendLieu> lieuMap = new HashMap<>();
        for (BackendLieu lieu : lieux) {
            lieuMap.put(lieu.getId(), lieu);
        }

        // Regrouper les horaires par événement
        Map<Integer, List<BackendEventSchedule>> schedulesByEvent = new HashMap<>();
        for (BackendEventSchedule schedule : schedules) {
            if (schedule.getEventId() != null) {
                int eventId = schedule.getEventId().intValue();
                if (!schedulesByEvent.containsKey(eventId)) {
                    schedulesByEvent.put(eventId, new ArrayList<>());
                }
                schedulesByEvent.get(eventId).add(schedule);
            }
        }

        // Créer les objets TourShow
        List<TourShow> tourShows = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (BackendEvent event : events) {
            if (event.getId() == null) continue;

            int eventId = event.getId().intValue();
            if (!schedulesByEvent.containsKey(eventId)) {
                continue;
            }

            List<BackendEventSchedule> eventSchedules = schedulesByEvent.get(eventId);
            if (eventSchedules.size() < 2) {
                // Ignorer les événements avec moins de 2 dates
                continue;
            }

            // Vérifier si l'événement a plusieurs lieux
            Set<Long> uniqueLieuIds = new HashSet<>();
            for (BackendEventSchedule schedule : eventSchedules) {
                if (schedule.getLieuId() != null) {
                    uniqueLieuIds.add(schedule.getLieuId());
                }
            }

            // Si on filtre par lieu, vérifier si l'événement a au moins une date dans ce lieu
            if (locationFilter != null) {
                boolean hasLocation = false;
                for (BackendEventSchedule schedule : eventSchedules) {
                    Long lieuId = schedule.getLieuId();
                    if (lieuId != null && lieuMap.containsKey(lieuId)) {
                        BackendLieu lieu = lieuMap.get(lieuId);
                        String[] location = getLocationForLieu(lieu);
                        if (location[0].equalsIgnoreCase(locationFilter)) {
                            hasLocation = true;
                            break;
                        }
                    }
                }

                if (!hasLocation) {
                    continue;
                }
            }

            TourShow tourShow = new TourShow();
            tourShow.setId(eventId);
            tourShow.setTitle(event.getTitle());
            tourShow.setDescription(event.getDescription());
            tourShow.setCategory(event.getCategory() != null ? event.getCategory().toString() : "AUTRE");

            // Charger l'image en fonction du titre de l'événement
            String imageName = event.getTitle().toLowerCase().replace(" ", "_").replace("'", "").replace("-", "_");
            imageName = imageName.replace("&", "_and_");

            if (imageName.matches("^[0-9].*")) {
                imageName = "i" + imageName;
            }

            int imageResourceId = getImageResourceId(imageName);
            // Si aucune image spécifique n'est trouvée, utiliser l'image de la catégorie
            if (imageResourceId == 0) {
                imageResourceId = getImageResourceForCategory(event.getCategory());
            }
            tourShow.setImageResId(imageResourceId);

            for (BackendEventSchedule schedule : eventSchedules) {
                Long lieuId = schedule.getLieuId();
                if (lieuId == null || schedule.getDateTime() == null) {
                    continue;
                }

                BackendLieu lieu = lieuMap.get(lieuId);
                if (lieu == null) {
                    continue;
                }

                String day = schedule.getDateTime().format(dayFormatter);
                String month = schedule.getDateTime().format(monthFormatter);
                String year = schedule.getDateTime().format(yearFormatter);
                String time = schedule.getDateTime().format(timeFormatter);

                String[] location = getLocationForLieu(lieu);
                String city = location[0];
                String country = location[1];

                TourDate tourDate = new TourDate(
                        day, month, year,
                        lieu.getName(), city, country,
                        time
                );

                tourShow.addTourDate(tourDate);
            }

            tourShows.add(tourShow);
        }

        return tourShows;
    }

    private int getImageResourceForCategory(CategoryEvent category) {
        if (category == null) {
            return R.drawable.event_1;
        }

        switch (category) {
            case MUSIC:
                return R.drawable.event_1;
            case THEATRE:
                return R.drawable.event_2;
            case DANCE:
                return R.drawable.event_1;
            case FESTIVAL:
                return R.drawable.event_2;
            case COMEDY:
                return R.drawable.event_1;
            case OPERA:
                return R.drawable.event_2;
            default:
                return R.drawable.event_1;
        }
    }

    private int getImageResourceId(String imageName) {
        // Essayer de charger l'image à partir du nom
        int resourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());

        // Si l'image n'existe pas, utiliser une image par défaut
        if (resourceId == 0) {
            return R.drawable.event_1;
        }

        return resourceId;
    }

    private void showLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        if (errorTextView != null) {
            errorTextView.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    private void showError(String errorMessage) {
        if (errorTextView != null) {
            errorTextView.setText(errorMessage);
            errorTextView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // Méthode pour afficher la boîte de dialogue des dates de tournée
    public void showTourDatesDialog(TourShow show) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_tour_dates, null);
        builder.setView(view);

        TextView dialogTitleTextView = view.findViewById(R.id.dialogTitleTextView);
        dialogTitleTextView.setText(show.getTitle() + " - Dates de tournée");

        RecyclerView dialogTourDatesRecyclerView = view.findViewById(R.id.dialogTourDatesRecyclerView);
        dialogTourDatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TourDateAdapter adapter = new TourDateAdapter(this, show.getTourDates());
        dialogTourDatesRecyclerView.setAdapter(adapter);

        // Configurer les onglets des mois
        TabLayout monthTabLayout = view.findViewById(R.id.monthTabLayout);
        setupMonthTabs(monthTabLayout, show.getTourDates(), adapter);

        // Configurer les filtres de localisation
        ChipGroup dialogLocationChipGroup = view.findViewById(R.id.dialogLocationChipGroup);
        dialogLocationChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Filtrer les dates en fonction de la localisation sélectionnée
            filterTourDates(adapter, show.getTourDates(), getSelectedLocation(dialogLocationChipGroup));
        });

        Button closeDialogButton = view.findViewById(R.id.closeDialogButton);
        AlertDialog dialog = builder.create();

        closeDialogButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupMonthTabs(TabLayout tabLayout, List<TourDate> dates, TourDateAdapter adapter) {
        // Extraire les mois uniques des dates
        List<String> monthYears = new ArrayList<>();
        for (TourDate date : dates) {
            String monthYear = date.getMonth() + " " + date.getYear();
            if (!monthYears.contains(monthYear)) {
                monthYears.add(monthYear);
                tabLayout.addTab(tabLayout.newTab().setText(monthYear));
            }
        }

        // Ajouter un listener pour filtrer par mois
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedMonthYear = tab.getText().toString();
                filterTourDatesByMonth(adapter, dates, selectedMonthYear);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Rien à faire
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Rien à faire
            }
        });
    }

    private void filterTourDatesByMonth(TourDateAdapter adapter, List<TourDate> allDates, String monthYear) {
        if (monthYear == null || monthYear.isEmpty()) {
            adapter.setTourDates(allDates);
            return;
        }

        List<TourDate> filteredDates = new ArrayList<>();
        for (TourDate date : allDates) {
            String dateMonthYear = date.getMonth() + " " + date.getYear();
            if (dateMonthYear.equals(monthYear)) {
                filteredDates.add(date);
            }
        }

        adapter.setTourDates(filteredDates);
    }

    private void filterTourDates(TourDateAdapter adapter, List<TourDate> allDates, String location) {
        if (location == null || location.equals("all")) {
            adapter.setTourDates(allDates);
        } else {
            List<TourDate> filteredDates = new ArrayList<>();
            for (TourDate date : allDates) {
                if (date.getCity().equals(location)) {
                    filteredDates.add(date);
                }
            }
            adapter.setTourDates(filteredDates);
        }
    }
}
