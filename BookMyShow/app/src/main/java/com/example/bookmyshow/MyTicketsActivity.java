package com.example.bookmyshow;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private LinearLayout emptyStateLayout;
    private FloatingActionButton filterFab;

    private List<String> selectedCategories = new ArrayList<>();
    private String selectedDateFilter = "all";
    private String selectedVenue = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        filterFab = findViewById(R.id.filterFab);

        setupViewPager();

        Button exploreShowsButton = findViewById(R.id.exploreShowsButton);
        exploreShowsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DiscoverActivity.class);
            startActivity(intent);
        });

        filterFab.setOnClickListener(v -> showFilterDialog());

        checkIfTicketsExist();
    }

    private void setupViewPager() {
        TicketsPagerAdapter pagerAdapter = new TicketsPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("À venir");
            } else {
                tab.setText("Passés");
            }
        }).attach();
    }

    private void checkIfTicketsExist() {
        // Cette méthode vérifie s'il y a des billets à afficher
        // Si non, elle affiche l'état vide

        // Simuler la vérification (à remplacer par une vraie vérification)
        boolean hasTickets = true;

        if (hasTickets) {
            viewPager.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_filter_tickets, null);
        builder.setView(view);

        ChipGroup categoryChipGroup = view.findViewById(R.id.categoryChipGroup);
        Spinner venueSpinner = view.findViewById(R.id.venueSpinner);
        Button resetFiltersButton = view.findViewById(R.id.resetFiltersButton);
        Button applyFiltersButton = view.findViewById(R.id.applyFiltersButton);

        List<String> venues = new ArrayList<>();
        venues.add("Tous les lieux");
        venues.add("Théâtre National");
        venues.add("Opéra de Tunis");
        venues.add("Comédie-Française");
        venues.add("Théâtre du Châtelet");
        venues.add("Théâtre Maisonneuve");
        venues.add("Centre Bell");

        ArrayAdapter<String> venueAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, venues);
        venueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        venueSpinner.setAdapter(venueAdapter);

        // Sélectionner les filtres actuels
        // (à implémenter selon la logique de votre application)

        AlertDialog dialog = builder.create();

        resetFiltersButton.setOnClickListener(v -> {
            selectedCategories.clear();
            selectedDateFilter = "all";
            selectedVenue = "all";

            // Mettre à jour l'interface utilisateur
            applyFilters();

            dialog.dismiss();
        });

        applyFiltersButton.setOnClickListener(v -> {
            // Récupérer les filtres sélectionnés
            // (à implémenter selon la logique de votre application)

            // Appliquer les filtres
            applyFilters();

            dialog.dismiss();
        });

        dialog.show();
    }

    private void applyFilters() {
        // Appliquer les filtres aux fragments
        UpcomingTicketsFragment upcomingFragment =
                (UpcomingTicketsFragment) getSupportFragmentManager()
                        .findFragmentByTag("f0");

        PastTicketsFragment pastFragment =
                (PastTicketsFragment) getSupportFragmentManager()
                        .findFragmentByTag("f1");

        if (upcomingFragment != null) {
            upcomingFragment.applyFilters(selectedCategories, selectedDateFilter, selectedVenue);
        }

        if (pastFragment != null) {
            pastFragment.applyFilters(selectedCategories, selectedDateFilter, selectedVenue);
        }
    }

    private class TicketsPagerAdapter extends FragmentStateAdapter {
        public TicketsPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new UpcomingTicketsFragment();
            } else {
                return new PastTicketsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}