package com.example.bookmyshow.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.bookmyshow.R;
import com.example.bookmyshow.api.ApiClient;
import com.example.bookmyshow.api.ApiService;
import com.example.bookmyshow.enums.CategoryEvent;
import com.example.bookmyshow.models.BackendEvent;
import com.example.bookmyshow.models.BackendEventSchedule;
import com.example.bookmyshow.models.BackendLieu;
import com.example.bookmyshow.HomeActivity;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDataService {
    private static final String TAG = "EventDataService";
    private ApiService apiService;
    private Context context;

    public EventDataService(Context context) {
        this.context = context;
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    // Méthode pour convertir un BackendEvent en Event pour l'UI
    public HomeActivity.Event convertToUiEvent(BackendEvent backendEvent, String venue, String date) {
        return new HomeActivity.Event(
                backendEvent.getId(),
                backendEvent.getTitle(),
                venue,
                date,
                R.drawable.event_1
        );
    }

    // Méthode pour convertir un BackendEvent en FeaturedEvent pour l'UI
    public HomeActivity.FeaturedEvent convertToFeaturedEvent(BackendEvent backendEvent) {
        CategoryEvent category = backendEvent.getCategory();

        String shortDescription = getShortDescriptionForCategory(category);

        Log.d("convertToFeaturedEvent", "Converting event: Title=" + backendEvent.getTitle() + ", ID=" + backendEvent.getId());

        return new HomeActivity.FeaturedEvent(
                backendEvent.getId(),
                backendEvent.getTitle(),
                shortDescription,
                R.drawable.event_2
        );
    }

    private String getShortDescriptionForCategory(CategoryEvent category) {
        if (category == null) {
            return "Un événement passionnant à ne pas manquer !";
        }

        switch (category) {
            case MUSIC:
                return "L'événement musical ultime de l'année.";
            case THEATRE:
                return "Une pièce captivante mise en scène par des acteurs talentueux.";
            case DANCE:
                return "Un spectacle de danse dynamique, alliant énergie et élégance.";
            case FESTIVAL:
                return "Un festival coloré célébrant la musique, la culture et l'art.";
            case COMEDY:
                return "Des spectacles de comédie hilarants avec les meilleurs humoristes.";
            case OPERA:
                return "Une soirée d'arias éblouissantes interprétées par des voix de classe mondiale.";
            default:
                return "Un événement passionnant à ne pas manquer !";
        }
    }

    // Méthode pour charger les événements en vedette
    public void loadFeaturedEvents(final FeaturedEventsCallback callback) {
        apiService.getAllEvents().enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> backendEvents = response.body();
                    List<HomeActivity.FeaturedEvent> featuredEvents = new ArrayList<>();

                    // Filtrer les événements avec IDs de 7 à 9 pour les featured events
                    for (BackendEvent event : backendEvents) {
                        if (event.getId() != null && event.getId() >= 7 && event.getId() <= 9) {
                            featuredEvents.add(convertToFeaturedEvent(event));
                        }
                    }

                    if (featuredEvents.isEmpty()) {
                        callback.onDataNotAvailable("Aucun événement en vedette trouvé");
                    } else {
                        callback.onFeaturedEventsLoaded(featuredEvents);
                    }
                } else {
                    callback.onDataNotAvailable("Erreur lors du chargement des événements en vedette");
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                callback.onDataNotAvailable("Erreur de connexion");
            }
        });
    }

    // Méthode pour charger les événements tendance
    public void loadTrendingEvents(final EventsCallback callback) {
        apiService.getAllEvents().enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> backendEvents = response.body();
                    List<BackendEvent> filteredEvents = new ArrayList<>();

                    // Filtrer les événements avec IDs de 10 à 13 pour les trending events
                    for (BackendEvent event : backendEvents) {
                        if (event.getId() != null && event.getId() >= 10 && event.getId() <= 13) {
                            filteredEvents.add(event);
                        }
                    }

                    if (filteredEvents.isEmpty()) {
                        callback.onDataNotAvailable("Aucun événement tendance trouvé");
                    } else {
                        loadSchedulesForFilteredEvents(filteredEvents, new ArrayList<>(), callback);
                    }
                } else {
                    callback.onDataNotAvailable("Erreur lors du chargement des événements tendance");
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                callback.onDataNotAvailable("Erreur de connexion");
            }
        });
    }

    // Méthode pour charger les lieux des événements
    private void loadLieuxForEvents(final List<BackendEvent> backendEvents, final List<HomeActivity.Event> events, final EventsCallback callback) {
        apiService.getAllLieux().enqueue(new Callback<List<BackendLieu>>() {
            @Override
            public void onResponse(Call<List<BackendLieu>> call, Response<List<BackendLieu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendLieu> lieux = response.body();

                    // Charger les horaires pour obtenir les dates des événements
                    loadSchedulesForEvents(backendEvents, lieux, events, callback);
                } else {
                    // Si on ne peut pas charger les lieux, utiliser des valeurs par défaut
                    for (int i = 0; i < Math.min(4, backendEvents.size()); i++) {
                        events.add(convertToUiEvent(backendEvents.get(i), "Lieu inconnu", "Date inconnue"));
                    }
                    callback.onEventsLoaded(events);
                }
            }

            @Override
            public void onFailure(Call<List<BackendLieu>> call, Throwable t) {
                Log.e(TAG, "Erreur API lieux: " + t.getMessage());
                // Si on ne peut pas charger les lieux, utiliser des valeurs par défaut
                for (int i = 0; i < Math.min(4, backendEvents.size()); i++) {
                    events.add(convertToUiEvent(backendEvents.get(i), "Lieu inconnu", "Date inconnue"));
                }
                callback.onEventsLoaded(events);
            }
        });
    }

    // Méthode pour charger les horaires des événements
    private void loadSchedulesForEvents(final List<BackendEvent> backendEvents, final List<BackendLieu> lieux, final List<HomeActivity.Event> events, final EventsCallback callback) {
        apiService.getAllEventSchedules().enqueue(new Callback<List<BackendEventSchedule>>() {
            @Override
            public void onResponse(Call<List<BackendEventSchedule>> call, Response<List<BackendEventSchedule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEventSchedule> schedules = response.body();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy");

                    for (int i = 0; i < Math.min(4, backendEvents.size()); i++) {
                        BackendEvent event = backendEvents.get(i);
                        String venue = "Lieu à confirmer";
                        String date = "Prochainement";

                        // Trouver l'horaire correspondant à cet événement
                        for (BackendEventSchedule schedule : schedules) {
                            if (schedule.getEventId() != null && schedule.getEventId().equals(event.getId())) {
                                // Trouver le lieu correspondant à cet horaire
                                for (BackendLieu lieu : lieux) {
                                    if (lieu.getId() != null && lieu.getId().equals(schedule.getLieuId())) {
                                        venue = lieu.getName();
                                        break;
                                    }
                                }

                                // Formater la date avec l'année incluse
                                if (schedule.getDateTime() != null) {
                                    date = schedule.getDateTime().format(formatter);
                                }

                                break;
                            }
                        }

                        events.add(new HomeActivity.Event(
                                event.getId(),
                                event.getTitle(),
                                venue,
                                date,
                                R.drawable.event_1
                        ));
                    }

                    callback.onEventsLoaded(events);
                } else {
                    // Si on ne peut pas charger les horaires, utiliser des valeurs par défaut
                    for (int i = 0; i < Math.min(4, backendEvents.size()); i++) {
                        events.add(convertToUiEvent(backendEvents.get(i), "Lieu à confirmer", "Prochainement"));
                    }
                    callback.onEventsLoaded(events);
                }
            }

            @Override
            public void onFailure(Call<List<BackendEventSchedule>> call, Throwable t) {
                Log.e(TAG, "Erreur API schedules: " + t.getMessage());
                // Si on ne peut pas charger les horaires, utiliser des valeurs par défaut
                for (int i = 0; i < Math.min(4, backendEvents.size()); i++) {
                    events.add(convertToUiEvent(backendEvents.get(i), "Lieu à confirmer", "Prochainement"));
                }
                callback.onEventsLoaded(events);
            }
        });
    }

    // Méthode pour charger les événements à proximité
    public void loadNearbyEvents(final EventsCallback callback) {
        apiService.getAllEvents().enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> backendEvents = response.body();
                    List<BackendEvent> filteredEvents = new ArrayList<>();

                    // Filtrer les événements avec IDs de 14 à 16 pour les nearby events
                    for (BackendEvent event : backendEvents) {
                        if (event.getId() != null && event.getId() >= 14 && event.getId() <= 16) {
                            filteredEvents.add(event);
                        }
                    }

                    if (filteredEvents.isEmpty()) {
                        callback.onDataNotAvailable("Aucun événement à proximité trouvé");
                    } else {
                        loadSchedulesForFilteredEvents(filteredEvents, new ArrayList<>(), callback);
                    }
                } else {
                    callback.onDataNotAvailable("Erreur lors du chargement des événements à proximité");
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                callback.onDataNotAvailable("Erreur de connexion");
            }
        });
    }

    // Méthode pour charger les événements correspondant aux horaires
    private void loadEventsForSchedules(final List<BackendEventSchedule> schedules, final EventsCallback callback) {
        apiService.getAllEvents().enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> allEvents = response.body();
                    List<HomeActivity.Event> nearbyEvents = new ArrayList<>();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM");

                    // Associer les horaires aux événements
                    for (BackendEventSchedule schedule : schedules) {
                        for (BackendEvent event : allEvents) {
                            if (event.getId() != null && event.getId().equals(schedule.getEventId())) {
                                String date = schedule.getDateTime() != null ?
                                        schedule.getDateTime().format(formatter) : "Date inconnue";
                                String venue = schedule.getLieuName() != null ?
                                        schedule.getLieuName() : "Lieu inconnu";

                                // Créer l'événement avec l'ID
                                HomeActivity.Event uiEvent = new HomeActivity.Event(
                                        event.getId(),
                                        event.getTitle(),
                                        venue,
                                        date,
                                        R.drawable.event_1
                                );
                                nearbyEvents.add(uiEvent);
                                break;
                            }
                        }

                        // Limiter à 3 événements
                        if (nearbyEvents.size() >= 3) {
                            break;
                        }
                    }

                    callback.onEventsLoaded(nearbyEvents);
                } else {
                    callback.onDataNotAvailable("Erreur lors du chargement des événements");
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                callback.onDataNotAvailable("Erreur de connexion");
            }
        });
    }

    // Méthode pour charger les suggestions d'événements
    public void loadSuggestions(final EventsCallback callback) {
        apiService.getAllEvents().enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> backendEvents = response.body();
                    List<HomeActivity.Event> suggestions = new ArrayList<>();

                    // Prendre les événements à partir du 5ème comme suggestions
                    int startIndex = Math.min(4, backendEvents.size());
                    for (int i = startIndex; i < Math.min(startIndex + 3, backendEvents.size()); i++) {
                        BackendEvent event = backendEvents.get(i);
                        // Créer l'événement avec l'ID
                        suggestions.add(new HomeActivity.Event(
                                event.getId(),
                                event.getTitle()
                        ));
                    }

                    callback.onEventsLoaded(suggestions);
                } else {
                    callback.onDataNotAvailable("Erreur lors du chargement des suggestions");
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                callback.onDataNotAvailable("Erreur de connexion");
            }
        });
    }

    // Méthode pour filtrer les événements par catégorie
    public void filterEventsByCategory(String category, final EventsCallback callback) {
        // Convertir la chaîne de catégorie en enum CategoryEvent
        CategoryEvent categoryEnum;
        try {
            categoryEnum = CategoryEvent.valueOf(category);
        } catch (IllegalArgumentException e) {
            callback.onDataNotAvailable("Catégorie non reconnue: " + category);
            return;
        }

        apiService.getEventsByCategory(category).enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> backendEvents = response.body();
                    List<HomeActivity.Event> filteredEvents = new ArrayList<>();

                    // Charger les lieux et les horaires pour ces événements
                    loadLieuxForFilteredEvents(backendEvents, filteredEvents, callback);
                } else {
                    callback.onDataNotAvailable("Erreur lors du filtrage des événements");
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                callback.onDataNotAvailable("Erreur de connexion");
            }
        });
    }

    private void loadLieuxForFilteredEvents(List<BackendEvent> backendEvents, List<HomeActivity.Event> events, EventsCallback callback) {
        apiService.getAllLieux().enqueue(new Callback<List<BackendLieu>>() {
            @Override
            public void onResponse(Call<List<BackendLieu>> call, Response<List<BackendLieu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendLieu> lieux = response.body();
                    loadSchedulesForFilteredEvents(backendEvents, events, callback);
                } else {
                    // Si on ne peut pas charger les lieux, utiliser des valeurs par défaut
                    for (BackendEvent event : backendEvents) {
                        events.add(new HomeActivity.Event(
                                event.getId(),
                                event.getTitle(),
                                "Lieu à confirmer",
                                "Prochainement",
                                R.drawable.event_1
                        ));
                    }
                    callback.onEventsLoaded(events);
                }
            }

            @Override
            public void onFailure(Call<List<BackendLieu>> call, Throwable t) {
                Log.e(TAG, "Erreur API lieux: " + t.getMessage());
                // Si on ne peut pas charger les lieux, utiliser des valeurs par défaut
                for (BackendEvent event : backendEvents) {
                    events.add(new HomeActivity.Event(
                            event.getId(),
                            event.getTitle(),
                            "Lieu à confirmer",
                            "Prochainement",
                            R.drawable.event_1
                    ));
                }
                callback.onEventsLoaded(events);
            }
        });
    }

    private void loadSchedulesForFilteredEvents(List<BackendEvent> backendEvents, List<HomeActivity.Event> events, EventsCallback callback) {
        apiService.getAllEventSchedules().enqueue(new Callback<List<BackendEventSchedule>>() {
            @Override
            public void onResponse(Call<List<BackendEventSchedule>> call, Response<List<BackendEventSchedule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEventSchedule> schedules = response.body();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy");

                    for (BackendEvent event : backendEvents) {
                        String venue = "Lieu à confirmer";
                        String date = "Prochainement";

                        // Trouver le premier horaire correspondant à cet événement
                        for (BackendEventSchedule schedule : schedules) {
                            if (schedule.getEventId() != null && schedule.getEventId().equals(event.getId())) {
                                // Utiliser directement le nom du lieu depuis l'horaire
                                if (schedule.getLieuName() != null) {
                                    venue = schedule.getLieuName();
                                }

                                // Formater la date
                                if (schedule.getDateTime() != null) {
                                    date = schedule.getDateTime().format(formatter);
                                }

                                // Ne prendre que le premier horaire et sortir de la boucle
                                break;
                            }
                        }

                        events.add(new HomeActivity.Event(
                                event.getId(),
                                event.getTitle(),
                                venue,
                                date,
                                R.drawable.event_1
                        ));
                    }

                    callback.onEventsLoaded(events);
                } else {
                    // Si on ne peut pas charger les horaires, utiliser des valeurs par défaut
                    for (BackendEvent event : backendEvents) {
                        events.add(new HomeActivity.Event(
                                event.getId(),
                                event.getTitle(),
                                "Lieu à confirmer",
                                "Prochainement",
                                R.drawable.event_1
                        ));
                    }
                    callback.onEventsLoaded(events);
                }
            }

            @Override
            public void onFailure(Call<List<BackendEventSchedule>> call, Throwable t) {
                Log.e(TAG, "Erreur API schedules: " + t.getMessage());
                // Si on ne peut pas charger les horaires, utiliser des valeurs par défaut
                for (BackendEvent event : backendEvents) {
                    events.add(new HomeActivity.Event(
                            event.getId(),
                            event.getTitle(),
                            "Lieu à confirmer",
                            "Prochainement",
                            R.drawable.event_1
                    ));
                }
                callback.onEventsLoaded(events);
            }
        });
    }

    // Méthode pour charger les détails d'un événement
    public void loadEventDetails(Long eventId, final EventDetailCallback callback) {
        if (eventId == null || eventId <= 0) {
            callback.onDataNotAvailable("ID d'événement invalide");
            return;
        }

        Log.d(TAG, "Chargement des détails pour l'événement ID: " + eventId);

        apiService.getEventById(eventId).enqueue(new Callback<BackendEvent>() {
            @Override
            public void onResponse(Call<BackendEvent> call, Response<BackendEvent> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BackendEvent event = response.body();
                    Log.d(TAG, "Événement chargé avec succès: " + event.getTitle());
                    callback.onEventLoaded(event);
                } else {
                    Log.e(TAG, "Erreur API: " + response.code() + " - " + response.message());
                    callback.onDataNotAvailable("Erreur lors du chargement des détails de l'événement (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<BackendEvent> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                callback.onDataNotAvailable("Erreur de connexion: " + t.getMessage());
            }
        });
    }
    // Méthode pour charger les horaires d'un événement
    public void loadEventSchedules(Long eventId, final EventSchedulesCallback callback) {
        if (eventId == null || eventId <= 0) {
            callback.onDataNotAvailable("ID d'événement invalide");
            return;
        }

        Log.d(TAG, "Chargement des horaires pour l'événement ID: " + eventId);

        apiService.getEventSchedulesByEventId(eventId).enqueue(new Callback<List<BackendEventSchedule>>() {
            @Override
            public void onResponse(Call<List<BackendEventSchedule>> call, Response<List<BackendEventSchedule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEventSchedule> schedules = response.body();
                    Log.d(TAG, "Nombre d'horaires chargés: " + schedules.size());
                    callback.onSchedulesLoaded(schedules);
                } else {
                    Log.e(TAG, "Erreur API: " + response.code() + " - " + response.message());
                    callback.onDataNotAvailable("Erreur lors du chargement des horaires (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<BackendEventSchedule>> call, Throwable t) {
                Log.e(TAG, "Erreur API: " + t.getMessage());
                callback.onDataNotAvailable("Erreur de connexion: " + t.getMessage());
            }
        });
    }
    // Interfaces de callback
    public interface FeaturedEventsCallback {
        void onFeaturedEventsLoaded(List<HomeActivity.FeaturedEvent> featuredEvents);
        void onDataNotAvailable(String errorMessage);
    }

    public interface EventsCallback {
        void onEventsLoaded(List<HomeActivity.Event> events);
        void onDataNotAvailable(String errorMessage);
    }

    public interface EventDetailCallback {
        void onEventLoaded(BackendEvent event);
        void onDataNotAvailable(String errorMessage);
    }

    public interface EventSchedulesCallback {
        void onSchedulesLoaded(List<BackendEventSchedule> schedules);
        void onDataNotAvailable(String errorMessage);
    }
}