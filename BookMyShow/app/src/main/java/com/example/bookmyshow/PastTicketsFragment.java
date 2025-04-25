package com.example.bookmyshow;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class PastTicketsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView pastTicketsRecyclerView;
    private LinearLayout emptyStateLayout;
    private EditText searchEditText;

    private TicketAdapter pastTicketsAdapter;

    private List<Ticket> allPastTickets = new ArrayList<>();
    private List<Ticket> filteredPastTickets = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_tickets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser les vues
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        pastTicketsRecyclerView = view.findViewById(R.id.pastTicketsRecyclerView);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        searchEditText = view.findViewById(R.id.searchEditText);

        // Configurer le RecyclerView
        setupRecyclerView();

        // Configurer le SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::refreshTickets);

        // Configurer la recherche
        setupSearch();

        // Charger les données
        loadTickets();
    }

    private void setupRecyclerView() {
        pastTicketsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pastTicketsAdapter = new TicketAdapter(getContext(), filteredPastTickets, false);
        pastTicketsRecyclerView.setAdapter(pastTicketsAdapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Non utilisé
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrer les billets en fonction du texte de recherche
                filterTicketsBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Non utilisé
            }
        });
    }

    private void filterTicketsBySearch(String query) {
        filteredPastTickets.clear();

        if (query.isEmpty()) {
            // Si la recherche est vide, afficher tous les billets (filtrés par catégorie, date, lieu)
            for (Ticket ticket : allPastTickets) {
                if (shouldIncludeTicket(ticket, new ArrayList<>(), "all", "all")) {
                    filteredPastTickets.add(ticket);
                }
            }
        } else {
            // Sinon, filtrer par le texte de recherche
            for (Ticket ticket : allPastTickets) {
                if (ticket.getEventTitle().toLowerCase().contains(query.toLowerCase()) ||
                        ticket.getVenue().toLowerCase().contains(query.toLowerCase())) {
                    if (shouldIncludeTicket(ticket, new ArrayList<>(), "all", "all")) {
                        filteredPastTickets.add(ticket);
                    }
                }
            }
        }

        pastTicketsAdapter.notifyDataSetChanged();
        checkIfTicketsExist();
    }

    private void loadTickets() {
        // Charger les données des billets passés (simulées ici)
        allPastTickets.clear();

        Ticket ticket1 = new Ticket();
        ticket1.setId(5);
        ticket1.setEventTitle("Hamlet");
        ticket1.setEventCategory("Théâtre");
        ticket1.setImageResId(R.drawable.event_5);
        ticket1.setDay("10");
        ticket1.setMonth("MAR");
        ticket1.setYear("2025");
        ticket1.setTime("19:30");
        ticket1.setVenue("Théâtre National");
        ticket1.setAddress("Avenue Habib Bourguiba, Tunis");
        ticket1.setSeatInfo("Orchestre, Rangée 7, Siège 9");
        ticket1.setExpired(true);

        Ticket ticket2 = new Ticket();
        ticket2.setId(6);
        ticket2.setEventTitle("Concert Symphonique");
        ticket2.setEventCategory("Concert");
        ticket2.setImageResId(R.drawable.event_1);
        ticket2.setDay("25");
        ticket2.setMonth("FEV");
        ticket2.setYear("2025");
        ticket2.setTime("20:00");
        ticket2.setVenue("Cité de la Culture");
        ticket2.setAddress("Avenue Mohamed V, Tunis");
        ticket2.setSeatInfo("Parterre, Rangée 10, Siège 15");
        ticket2.setExpired(true);

        Ticket ticket3 = new Ticket();
        ticket3.setId(7);
        ticket3.setEventTitle("La Traviata");
        ticket3.setEventCategory("Opéra");
        ticket3.setImageResId(R.drawable.event_2);
        ticket3.setDay("15");
        ticket3.setMonth("JAN");
        ticket3.setYear("2025");
        ticket3.setTime("19:00");
        ticket3.setVenue("Opéra de Tunis");
        ticket3.setAddress("Rue de Marseille, Tunis");
        ticket3.setSeatInfo("Loge, Rangée 1, Siège 3");
        ticket3.setExpired(true);

        allPastTickets.add(ticket1);
        allPastTickets.add(ticket2);
        allPastTickets.add(ticket3);

        // Appliquer les filtres initiaux (tous les billets)
        filteredPastTickets.clear();
        filteredPastTickets.addAll(allPastTickets);

        // Mettre à jour l'adaptateur
        pastTicketsAdapter.notifyDataSetChanged();

        // Vérifier s'il y a des billets à afficher
        checkIfTicketsExist();

        // Arrêter l'animation de rafraîchissement si elle est en cours
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refreshTickets() {
        // Rafraîchir les données des billets
        // Dans une application réelle, vous feriez une requête au serveur ici
        loadTickets();
    }

    // Changed from private to public to allow access from TicketAdapter
    public void checkIfTicketsExist() {
        // Vérifier s'il y a des billets à afficher
        if (filteredPastTickets.isEmpty()) {
            // Aucun billet à afficher
            pastTicketsRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            // Il y a des billets à afficher
            pastTicketsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    public void applyFilters(List<String> categories, String dateFilter, String venue) {
        // Appliquer les filtres aux billets

        filteredPastTickets.clear();
        for (Ticket ticket : allPastTickets) {
            if (shouldIncludeTicket(ticket, categories, dateFilter, venue)) {
                filteredPastTickets.add(ticket);
            }
        }

        // Mettre à jour l'adaptateur
        pastTicketsAdapter.notifyDataSetChanged();

        // Vérifier s'il y a des billets à afficher
        checkIfTicketsExist();
    }

    private boolean shouldIncludeTicket(Ticket ticket, List<String> categories, String dateFilter, String venue) {
        // Vérifier si le billet correspond aux filtres

        // Filtre par catégorie
        if (!categories.isEmpty() && !categories.contains(ticket.getEventCategory())) {
            return false;
        }

        // Filtre par date
        // (à implémenter selon la logique de votre application)

        // Filtre par lieu
        if (!venue.equals("all") && !ticket.getVenue().equals(venue)) {
            return false;
        }

        return true;
    }
}