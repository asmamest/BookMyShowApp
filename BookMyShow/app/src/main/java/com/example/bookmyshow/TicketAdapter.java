package com.example.bookmyshow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_UPCOMING = 1;
    private static final int VIEW_TYPE_PAST = 2;

    private Context context;
    private List<Ticket> tickets;
    private boolean isUpcoming;

    public TicketAdapter(Context context, List<Ticket> tickets, boolean isUpcoming) {
        this.context = context;
        this.tickets = tickets;
        this.isUpcoming = isUpcoming;
    }

    @Override
    public int getItemViewType(int position) {
        return isUpcoming ? VIEW_TYPE_UPCOMING : VIEW_TYPE_PAST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_UPCOMING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_upcoming_ticket, parent, false);
            return new UpcomingTicketViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_past_ticket, parent, false);
            return new PastTicketViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);

        if (holder instanceof UpcomingTicketViewHolder) {
            bindUpcomingTicket((UpcomingTicketViewHolder) holder, ticket);
        } else if (holder instanceof PastTicketViewHolder) {
            bindPastTicket((PastTicketViewHolder) holder, ticket);
        }
    }

    private void bindUpcomingTicket(UpcomingTicketViewHolder holder, Ticket ticket) {
        // Configurer les vues pour un billet à venir
        holder.eventImageView.setImageResource(ticket.getImageResId());
        holder.eventTitleTextView.setText(ticket.getEventTitle());
        holder.eventCategoryTextView.setText(ticket.getEventCategory());
        holder.dayTextView.setText(ticket.getDay());
        holder.monthTextView.setText(ticket.getMonth());
        holder.timeTextView.setText(ticket.getTime());
        holder.venueTextView.setText(ticket.getVenue());
        holder.addressTextView.setText(ticket.getAddress());
        holder.seatInfoTextView.setText(ticket.getSeatInfo());
        holder.countdownTextView.setText(ticket.getCountdown());

        // Configurer les boutons d'action
        holder.viewTicketButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, TicketDetailActivity.class);
            intent.putExtra("ticket_id", ticket.getId());
            context.startActivity(intent);
        });

        holder.addToCalendarButton.setOnClickListener(v -> {
            // Ajouter l'événement au calendrier
            addEventToCalendar(ticket);
        });

        holder.directionsButton.setOnClickListener(v -> {
            // Ouvrir l'application de navigation avec l'adresse du lieu
            openDirections(ticket.getAddress());
        });

        holder.shareButton.setOnClickListener(v -> {
            // Partager les informations du billet
            shareTicket(ticket);
        });
    }

    private void bindPastTicket(PastTicketViewHolder holder, Ticket ticket) {
        // Configurer les vues pour un billet passé
        holder.eventImageView.setImageResource(ticket.getImageResId());
        holder.eventTitleTextView.setText(ticket.getEventTitle());
        holder.eventCategoryTextView.setText(ticket.getEventCategory());
        holder.dateTextView.setText(ticket.getDay() + " " + ticket.getMonth() + " " + ticket.getYear());
        holder.timeTextView.setText(ticket.getTime());
        holder.venueTextView.setText(ticket.getVenue());

        // Configurer le bouton d'options
        holder.moreOptionsButton.setOnClickListener(v -> {
            showPopupMenu(v, ticket);
        });
    }

    private void showPopupMenu(View view, Ticket ticket) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.menu_past_ticket, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_view_details) {
                Intent intent = new Intent(context, TicketDetailActivity.class);
                intent.putExtra("ticket_id", ticket.getId());
                context.startActivity(intent);
                return true;
            } else if (id == R.id.action_download_pdf) {
                // Télécharger le PDF du billet
                downloadTicketPdf(ticket);
                return true;
            } else if (id == R.id.action_delete) {
                // Supprimer le billet de l'historique
                deleteTicket(ticket);
                return true;
            }

            return false;
        });

        popup.show();
    }

    private void addEventToCalendar(Ticket ticket) {
        // Implémenter l'ajout de l'événement au calendrier
        // Cette méthode utiliserait l'API Calendar pour ajouter l'événement

        // Exemple simplifié
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", ticket.getEventTitle());
        intent.putExtra("description", "Billet réservé via BookMyShow");
        intent.putExtra("eventLocation", ticket.getVenue() + ", " + ticket.getAddress());
        // Ajouter la date et l'heure (à implémenter)

        context.startActivity(intent);
    }

    private void openDirections(String address) {
        // Ouvrir l'application de navigation avec l'adresse du lieu
        // Cette méthode utiliserait l'API Maps pour ouvrir les directions

        // Exemple simplifié
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }

    private void shareTicket(Ticket ticket) {
        // Partager les informations du billet

        String shareText = "J'ai réservé des billets pour " + ticket.getEventTitle() +
                " le " + ticket.getDay() + " " + ticket.getMonth() + " " + ticket.getYear() +
                " à " + ticket.getTime() + " au " + ticket.getVenue() + ". Rejoins-moi !";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        context.startActivity(Intent.createChooser(shareIntent, "Partager via"));
    }

    private void downloadTicketPdf(Ticket ticket) {
        // Télécharger le PDF du billet
        // Cette méthode générerait un PDF du billet et le téléchargerait

        // Exemple simplifié (afficher un message Toast)
        Toast.makeText(context, "Téléchargement du PDF pour " + ticket.getEventTitle(), Toast.LENGTH_SHORT).show();
    }

    private void deleteTicket(Ticket ticket) {
        // Supprimer le billet de l'historique
        // Cette méthode supprimerait le billet de la base de données

        // Exemple simplifié
        int position = tickets.indexOf(ticket);
        if (position != -1) {
            tickets.remove(position);
            notifyItemRemoved(position);

            // Vérifier si la liste est vide
            if (tickets.isEmpty() && context instanceof MyTicketsActivity) {
                // Mettre à jour l'interface utilisateur pour afficher l'état vide
                PastTicketsFragment fragment = (PastTicketsFragment) ((MyTicketsActivity) context)
                        .getSupportFragmentManager()
                        .findFragmentByTag("f1");

                if (fragment != null) {
                    fragment.checkIfTicketsExist();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    // ViewHolder pour les billets à venir
    static class UpcomingTicketViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImageView;
        TextView eventTitleTextView;
        TextView eventCategoryTextView;
        TextView dayTextView;
        TextView monthTextView;
        TextView timeTextView;
        TextView venueTextView;
        TextView addressTextView;
        TextView seatInfoTextView;
        TextView countdownTextView;
        Button viewTicketButton;
        Button addToCalendarButton;
        Button directionsButton;
        Button shareButton;

        public UpcomingTicketViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImageView = itemView.findViewById(R.id.eventImageView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView);
            eventCategoryTextView = itemView.findViewById(R.id.eventCategoryTextView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            monthTextView = itemView.findViewById(R.id.monthTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            venueTextView = itemView.findViewById(R.id.venueTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            seatInfoTextView = itemView.findViewById(R.id.seatInfoTextView);
            countdownTextView = itemView.findViewById(R.id.countdownTextView);
            viewTicketButton = itemView.findViewById(R.id.viewTicketButton);
            addToCalendarButton = itemView.findViewById(R.id.addToCalendarButton);
            directionsButton = itemView.findViewById(R.id.directionsButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }

    // ViewHolder pour les billets passés
    static class PastTicketViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImageView;
        TextView eventTitleTextView;
        TextView eventCategoryTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView venueTextView;
        ImageButton moreOptionsButton;

        public PastTicketViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImageView = itemView.findViewById(R.id.eventImageView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitleTextView);
            eventCategoryTextView = itemView.findViewById(R.id.eventCategoryTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            venueTextView = itemView.findViewById(R.id.venueTextView);
            moreOptionsButton = itemView.findViewById(R.id.moreOptionsButton);
        }
    }
}