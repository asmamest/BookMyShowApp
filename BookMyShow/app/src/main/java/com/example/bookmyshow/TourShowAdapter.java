package com.example.bookmyshow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TourShowAdapter extends RecyclerView.Adapter<TourShowAdapter.TourShowViewHolder> {
    private static final int MAX_DATES_TO_SHOW = 4; // Limite à 4 dates initialement

    private Context context;
    private List<TourShow> tourShows;

    public TourShowAdapter(Context context, List<TourShow> tourShows) {
        this.context = context;
        this.tourShows = tourShows;
    }

    public void setTourShows(List<TourShow> tourShows) {
        this.tourShows = tourShows;
    }

    @NonNull
    @Override
    public TourShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour_show, parent, false);
        return new TourShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourShowViewHolder holder, int position) {
        TourShow tourShow = tourShows.get(position);
        holder.bind(tourShow);
    }

    @Override
    public int getItemCount() {
        return tourShows.size();
    }

    class TourShowViewHolder extends RecyclerView.ViewHolder {
        private ImageView showImageView;
        private TextView showTitleTextView;
        private TextView showCategoryTextView;
        private TextView showDescriptionTextView;
        private RecyclerView tourDatesRecyclerView;
        private Button viewAllDatesButton;
        private Button showDetailsButton;
        private TourDateAdapter tourDateAdapter;

        public TourShowViewHolder(@NonNull View itemView) {
            super(itemView);
            showImageView = itemView.findViewById(R.id.showImageView);
            showTitleTextView = itemView.findViewById(R.id.showTitleTextView);
            showCategoryTextView = itemView.findViewById(R.id.showCategoryTextView);
            showDescriptionTextView = itemView.findViewById(R.id.showDescriptionTextView);
            tourDatesRecyclerView = itemView.findViewById(R.id.tourDatesRecyclerView);
            viewAllDatesButton = itemView.findViewById(R.id.viewAllDatesButton);
            showDetailsButton = itemView.findViewById(R.id.showDetailsButton);
        }

        void bind(TourShow tourShow) {
            showTitleTextView.setText(tourShow.getTitle());
            showCategoryTextView.setText(tourShow.getCategory());
            showDescriptionTextView.setText(tourShow.getDescription());

            // Charger l'image
            if (tourShow.getImageResId() != 0) {
                showImageView.setImageResource(tourShow.getImageResId());
            }
            // Préparer la liste des dates (limitée à MAX_DATES_TO_SHOW)
            List<TourDate> datesToShow = tourShow.getTourDates();
            boolean shouldShowMoreButton = datesToShow.size() > MAX_DATES_TO_SHOW;

            if (shouldShowMoreButton) {
                datesToShow = datesToShow.subList(0, MAX_DATES_TO_SHOW);
            }

            // Configurer l'adaptateur avec les dates limitées
            TourDateAdapter tourDateAdapter = new TourDateAdapter(context, datesToShow, tourShow);
            tourDatesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            tourDatesRecyclerView.setAdapter(tourDateAdapter);


            // Configurer le bouton "Voir toutes les dates"
            viewAllDatesButton.setOnClickListener(v -> {
                if (context instanceof DiscoverActivity) {
                    ((DiscoverActivity) context).showTourDatesDialog(tourShow);
                }
            });

            // Configurer le bouton "Détails du spectacle"
            showDetailsButton.setOnClickListener(v -> {
                // Naviguer vers EventDetailActivity avec l'ID de l'événement
                // mais sans spécifier de date particulière
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("eventId", tourShow.getId());
                context.startActivity(intent);
            });
        }
    }
}
