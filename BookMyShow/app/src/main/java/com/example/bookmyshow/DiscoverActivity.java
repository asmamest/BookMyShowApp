package com.example.bookmyshow;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import android.app.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DiscoverActivity extends AppCompatActivity {

    private RecyclerView tourShowsRecyclerView;
    private RecyclerView upcomingShowsRecyclerView;
    private TourShowAdapter tourShowAdapter;
    private TourShowAdapter upcomingShowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialiser les RecyclerViews
        setupRecyclerViews();

        // Configurer les filtres
        setupFilters();

        // Charger les données
        loadTourShows();
        loadUpcomingShows();
    }

    private void setupRecyclerViews() {
        // RecyclerView pour les spectacles en tournée
        tourShowsRecyclerView = findViewById(R.id.tourShowsRecyclerView);
        tourShowsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tourShowAdapter = new TourShowAdapter(this, new ArrayList<>());
        tourShowsRecyclerView.setAdapter(tourShowAdapter);

        // RecyclerView pour les spectacles à venir
        upcomingShowsRecyclerView = findViewById(R.id.upcomingShowsRecyclerView);
        upcomingShowsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        upcomingShowAdapter = new TourShowAdapter(this, new ArrayList<>());
        upcomingShowsRecyclerView.setAdapter(upcomingShowAdapter);
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
        // Implémenter la logique de filtrage ici
        // Cette méthode sera appelée lorsque les filtres changent
    }

    private void showMoreLocationsDialog() {
        // Afficher une boîte de dialogue avec plus d'options de localisation
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_more_locations, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadTourShows() {
        // Charger les données des spectacles en tournée (simulées ici)
        List<TourShow> tourShows = new ArrayList<>();

        // Exemple 1: "Le Misanthrope" avec plusieurs dates dans différentes villes
        TourShow show1 = new TourShow();
        show1.setId(1);
        show1.setTitle("Le Misanthrope");
        show1.setCategory("Théâtre");
        show1.setImageResId(R.drawable.event_1);
        show1.setDescription("Une pièce de Molière mise en scène par Thomas Jolly avec la Comédie-Française.");

        List<TourDate> dates1 = new ArrayList<>();
        dates1.add(new TourDate("15", "AVR", "2025", "Théâtre National", "Tunis", "Tunisie", "20:00"));
        dates1.add(new TourDate("18", "AVR", "2025", "Théâtre Municipal", "Tunis", "Tunisie", "19:30"));
        dates1.add(new TourDate("25", "AVR", "2025", "Comédie-Française", "Paris", "France", "20:00"));
        dates1.add(new TourDate("28", "AVR", "2025", "Théâtre du Châtelet", "Paris", "France", "19:00"));
        dates1.add(new TourDate("10", "MAI", "2025", "Théâtre Maisonneuve", "Montréal", "Canada", "19:30"));
        show1.setTourDates(dates1);

        // Exemple 2: "Carmen" avec plusieurs dates
        TourShow show2 = new TourShow();
        show2.setId(2);
        show2.setTitle("Carmen");
        show2.setCategory("Opéra");
        show2.setImageResId(R.drawable.event_2);
        show2.setDescription("L'opéra de Bizet dans une mise en scène moderne par Calixto Bieito.");

        List<TourDate> dates2 = new ArrayList<>();
        dates2.add(new TourDate("20", "AVR", "2025", "Opéra de Tunis", "Tunis", "Tunisie", "19:00"));
        dates2.add(new TourDate("05", "MAI", "2025", "Opéra Bastille", "Paris", "France", "19:30"));
        dates2.add(new TourDate("15", "MAI", "2025", "Place des Arts", "Montréal", "Canada", "20:00"));
        show2.setTourDates(dates2);

        // Exemple 3: "Cirque du Soleil: Alegría" avec plusieurs dates
        TourShow show3 = new TourShow();
        show3.setId(3);
        show3.setTitle("Cirque du Soleil: Alegría");
        show3.setCategory("Cirque");
        show3.setImageResId(R.drawable.event_2);
        show3.setDescription("Le spectacle emblématique du Cirque du Soleil revient avec une nouvelle vision.");

        List<TourDate> dates3 = new ArrayList<>();
        dates3.add(new TourDate("10", "AVR", "2025", "Parc des expositions", "Tunis", "Tunisie", "20:00"));
        dates3.add(new TourDate("12", "AVR", "2025", "Parc des expositions", "Tunis", "Tunisie", "15:00"));
        dates3.add(new TourDate("13",  "AVR", "2025", "Parc des expositions", "Tunis", "Tunisie", "15:00"));
        dates3.add(new TourDate("25", "AVR", "2025", "La Défense Arena", "Paris", "France", "20:00"));
        dates3.add(new TourDate("26", "AVR", "2025", "La Défense Arena", "Paris", "France", "15:00"));
        dates3.add(new TourDate("27", "AVR", "2025", "La Défense Arena", "Paris", "France", "20:00"));
        dates3.add(new TourDate("15", "MAI", "2025", "Centre Bell", "Montréal", "Canada", "19:30"));
        dates3.add(new TourDate("16", "MAI", "2025", "Centre Bell", "Montréal", "Canada", "19:30"));
        show3.setTourDates(dates3);

        tourShows.add(show1);
        tourShows.add(show2);
        tourShows.add(show3);

        tourShowAdapter.setTourShows(tourShows);
        tourShowAdapter.notifyDataSetChanged();
    }

    private void loadUpcomingShows() {
        // Charger les données des spectacles à venir (simulées ici)
        List<TourShow> upcomingShows = new ArrayList<>();

        // Exemple 1: "Hamlet" avec plusieurs dates
        TourShow show1 = new TourShow();
        show1.setId(4);
        show1.setTitle("Hamlet");
        show1.setCategory("Théâtre");
        show1.setImageResId(R.drawable.event_1);
        show1.setDescription("La tragédie de Shakespeare dans une mise en scène contemporaine.");

        List<TourDate> dates1 = new ArrayList<>();
        dates1.add(new TourDate("10", "JUN", "2025", "Théâtre National", "Tunis", "Tunisie", "20:00"));
        dates1.add(new TourDate("20", "JUN", "2025", "Odéon", "Paris", "France", "19:30"));
        dates1.add(new TourDate("05", "JUL", "2025", "Théâtre du Nouveau Monde", "Montréal", "Canada", "19:00"));
        show1.setTourDates(dates1);

        // Exemple 2: "Ballet National de Russie" avec plusieurs dates
        TourShow show2 = new TourShow();
        show2.setId(5);
        show2.setTitle("Ballet National de Russie");
        show2.setCategory("Danse");
        show2.setImageResId(R.drawable.event_2);
        show2.setDescription("Le Lac des Cygnes et Casse-Noisette par le Ballet National de Russie.");

        List<TourDate> dates2 = new ArrayList<>();
        dates2.add(new TourDate("15", "JUN", "2025", "Théâtre Municipal", "Tunis", "Tunisie", "19:00"));
        dates2.add(new TourDate("25", "JUN", "2025", "Opéra Garnier", "Paris", "France", "20:00"));
        dates2.add(new TourDate("10", "JUL", "2025", "Place des Arts", "Montréal", "Canada", "19:30"));
        show2.setTourDates(dates2);

        upcomingShows.add(show1);
        upcomingShows.add(show2);

        upcomingShowAdapter.setTourShows(upcomingShows);
        upcomingShowAdapter.notifyDataSetChanged();
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
        setupMonthTabs(monthTabLayout, show.getTourDates());

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

    private void setupMonthTabs(TabLayout tabLayout, List<TourDate> dates) {
        // Extraire les mois uniques des dates
        List<String> months = new ArrayList<>();
        for (TourDate date : dates) {
            if (!months.contains(date.getMonth())) {
                months.add(date.getMonth());
                tabLayout.addTab(tabLayout.newTab().setText(date.getMonth() + " " + date.getYear()));
            }
        }
    }

    private String getSelectedLocation(ChipGroup chipGroup) {
        int checkedChipId = chipGroup.getCheckedChipId();
        if (checkedChipId == R.id.dialogAllLocationsChip || checkedChipId == R.id.allLocationsChip) {
            return "all";
        } else if (checkedChipId == R.id.dialogTunisChip || checkedChipId == R.id.tunisChip) {
            return "Tunis";
        } else if (checkedChipId == R.id.dialogParisChip || checkedChipId == R.id.parisChip) {
            return "Paris";
        } else if (checkedChipId == R.id.dialogMontrealChip || checkedChipId == R.id.montrealChip) {
            return "Montréal";
        }
        return "all";
    }

    private void filterTourDates(TourDateAdapter adapter, List<TourDate> allDates, String location) {
        if (location.equals("all")) {
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
        adapter.notifyDataSetChanged();
    }

}