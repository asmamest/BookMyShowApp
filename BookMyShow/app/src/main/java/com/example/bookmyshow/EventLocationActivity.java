package com.example.bookmyshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class EventLocationActivity extends AppCompatActivity {

    private MapView map;
    private String locationName;
    private String mapPosition; // Format: "latitude,longitude"
    private EditText startLocationEditText;
    private RadioGroup transportModeRadioGroup;
    private Button getDirectionsButton;
    private TextView locationNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuration d'OSMDroid
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_event_location);

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        locationName = intent.getStringExtra("locationName");
        mapPosition = intent.getStringExtra("mapPosition");

        // Initialiser les vues
        setupToolbar();
        initViews();
        setupMap();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Lieu de l'événement");
    }

    private void initViews() {
        startLocationEditText = findViewById(R.id.startLocationEditText);
        transportModeRadioGroup = findViewById(R.id.transportModeRadioGroup);
        getDirectionsButton = findViewById(R.id.itineraryButton);        locationNameTextView = findViewById(R.id.locationNameTextView);
        map = findViewById(R.id.map);

        // Définir le nom du lieu
        locationNameTextView.setText(locationName);
    }

    private void setupMap() {
        // Configuration de base de la carte
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Extraire les coordonnées du lieu
        try {
            String[] coordinates = mapPosition.split(",");
            double latitude = Double.parseDouble(coordinates[0]);
            double longitude = Double.parseDouble(coordinates[1]);

            GeoPoint eventLocation = new GeoPoint(latitude, longitude);

            // Contrôleur de carte pour le zoom et le centrage
            IMapController mapController = map.getController();
            mapController.setZoom(16.0);
            mapController.setCenter(eventLocation);

            // Ajouter un marqueur sur le lieu de l'événement
            Marker marker = new Marker(map);
            marker.setPosition(eventLocation);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(locationName);
            map.getOverlays().add(marker);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors du chargement de la carte", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        getDirectionsButton.setOnClickListener(v -> {
            String startLocation = startLocationEditText.getText().toString().trim();
            if (startLocation.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer une adresse de départ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Déterminer le mode de transport
            String transportMode = "driving"; // Par défaut
            int selectedId = transportModeRadioGroup.getCheckedRadioButtonId();
            if (selectedId == R.id.walkingRadioButton) {
                transportMode = "walking";
            }

            // Ouvrir une application de navigation externe avec l'itinéraire
            openExternalNavigationApp(startLocation, mapPosition, transportMode);
        });
    }

    private void openExternalNavigationApp(String startLocation, String destination, String transportMode) {
        // Extraire les coordonnées de destination
        try {
            String[] coordinates = destination.split(",");
            double latitude = Double.parseDouble(coordinates[0]);
            double longitude = Double.parseDouble(coordinates[1]);

            // Essayer d'abord avec OsmAnd (application OSM populaire)
            Uri osmandUri = Uri.parse("osmand.navigation:q=" + latitude + "," + longitude);
            Intent osmandIntent = new Intent(Intent.ACTION_VIEW, osmandUri);

            // Si OsmAnd n'est pas installé, essayer avec Google Maps
            if (osmandIntent.resolveActivity(getPackageManager()) == null) {
                // Format pour Google Maps: "google.navigation:q=latitude,longitude&mode=d|w"
                String modeParam = transportMode.equals("walking") ? "w" : "d";
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination + "&mode=" + modeParam);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Si aucune application n'est disponible, ouvrir dans le navigateur
                    Uri webUri = Uri.parse("https://www.openstreetmap.org/directions?from=" +
                            Uri.encode(startLocation) + "&to=" + latitude + "," + longitude);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                    startActivity(webIntent);
                }
            } else {
                startActivity(osmandIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'ouverture de l'itinéraire", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Important pour OSMDroid
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Important pour OSMDroid
        map.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}