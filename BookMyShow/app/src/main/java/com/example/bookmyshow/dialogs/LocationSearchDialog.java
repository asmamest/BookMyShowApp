// LocationSearchDialog.java
package com.example.bookmyshow.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmyshow.R;
import com.example.bookmyshow.api.ApiClient;
import com.example.bookmyshow.api.ApiService;
import com.example.bookmyshow.models.BackendLieu;
import com.example.bookmyshow.models.GeoPoint;
import com.example.bookmyshow.utils.LocationManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationSearchDialog extends Dialog {

    private EditText searchEditText;
    private RecyclerView resultsRecyclerView;
    private SeekBar radiusSeekBar;
    private TextView radiusValueTextView;
    private Button cancelButton;
    private Button resetButton;
    private LocationAdapter adapter;
    private LocationManager locationManager;
    private ApiService apiService;
    private LocationSelectedListener listener;

    public interface LocationSelectedListener {
        void onLocationSelected(String locationName, double latitude, double longitude, int radius);
    }

    public LocationSearchDialog(@NonNull Context context, LocationManager locationManager, LocationSelectedListener listener) {
        super(context);
        this.locationManager = locationManager;
        this.listener = listener;
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_location_search);

        // Initialiser les vues
        searchEditText = findViewById(R.id.searchEditText);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        radiusSeekBar = findViewById(R.id.radiusSeekBar);
        radiusValueTextView = findViewById(R.id.radiusValueTextView);
        cancelButton = findViewById(R.id.cancelButton);
        resetButton = findViewById(R.id.resetButton);

        // Configurer le RecyclerView
        adapter = new LocationAdapter(new ArrayList<>());
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRecyclerView.setAdapter(adapter);

        // Configurer le SeekBar
        radiusSeekBar.setMax(100); // 100 km max
        radiusSeekBar.setProgress(locationManager.getRadius());
        updateRadiusText(locationManager.getRadius());

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateRadiusText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Configurer la recherche
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 3) {
                    searchLocations(s.toString());
                }
            }
        });

        // Configurer les boutons
        cancelButton.setOnClickListener(v -> dismiss());

        resetButton.setOnClickListener(v -> {
            locationManager.resetLocation();
            if (listener != null) {
                listener.onLocationSelected(
                        locationManager.getLocationName(),
                        locationManager.getLatitude(),
                        locationManager.getLongitude(),
                        0
                );
            }
            dismiss();
        });
    }

    private void updateRadiusText(int radius) {
        radiusValueTextView.setText(radius + " km");
    }

    private void searchLocations(String query) {
        apiService.searchLieuxByName(query).enqueue(new Callback<List<BackendLieu>>() {
            @Override
            public void onResponse(Call<List<BackendLieu>> call, Response<List<BackendLieu>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setLocations(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<BackendLieu>> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur de recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

        private List<BackendLieu> locations;

        public LocationAdapter(List<BackendLieu> locations) {
            this.locations = locations;
        }

        public void setLocations(List<BackendLieu> locations) {
            this.locations = locations;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BackendLieu location = locations.get(position);
            holder.textView.setText(location.getName());

            holder.itemView.setOnClickListener(v -> {
                GeoPoint geoPoint = location.getMapPosition();
                if (geoPoint != null) {
                    int radius = radiusSeekBar.getProgress();
                    locationManager.saveLocation(location.getName(), geoPoint.getLatitude(), geoPoint.getLongitude());
                    locationManager.saveRadius(radius);

                    if (listener != null) {
                        listener.onLocationSelected(
                                location.getName(),
                                geoPoint.getLatitude(),
                                geoPoint.getLongitude(),
                                radius
                        );
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Coordonnées non disponibles", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return locations.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}