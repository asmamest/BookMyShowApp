package com.example.bookmyshow;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TourDateAdapter extends RecyclerView.Adapter<TourDateAdapter.ViewHolder> {

    private Context context;
    private List<TourDate> tourDates;

    public TourDateAdapter(Context context, List<TourDate> tourDates) {
        this.context = context;
        this.tourDates = tourDates;
    }

    public void setTourDates(List<TourDate> tourDates) {
        this.tourDates = tourDates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TourDate date = tourDates.get(position);

        // Configurer les vues
        holder.dayTextView.setText(date.getDay());
        holder.monthTextView.setText(date.getMonth());
        holder.venueTextView.setText(date.getVenue());
        holder.cityTextView.setText(date.getCity() + ", " + date.getCountry());
        holder.timeTextView.setText(date.getTime());

        // Configurer le bouton de réservation
        holder.bookButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentActivity.class);
            // Ajouter les informations nécessaires pour la réservation
            intent.putExtra("venue", date.getVenue());
            intent.putExtra("city", date.getCity());
            intent.putExtra("date", date.getDay() + " " + date.getMonth() + " " + date.getYear());
            intent.putExtra("time", date.getTime());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tourDates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        TextView monthTextView;
        TextView venueTextView;
        TextView cityTextView;
        TextView timeTextView;
        Button bookButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            monthTextView = itemView.findViewById(R.id.monthTextView);
            venueTextView = itemView.findViewById(R.id.venueTextView);
            cityTextView = itemView.findViewById(R.id.cityTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            bookButton = itemView.findViewById(R.id.bookButton);
        }
    }
}