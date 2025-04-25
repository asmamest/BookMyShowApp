package com.example.bookmyshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class UpcomingTicketsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView soonTicketsRecyclerView;
    private RecyclerView laterTicketsRecyclerView;
    private LinearLayout emptyStateLayout;
    private TextView soonHeaderTextView;
    private TextView laterHeaderTextView;

    /*private TicketAdapter soonTicketsAdapter;
    private TicketAdapter laterTicketsAdapter;*/

    private List<Ticket> allSoonTickets = new ArrayList<>();
    private List<Ticket> allLaterTickets = new ArrayList<>();
    private List<Ticket> filteredSoonTickets = new ArrayList<>();
    private List<Ticket> filteredLaterTickets = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upcoming_tickets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser les vues
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        soonTicketsRecyclerView = view.findViewById(R.id.soonTicketsRecyclerView);
        laterTicketsRecyclerView = view.findViewById(R.id.laterTicketsRecyclerView);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        soonHeaderTextView = view.findViewById(R.id.soonHeaderTextView);
        laterHeaderTextView = view.findViewById(R.id.laterHeaderTextView);

        // Configurer les RecyclerViews
        setupRecyclerViews();

        // Configurer le SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::refreshTickets);

        // Charger les données
        loadTickets();
    }

    private void setupRecyclerViews() {
        // RecyclerView pour les billets à venir bientôt
        soonTicketsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //soonTicketsAdapter = new TicketAdapter(getContext(), filteredSoonTickets, true);
        //soonTicketsRecyclerView.setAdapter(soonTicketsAdapter);

        // RecyclerView pour les billets à venir plus tard
        laterTicketsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //laterTicketsAdapter = new TicketAdapter(getContext(), filteredLaterTickets, true);
       // laterTicketsRecyclerView.setAdapter(laterTicketsAdapter);
    }

    private void loadTickets() {
        // Charger les données des billets (simulées ici)

        // Billets à venir bientôt (dans les 7 prochains jours)
        allSoonTickets.clear();

        Ticket ticket1 = new Ticket();
        ticket1.setId(1);
        ticket1.setEventTitle("Le Misanthrope");
        ticket1.setEventCategory("Théâtre");
        ticket1.setImageResId(R.drawable.event_1);
        ticket1.setDay("15");
        ticket1.setMonth("AVR");
        ticket1.setYear("2025");
        ticket1.setTime("20:00");
        ticket1.setVenue("Théâtre National");
        ticket1.setAddress("Avenue Habib Bourguiba, Tunis");
        ticket1.setSeatInfo("Orchestre, Rangée 5, Siège 12");
        ticket1.setCountdown("Dans 3 jours");
        ticket1.setExpired(false);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2);
        ticket2.setEventTitle("Carmen");
        ticket2.setEventCategory("Opéra");
        ticket2.setImageResId(R.drawable.event_2);
        ticket2.setDay("20");
        ticket2.setMonth("AVR");
        ticket2.setYear("2025");
        ticket2.setTime("19:00");
        ticket2.setVenue("Opéra de Tunis");
        ticket2.setAddress("Rue de Marseille, Tunis");
        ticket2.setSeatInfo("Balcon, Rangée 2, Siège 8");
        ticket2.setCountdown("Dans 5 jours");
        ticket2.setExpired(false);

        allSoonTickets.add(ticket1);
        allSoonTickets.add(ticket2);

        // Billets à venir plus tard
        allLaterTickets.clear();

        Ticket ticket3 = new Ticket();
        ticket3.setId(3);
        ticket3.setEventTitle("Cirque du Soleil: Alegría");
        ticket3.setEventCategory("Cirque");
        ticket3.setImageResId(R.drawable.event_2);
        ticket3.setDay("10");
        ticket3.setMonth("MAI");
        ticket3.setYear("2025");
        ticket3.setTime("20:00");
        ticket3.setVenue("Parc des expositions");
        ticket3.setAddress("La Charguia, Tunis");
        ticket3.setSeatInfo("Section A, Rangée 3, Siège 15");
        ticket3.setCountdown("Dans 25 jours");
        ticket3.setExpired(false);

        Ticket ticket4 = new Ticket();
        ticket4.setId(4);
        ticket4.setEventTitle("Ballet National de Russie");
        ticket4.setEventCategory("Danse");
        ticket4.setImageResId(R.drawable.event_1);
        ticket4.setDay("15");
        ticket4.setMonth("JUN");
        ticket4.setYear("2025");
        ticket4.setTime("19:00");
        ticket4.setVenue("Théâtre Municipal");
        ticket4.setAddress("Avenue de Paris, Tunis");
        ticket4.setSeatInfo("Loge, Rangée 1, Siège 2");
        ticket4.setCountdown("Dans 61 jours");
        ticket4.setExpired(false);

        allLaterTickets.add(ticket3);
        allLaterTickets.add(ticket4);

        // Appliquer les filtres initiaux (tous les billets)
        filteredSoonTickets.clear();
        filteredSoonTickets.addAll(allSoonTickets);

        filteredLaterTickets.clear();
        filteredLaterTickets.addAll(allLaterTickets);

        // Mettre à jour les adaptateurs
        //soonTicketsAdapter.notifyDataSetChanged();
        //laterTicketsAdapter.notifyDataSetChanged();

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

    private void checkIfTicketsExist() {
        // Vérifier s'il y a des billets à afficher
        if (filteredSoonTickets.isEmpty() && filteredLaterTickets.isEmpty()) {
            // Aucun billet à afficher
            soonHeaderTextView.setVisibility(View.GONE);
            laterHeaderTextView.setVisibility(View.GONE);
            soonTicketsRecyclerView.setVisibility(View.GONE);
            laterTicketsRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            // Il y a des billets à afficher
            emptyStateLayout.setVisibility(View.GONE);

            // Vérifier s'il y a des billets à venir bientôt
            if (filteredSoonTickets.isEmpty()) {
                soonHeaderTextView.setVisibility(View.GONE);
                soonTicketsRecyclerView.setVisibility(View.GONE);
            } else {
                soonHeaderTextView.setVisibility(View.VISIBLE);
                soonTicketsRecyclerView.setVisibility(View.VISIBLE);
            }

            // Vérifier s'il y a des billets à venir plus tard
            if (filteredLaterTickets.isEmpty()) {
                laterHeaderTextView.setVisibility(View.GONE);
                laterTicketsRecyclerView.setVisibility(View.GONE);
            } else {
                laterHeaderTextView.setVisibility(View.VISIBLE);
                laterTicketsRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void applyFilters(List<String> categories, String dateFilter, String venue) {
        // Appliquer les filtres aux billets

        // Filtrer les billets à venir bientôt
        filteredSoonTickets.clear();
        for (Ticket ticket : allSoonTickets) {
            if (shouldIncludeTicket(ticket, categories, dateFilter, venue)) {
                filteredSoonTickets.add(ticket);
            }
        }

        // Filtrer les billets à venir plus tard
        filteredLaterTickets.clear();
        for (Ticket ticket : allLaterTickets) {
            if (shouldIncludeTicket(ticket, categories, dateFilter, venue)) {
                filteredLaterTickets.add(ticket);
            }
        }

        // Mettre à jour les adaptateurs
      //  soonTicketsAdapter.notifyDataSetChanged();
       // laterTicketsAdapter.notifyDataSetChanged();

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