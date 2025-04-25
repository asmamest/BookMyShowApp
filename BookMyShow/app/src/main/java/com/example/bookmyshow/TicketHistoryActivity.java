package com.example.bookmyshow;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TicketHistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView ticketsRecyclerView;
    private LinearLayout emptyStateView;

    private List<TicketHistoryItem> allTickets = new ArrayList<>();
    private List<TicketHistoryItem> filteredTickets = new ArrayList<>();
    private TicketHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_history);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ticket History");

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        ticketsRecyclerView = findViewById(R.id.ticketsRecyclerView);
        emptyStateView = findViewById(R.id.emptyStateView);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup tabs
        setupTabs();

        // Load ticket history
        loadTicketHistory();
    }

    private void setupRecyclerView() {
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketHistoryAdapter(filteredTickets);
        ticketsRecyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Movies"));
        tabLayout.addTab(tabLayout.newTab().setText("Events"));
        tabLayout.addTab(tabLayout.newTab().setText("Plays"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterTickets(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not needed
            }
        });
    }

    private void loadTicketHistory() {
        // In a real app, this would load from a database or API
        // For demo purposes, we'll add some sample ticket history

        // Clear existing data
        allTickets.clear();

        // Add sample data
        allTickets.add(new TicketHistoryItem("Avengers: Endgame", "Movie", "Apr 26, 2023", "Completed", R.drawable.event_1));
        allTickets.add(new TicketHistoryItem("Hamilton", "Play", "May 15, 2023", "Completed", R.drawable.event_2));
        allTickets.add(new TicketHistoryItem("Ed Sheeran Concert", "Event", "Jun 10, 2023", "Completed", R.drawable.event_2));
        allTickets.add(new TicketHistoryItem("Dune: Part Two", "Movie", "Mar 1, 2024", "Completed", R.drawable.event_1));
        allTickets.add(new TicketHistoryItem("The Lion King", "Play", "Feb 12, 2024", "Cancelled", R.drawable.event_5));

        // Apply initial filter (All)
        filterTickets(0);
    }

    private void filterTickets(int tabPosition) {
        filteredTickets.clear();

        switch (tabPosition) {
            case 0: // All
                filteredTickets.addAll(allTickets);
                break;
            case 1: // Movies
                for (TicketHistoryItem ticket : allTickets) {
                    if (ticket.getType().equals("Movie")) {
                        filteredTickets.add(ticket);
                    }
                }
                break;
            case 2: // Events
                for (TicketHistoryItem ticket : allTickets) {
                    if (ticket.getType().equals("Event")) {
                        filteredTickets.add(ticket);
                    }
                }
                break;
            case 3: // Plays
                for (TicketHistoryItem ticket : allTickets) {
                    if (ticket.getType().equals("Play")) {
                        filteredTickets.add(ticket);
                    }
                }
                break;
        }

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredTickets.isEmpty()) {
            ticketsRecyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            ticketsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Model class for ticket history items
    public static class TicketHistoryItem {
        private String title;
        private String type;
        private String date;
        private String status;
        private int imageResId;

        public TicketHistoryItem(String title, String type, String date, String status, int imageResId) {
            this.title = title;
            this.type = type;
            this.date = date;
            this.status = status;
            this.imageResId = imageResId;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public String getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }

        public int getImageResId() {
            return imageResId;
        }
    }

    // Adapter for ticket history
    public static class TicketHistoryAdapter extends RecyclerView.Adapter<TicketHistoryAdapter.ViewHolder> {

        private List<TicketHistoryItem> tickets;

        public TicketHistoryAdapter(List<TicketHistoryItem> tickets) {
            this.tickets = tickets;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_ticket_history, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TicketHistoryItem ticket = tickets.get(position);
            holder.bind(ticket);
        }

        @Override
        public int getItemCount() {
            return tickets.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            // In a real app, you would have actual view references here

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize views
            }

            public void bind(TicketHistoryItem ticket) {
                // Bind data to views
            }
        }
    }
}