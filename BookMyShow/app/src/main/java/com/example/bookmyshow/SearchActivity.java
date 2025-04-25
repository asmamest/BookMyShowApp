package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmyshow.api.ApiClient;
import com.example.bookmyshow.api.ApiService;
import com.example.bookmyshow.models.BackendEvent;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private EditText searchEditText;
    private RecyclerView resultsRecyclerView;
    private View noResultsView;
    private ApiService apiService;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialiser l'API
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialiser les vues
        setupToolbar();
        initViews();
        setupSearchListener();

        // Récupérer la requête de recherche initiale si elle existe
        String initialQuery = getIntent().getStringExtra("query");
        if (initialQuery != null && !initialQuery.isEmpty()) {
            searchEditText.setText(initialQuery);
            performSearch(initialQuery);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recherche");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        searchEditText = findViewById(R.id.searchEditText);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        noResultsView = findViewById(R.id.noResultsView);

        // Configurer le RecyclerView
        adapter = new SearchAdapter(new ArrayList<>());
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(adapter);
    }

    private void setupSearchListener() {
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
                    performSearch(s.toString());
                } else if (s.length() == 0) {
                    adapter.setEvents(new ArrayList<>());
                    showNoResults(false);
                }
            }
        });
    }

    private void performSearch(String query) {
        apiService.searchEventsByTitle(query).enqueue(new Callback<List<BackendEvent>>() {
            @Override
            public void onResponse(Call<List<BackendEvent>> call, Response<List<BackendEvent>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BackendEvent> events = response.body();
                    adapter.setEvents(events);
                    showNoResults(events.isEmpty());
                } else {
                    adapter.setEvents(new ArrayList<>());
                    showNoResults(true);
                }
            }

            @Override
            public void onFailure(Call<List<BackendEvent>> call, Throwable t) {
                Log.e(TAG, "Erreur de recherche", t);
                Toast.makeText(SearchActivity.this, "Erreur de recherche", Toast.LENGTH_SHORT).show();
                adapter.setEvents(new ArrayList<>());
                showNoResults(true);
            }
        });
    }

    private void showNoResults(boolean show) {
        noResultsView.setVisibility(show ? View.VISIBLE : View.GONE);
        resultsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

        private List<BackendEvent> events;

        public SearchAdapter(List<BackendEvent> events) {
            this.events = events;
        }

        public void setEvents(List<BackendEvent> events) {
            this.events = events;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BackendEvent event = events.get(position);
            holder.titleTextView.setText(event.getTitle());
            holder.categoryTextView.setText(event.getCategory() != null ? event.getCategory().toString() : "");

            // Charger l'image de l'événement si disponible
            if (event.getEventImg() != null && !event.getEventImg().isEmpty()) {
                // Utiliser Glide pour charger l'image
                // Glide.with(holder.itemView.getContext())
                //     .load(event.getEventImg())
                //     .placeholder(R.drawable.event_1)
                //     .error(R.drawable.event_1)
                //     .into(holder.imageView);
                holder.imageView.setImageResource(R.drawable.event_1);
            } else {
                holder.imageView.setImageResource(R.drawable.event_1);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(SearchActivity.this, EventDetailActivity.class);
                intent.putExtra("eventId", event.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView titleTextView;
            TextView categoryTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.eventImageView);
                titleTextView = itemView.findViewById(R.id.eventTitleTextView);
                categoryTextView = itemView.findViewById(R.id.eventCategoryTextView);
            }
        }
    }
}