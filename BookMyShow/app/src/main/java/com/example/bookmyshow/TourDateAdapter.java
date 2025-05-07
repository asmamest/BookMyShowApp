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

public class TourDateAdapter extends RecyclerView.Adapter<TourDateAdapter.TourDateViewHolder> {

    private Context context;
    private List<TourDate> tourDates;
    private TourShow parentShow; // L'événement parent auquel appartiennent ces dates

    public TourDateAdapter(Context context, List<TourDate> tourDates, TourShow parentShow) {
        this.context = context;
        this.tourDates = tourDates;
        this.parentShow = parentShow;
    }

    public void setTourDates(List<TourDate> tourDates) {
        this.tourDates = tourDates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TourDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour_date, parent, false);
        return new TourDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TourDateViewHolder holder, int position) {
        TourDate tourDate = tourDates.get(position);
        holder.bind(tourDate, position);
    }

    @Override
    public int getItemCount() {
        return tourDates.size();
    }

    class TourDateViewHolder extends RecyclerView.ViewHolder {
        private TextView dayTextView;
        private TextView monthTextView;
        private TextView venueTextView;
        private TextView cityCountryTextView;
        private TextView timeTextView;
        private Button viewButton;

        public TourDateViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            monthTextView = itemView.findViewById(R.id.monthTextView);
            venueTextView = itemView.findViewById(R.id.venueTextView);
            cityCountryTextView = itemView.findViewById(R.id.cityCountryTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
        }

        // Dans TourDateAdapter.java, modifiez la méthode bind du ViewHolder
        void bind(TourDate tourDate, int position) {
            dayTextView.setText(tourDate.getDay());
            monthTextView.setText(tourDate.getMonth());
            venueTextView.setText(tourDate.getVenue());
            cityCountryTextView.setText(tourDate.getCity() + ", " + tourDate.getCountry());
            timeTextView.setText(tourDate.getTime());

            // Configurer le bouton "Voir" pour naviguer vers EventDetailActivity
            viewButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, EventDetailActivity.class);

                // Passer l'ID de l'événement et l'index de la date
                intent.putExtra("eventId", parentShow.getId());
                intent.putExtra("dateIndex", position);

                // Passer les informations communes de l'événement
                intent.putExtra("eventTitle", parentShow.getTitle());
                intent.putExtra("eventDescription", parentShow.getDescription());
                intent.putExtra("eventCategory", parentShow.getCategory());
                intent.putExtra("eventImageResId", parentShow.getImageResId());

                // Passer directement les informations de date et lieu spécifiques
                // Format de date complet pour l'affichage
                String formattedDate =
                        tourDate.getDay() + " " +
                                tourDate.getMonth() + " " +
                                tourDate.getYear() + " | " +
                                tourDate.getTime();

                // Format de lieu complet pour l'affichage
                String formattedLocation = tourDate.getVenue() + ", " +
                        tourDate.getCity() + ", " +
                        tourDate.getCountry();

                intent.putExtra("eventDate", formattedDate);
                intent.putExtra("eventLocation", formattedLocation);

                // Passer l'information sur la disponibilité (à remplacer par la vraie logique)
                // Pour l'exemple, on considère qu'un événement est complet aléatoirement
                boolean isSoldOut = Math.random() < 0.3; // 30% de chance d'être complet
                intent.putExtra("isSoldOut", isSoldOut);

                context.startActivity(intent);
            });
        }    }
}
