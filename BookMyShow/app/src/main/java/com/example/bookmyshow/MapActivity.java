package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.bookmyshow.utils.LocationManager;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private Map<Marker, Event> markerEventMap = new HashMap<>();
    private CardView eventInfoCard;
    private ImageView eventImageView;
    private TextView eventTitleTextView;
    private TextView eventVenueTextView;
    private TextView eventDateTextView;
    private Button viewDetailsButton;
    private TextView listViewButton;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialiser la configuration OSMDroid
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // Initialiser les vues
        initViews();
        locationManager = new LocationManager(this);

        // Configurer la carte
        setupMap();

        // Ajouter les marqueurs d'événements
        addEventMarkers();

        // Configurer les écouteurs de clics
        setupClickListeners();
    }

    private void initViews() {
        mapView = findViewById(R.id.mapView);
        eventInfoCard = findViewById(R.id.eventInfoCard);
        eventImageView = findViewById(R.id.eventImageView);
        eventTitleTextView = findViewById(R.id.eventTitleTextView);
        eventVenueTextView = findViewById(R.id.eventVenueTextView);
        eventDateTextView = findViewById(R.id.eventDateTextView);
        viewDetailsButton = findViewById(R.id.viewDetailsButton);
        listViewButton = findViewById(R.id.listViewButton);

        // Configurer la toolbar si elle existe
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        }
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Utiliser la localisation enregistrée ou l'Afrique par défaut
        GeoPoint startPoint;
        if (locationManager.hasCustomLocation()) {
            startPoint = new GeoPoint(locationManager.getLatitude(), locationManager.getLongitude());
            mapView.getController().setZoom(12.0);
        } else {
            // Centre de l'Afrique
            startPoint = new GeoPoint(9.1450, 18.4283);
            mapView.getController().setZoom(4.0);

            // Afficher un message pour sélectionner une localisation
            Toast.makeText(this, "Sélectionnez une localisation pour voir les événements à proximité", Toast.LENGTH_LONG).show();
        }

        mapView.getController().setCenter(startPoint);
    }
    private void setupClickListeners() {
        viewDetailsButton.setOnClickListener(v -> {
            // Récupérer l'événement actuellement affiché
            // et passer ses informations à l'activité de détail
            Intent intent = new Intent(MapActivity.this, EventDetailActivity.class);
            // Vous pouvez ajouter des extras à l'intent ici
            startActivity(intent);
        });

        listViewButton.setOnClickListener(v -> {
            finish();
        });

        mapView.setOnTouchListener((v, event) -> {
            eventInfoCard.setVisibility(View.GONE);
            return false;
        });
    }

    private void addEventMarkers() {
        List<Event> events = getEventsList();

        for (Event event : events) {
            addEventMarker(event);
        }
    }

    private void addEventMarker(Event event) {
        Marker marker = new Marker(mapView);
        marker.setPosition(event.getLocation());
        marker.setTitle(event.getTitle());
        marker.setSnippet(event.getVenue());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // Définir l'icône du marqueur
        marker.setIcon(getResources().getDrawable(R.drawable.ic_map_marker));

        // Configurer le listener de clic sur le marqueur
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            showEventInfo(event);
            return true;
        });

        mapView.getOverlays().add(marker);
        markerEventMap.put(marker, event);
    }

    private void showEventInfo(Event event) {
        eventImageView.setImageResource(event.getImageResId());
        eventTitleTextView.setText(event.getTitle());
        eventVenueTextView.setText(event.getVenue());
        eventDateTextView.setText(event.getDate());
        eventInfoCard.setVisibility(View.VISIBLE);
    }

    // Méthode pour obtenir la liste des événements (simulée)
    private List<Event> getEventsList() {
        // Si aucune localisation n'est sélectionnée, retourner une liste vide
        if (!locationManager.hasCustomLocation()) {
            return new ArrayList<>();
        }

        // Sinon, charger les événements à proximité de la localisation sélectionnée
        double latitude = locationManager.getLatitude();
        double longitude = locationManager.getLongitude();
        int radius = locationManager.getRadius();

        // Ici, vous devriez appeler votre API pour obtenir les événements à proximité
        // Pour l'instant, nous allons simuler des événements autour de la localisation
        List<Event> events = new ArrayList<>();

        events.add(new Event(
                "Rock Concert",
                "Grand Arena",
                "Apr 15",
                R.drawable.event_1,
                "Live rock music festival featuring top bands",
                new GeoPoint(latitude + 0.01, longitude + 0.01)
        ));

        events.add(new Event(
                "Jazz Night",
                "Blue Note Club",
                "Apr 18",
                R.drawable.event_2,
                "Smooth jazz evening with renowned musicians",
                new GeoPoint(latitude - 0.01, longitude - 0.01)
        ));

        events.add(new Event(
                "Theatre Play",
                "City Theatre",
                "Apr 20",
                R.drawable.event_2,
                "Award-winning drama performance",
                new GeoPoint(latitude + 0.02, longitude - 0.02)
        ));

        events.add(new Event(
                "DJ Party",
                "Skyline Club",
                "Apr 22",
                R.drawable.event_1,
                "Electronic music night with international DJs",
                new GeoPoint(latitude - 0.02, longitude + 0.02)
        ));

        return events;
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // Classe Event complète avec toutes les méthodes nécessaires
    static class Event {
        private String title;
        private String venue;
        private String date;
        private int imageResId;
        private String description;
        private GeoPoint location;

        public Event(String title, String venue, String date, int imageResId, String description, GeoPoint location) {
            this.title = title;
            this.venue = venue;
            this.date = date;
            this.imageResId = imageResId;
            this.description = description;
            this.location = location;
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

        public String getDescription() {
            return description;
        }

        public GeoPoint getLocation() {
            return location;
        }

        public int getImageResource() {
            return imageResId;
        }
    }
}