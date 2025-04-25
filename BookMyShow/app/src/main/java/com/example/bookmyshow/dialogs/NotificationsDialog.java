// NotificationsDialog.java
package com.example.bookmyshow.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmyshow.R;
import com.example.bookmyshow.utils.NotificationManager;

import java.util.List;

public class NotificationsDialog extends Dialog {

    private RecyclerView notificationsRecyclerView;
    private Button clearButton;
    private View emptyView;
    private NotificationManager notificationManager;

    public NotificationsDialog(@NonNull Context context, NotificationManager notificationManager) {
        super(context);
        this.notificationManager = notificationManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notifications);

        // Initialiser les vues
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        clearButton = findViewById(R.id.clearButton);
        emptyView = findViewById(R.id.emptyView);

        // Configurer le RecyclerView
        List<String> notifications = notificationManager.getNotificationTitles();
        NotificationsAdapter adapter = new NotificationsAdapter(notifications);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsRecyclerView.setAdapter(adapter);

        // Afficher ou masquer la vue vide
        if (notifications.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            notificationsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            notificationsRecyclerView.setVisibility(View.VISIBLE);
        }

        // Configurer le bouton de suppression
        clearButton.setOnClickListener(v -> {
            notificationManager.clearNotifications();
            adapter.setNotifications(notificationManager.getNotificationTitles());
            emptyView.setVisibility(View.VISIBLE);
            notificationsRecyclerView.setVisibility(View.GONE);
        });

        // Marquer les notifications comme vues
        notificationManager.markNotificationsAsSeen();
    }

    private class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

        private List<String> notifications;

        public NotificationsAdapter(List<String> notifications) {
            this.notifications = notifications;
        }

        public void setNotifications(List<String> notifications) {
            this.notifications = notifications;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String notification = notifications.get(position);
            holder.notificationTextView.setText("Nouvel événement: " + notification);
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView notificationTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                notificationTextView = itemView.findViewById(R.id.notificationTextView);
            }
        }
    }
}