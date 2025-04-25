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

import java.util.List;

public class TourShowAdapter extends RecyclerView.Adapter<TourShowAdapter.ViewHolder> {

    private Context context;
    private List<TourShow> tourShows;
    private static final int MAX_DATES_TO_SHOW = 3; // Nombre maximum de dates à afficher dans la carte

    public TourShowAdapter(Context context, List<TourShow> tourShows) {
        this.context = context;
        this.tourShows = tourShows;
    }

    public void setTourShows(List<TourShow> tourShows) {
        this.tourShows = tourShows;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour_show, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TourShow show = tourShows.get(position);

        // Configurer les vues
        holder.showImageView.setImageResource(show.getImageResId());
        holder.showTitleTextView.setText(show.getTitle());
        holder.showCategoryTextView.setText(show.getCategory());
        holder.showDescriptionTextView.setText(show.getDescription());

        // Configurer le RecyclerView des dates
        List<TourDate> datesToShow = show.getTourDates();
        if (datesToShow.size() > MAX_DATES_TO_SHOW) {
            datesToShow = datesToShow.subList(0, MAX_DATES_TO_SHOW);
        }

        TourDateAdapter dateAdapter = new TourDateAdapter(context, datesToShow);
        holder.tourDatesRecyclerView.setAdapter(dateAdapter);

        // Configurer les boutons
        holder.viewAllDatesButton.setOnClickListener(v -> {
            if (context instanceof DiscoverActivity) {
                ((DiscoverActivity) context).showTourDatesDialog(show);
            }
        });

        holder.showDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event_id", show.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tourShows.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView showImageView;
        TextView showTitleTextView;
        TextView showCategoryTextView;
        TextView showDescriptionTextView;
        RecyclerView tourDatesRecyclerView;
        Button viewAllDatesButton;
        Button showDetailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showImageView = itemView.findViewById(R.id.showImageView);
            showTitleTextView = itemView.findViewById(R.id.showTitleTextView);
            showCategoryTextView = itemView.findViewById(R.id.showCategoryTextView);
            showDescriptionTextView = itemView.findViewById(R.id.showDescriptionTextView);
            tourDatesRecyclerView = itemView.findViewById(R.id.tourDatesRecyclerView);
            viewAllDatesButton = itemView.findViewById(R.id.viewAllDatesButton);
            showDetailsButton = itemView.findViewById(R.id.showDetailsButton);

            // Configurer le RecyclerView
            tourDatesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
