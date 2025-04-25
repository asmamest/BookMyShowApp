package com.example.bookmyshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TicketDetailActivity extends AppCompatActivity {

    private Ticket ticket;
    private int ticketId;

    // Vues
    private ImageView eventImageView;
    private TextView eventTitleTextView;
    private TextView eventCategoryTextView;
    private TextView ticketStatusTextView;
    private TextView dateTimeTextView;
    private TextView venueTextView;
    private TextView addressTextView;
    private TextView seatInfoTextView;
    private TextView orderNumberTextView;
    private ImageView qrCodeImageView;
    private TextView ticketIdTextView;
    private TextView entryInfoTextView;
    private TextView contactInfoTextView;
    private Button addToCalendarButton;
    private Button directionsButton;
    private Button shareButton;
    private Button downloadPdfButton;
    private Button transferTicketButton;
    private FloatingActionButton fullscreenFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        ticketId = getIntent().getIntExtra("ticket_id", -1);
        if (ticketId == -1) {
            Toast.makeText(this, "Erreur: Billet introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Charger les données du billet
        loadTicketData();

        // Configurer les boutons d'action
        setupActionButtons();
    }

    private void initViews() {
        eventImageView = findViewById(R.id.eventImageView);
        eventTitleTextView = findViewById(R.id.eventTitleTextView);
        eventCategoryTextView = findViewById(R.id.eventCategoryTextView);
        ticketStatusTextView = findViewById(R.id.ticketStatusTextView);
        dateTimeTextView = findViewById(R.id.dateTimeTextView);
        venueTextView = findViewById(R.id.venueTextView);
        addressTextView = findViewById(R.id.addressTextView);
        seatInfoTextView = findViewById(R.id.seatInfoTextView);
        orderNumberTextView = findViewById(R.id.orderNumberTextView);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        ticketIdTextView = findViewById(R.id.ticketIdTextView);
        entryInfoTextView = findViewById(R.id.entryInfoTextView);
        contactInfoTextView = findViewById(R.id.contactInfoTextView);
        addToCalendarButton = findViewById(R.id.addToCalendarButton);
        directionsButton = findViewById(R.id.directionsButton);
        shareButton = findViewById(R.id.shareButton);
        downloadPdfButton = findViewById(R.id.downloadPdfButton);
        transferTicketButton = findViewById(R.id.transferTicketButton);
    }

    private void loadTicketData() {
        // Dans une application réelle, vous récupéreriez les données du billet depuis une base de données
        // Ici, nous simulons la récupération des données

        // Exemple de données de billet
        ticket = getTicketById(ticketId);

        if (ticket == null) {
            Toast.makeText(this, "Erreur: Billet introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Mettre à jour les vues avec les données du billet
        eventImageView.setImageResource(ticket.getImageResId());
        eventTitleTextView.setText(ticket.getEventTitle());
        eventCategoryTextView.setText(ticket.getEventCategory());

        // Statut du billet
        if (ticket.isExpired()) {
            ticketStatusTextView.setText("Billet expiré");
            ticketStatusTextView.setTextColor(Color.parseColor("#FF5252"));
        } else {
            ticketStatusTextView.setText("Billet confirmé");
            ticketStatusTextView.setTextColor(Color.parseColor("#4CAF50"));
        }

        // Date et heure
        String dateTime = ticket.getDay() + " " + ticket.getMonth() + " " + ticket.getYear() + " à " + ticket.getTime();
        dateTimeTextView.setText(dateTime);

        // Lieu et adresse
        venueTextView.setText(ticket.getVenue());
        addressTextView.setText(ticket.getAddress());

        // Informations sur le siège
        seatInfoTextView.setText(ticket.getSeatInfo());

        // Numéro de commande (simulé)
        orderNumberTextView.setText("BMS-" + (10000000 + ticketId));

        // ID du billet (simulé)
        String ticketIdStr = "T-" + (1000000000 - ticketId);
        ticketIdTextView.setText("ID: " + ticketIdStr);

        // Générer le code QR
        generateQRCode(ticketIdStr);

        // Informations d'entrée spécifiques à la catégorie
        updateEntryInfo(ticket.getEventCategory());

        // Désactiver certains boutons si le billet est expiré
        if (ticket.isExpired()) {
            addToCalendarButton.setEnabled(false);
            transferTicketButton.setEnabled(false);
        }
    }

    private void updateEntryInfo(String category) {
        String baseInfo = "• Les portes ouvrent 1 heure avant le début du spectacle.\n" +
                "• Une pièce d'identité peut être demandée à l'entrée.\n";

        String specificInfo = "";

        switch (category) {
            case "Théâtre":
                specificInfo = "• Les photos et vidéos ne sont pas autorisées pendant la représentation.\n" +
                        "• Les retardataires ne seront admis qu'à un moment approprié de la représentation.";
                break;
            case "Concert":
                specificInfo = "• Les appareils photo professionnels ne sont pas autorisés.\n" +
                        "• Les bouteilles en verre et les objets métalliques sont interdits.";
                break;
            case "Opéra":
                specificInfo = "• Tenue correcte exigée.\n" +
                        "• Les retardataires ne seront admis qu'à l'entracte.";
                break;
            case "Danse":
                specificInfo = "• Les photos et vidéos ne sont pas autorisées pendant le spectacle.\n" +
                        "• Silence absolu requis pendant la représentation.";
                break;
            case "Cirque":
                specificInfo = "• Les animaux de compagnie ne sont pas admis.\n" +
                        "• Nourriture et boissons disponibles sur place.";
                break;
            default:
                specificInfo = "• Les photos et vidéos ne sont pas autorisées pendant le spectacle.\n" +
                        "• Les retardataires ne seront admis qu'à un moment approprié de la représentation.";
        }

        entryInfoTextView.setText(baseInfo + specificInfo);
    }

    private void generateQRCode(String ticketId) {
        try {
            // Contenu du QR code (dans une application réelle, ce serait un identifiant unique sécurisé)
            String qrContent = "BOOKMYSHOW:" + ticketId + ":" + System.currentTimeMillis();

            // Générer le QR code
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 512, 512);

            // Convertir la matrice en bitmap
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Afficher le QR code
            qrCodeImageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
            qrCodeImageView.setImageResource(R.drawable.qr_code);
        }
    }

    private void setupActionButtons() {
        // Ajouter au calendrier
        addToCalendarButton.setOnClickListener(v -> addEventToCalendar());

        // Obtenir l'itinéraire
        directionsButton.setOnClickListener(v -> openDirections());

        // Partager le billet
        shareButton.setOnClickListener(v -> shareTicket());

        // Télécharger le PDF
        downloadPdfButton.setOnClickListener(v -> downloadTicketPdf());

        }

    private void addEventToCalendar() {
        // Créer un intent pour ajouter l'événement au calendrier
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(android.provider.CalendarContract.Events.CONTENT_URI);

        // Définir les détails de l'événement
        intent.putExtra(android.provider.CalendarContract.Events.TITLE, ticket.getEventTitle());
        intent.putExtra(android.provider.CalendarContract.Events.DESCRIPTION,
                "Billet réservé via BookMyShow\nSiège: " + ticket.getSeatInfo());
        intent.putExtra(android.provider.CalendarContract.Events.EVENT_LOCATION,
                ticket.getVenue() + ", " + ticket.getAddress());

        // Définir la date et l'heure (simulées ici)
        Calendar startTime = Calendar.getInstance();
        // Configurer la date et l'heure en fonction des données du billet
        // (à implémenter selon le format de date/heure de votre application)

        intent.putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis());

        // Ajouter un rappel 1 heure avant
        intent.putExtra(android.provider.CalendarContract.Reminders.MINUTES, 60);

        // Lancer l'intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aucune application de calendrier trouvée", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDirections() {
        // Ouvrir l'application de navigation avec l'adresse du lieu
        String address = ticket.getVenue() + ", " + ticket.getAddress();
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback si Google Maps n'est pas installé
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }

    private void shareTicket() {
        // Créer le texte à partager
        String shareText = "J'ai réservé des billets pour " + ticket.getEventTitle() +
                " le " + ticket.getDay() + " " + ticket.getMonth() + " " + ticket.getYear() +
                " à " + ticket.getTime() + " au " + ticket.getVenue() + ". Rejoins-moi !";

        // Créer l'intent de partage
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mon billet pour " + ticket.getEventTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Lancer l'intent
        startActivity(Intent.createChooser(shareIntent, "Partager via"));
    }

    private void downloadTicketPdf() {
        // Simuler le téléchargement d'un PDF
        Toast.makeText(this, "Téléchargement du PDF en cours...", Toast.LENGTH_SHORT).show();

        // Dans une application réelle, vous généreriez un PDF ici
        // et le téléchargeriez dans le dossier de téléchargements

        // Simuler un délai de téléchargement
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this, "PDF téléchargé avec succès", Toast.LENGTH_SHORT).show();
        }, 2000);
    }


    private Ticket getTicketById(int id) {
        // Dans une application réelle, vous récupéreriez le billet depuis une base de données
        // Ici, nous simulons la récupération du billet

        // Créer un billet fictif pour la démonstration
        Ticket ticket = new Ticket();
        ticket.setId(id);

        // Définir les propriétés du billet en fonction de l'ID
        switch (id) {
            case 1:
                ticket.setEventTitle("Le Misanthrope");
                ticket.setEventCategory("Théâtre");
                ticket.setImageResId(R.drawable.event_1);
                ticket.setDay("15");
                ticket.setMonth("AVR");
                ticket.setYear("2025");
                ticket.setTime("20:00");
                ticket.setVenue("Théâtre National");
                ticket.setAddress("Avenue Habib Bourguiba, Tunis");
                ticket.setSeatInfo("Orchestre, Rangée 5, Siège 12");
                ticket.setCountdown("Dans 3 jours");
                ticket.setExpired(false);
                break;
            case 2:
                ticket.setEventTitle("Carmen");
                ticket.setEventCategory("Opéra");
                ticket.setImageResId(R.drawable.event_2);
                ticket.setDay("20");
                ticket.setMonth("AVR");
                ticket.setYear("2025");
                ticket.setTime("19:00");
                ticket.setVenue("Opéra de Tunis");
                ticket.setAddress("Rue de Marseille, Tunis");
                ticket.setSeatInfo("Balcon, Rangée 2, Siège 8");
                ticket.setCountdown("Dans 5 jours");
                ticket.setExpired(false);
                break;
            case 3:
                ticket.setEventTitle("Cirque du Soleil: Alegría");
                ticket.setEventCategory("Cirque");
                ticket.setImageResId(R.drawable.event_1);
                ticket.setDay("10");
                ticket.setMonth("MAI");
                ticket.setYear("2025");
                ticket.setTime("20:00");
                ticket.setVenue("Parc des expositions");
                ticket.setAddress("La Charguia, Tunis");
                ticket.setSeatInfo("Section A, Rangée 3, Siège 15");
                ticket.setCountdown("Dans 25 jours");
                ticket.setExpired(false);
                break;
            case 4:
                ticket.setEventTitle("Ballet National de Russie");
                ticket.setEventCategory("Danse");
                ticket.setImageResId(R.drawable.event_2);
                ticket.setDay("15");
                ticket.setMonth("JUN");
                ticket.setYear("2025");
                ticket.setTime("19:00");
                ticket.setVenue("Théâtre Municipal");
                ticket.setAddress("Avenue de Paris, Tunis");
                ticket.setSeatInfo("Loge, Rangée 1, Siège 2");
                ticket.setCountdown("Dans 61 jours");
                ticket.setExpired(false);
                break;
            case 5:
                ticket.setEventTitle("Hamlet");
                ticket.setEventCategory("Théâtre");
                ticket.setImageResId(R.drawable.event_5);
                ticket.setDay("10");
                ticket.setMonth("MAR");
                ticket.setYear("2025");
                ticket.setTime("19:30");
                ticket.setVenue("Théâtre National");
                ticket.setAddress("Avenue Habib Bourguiba, Tunis");
                ticket.setSeatInfo("Orchestre, Rangée 7, Siège 9");
                ticket.setExpired(true);
                break;
            case 6:
                ticket.setEventTitle("Concert Symphonique");
                ticket.setEventCategory("Concert");
                ticket.setImageResId(R.drawable.event_2);
                ticket.setDay("25");
                ticket.setMonth("FEV");
                ticket.setYear("2025");
                ticket.setTime("20:00");
                ticket.setVenue("Cité de la Culture");
                ticket.setAddress("Avenue Mohamed V, Tunis");
                ticket.setSeatInfo("Parterre, Rangée 10, Siège 15");
                ticket.setExpired(true);
                break;
            case 7:
                ticket.setEventTitle("La Traviata");
                ticket.setEventCategory("Opéra");
                ticket.setImageResId(R.drawable.event_1);
                ticket.setDay("15");
                ticket.setMonth("JAN");
                ticket.setYear("2025");
                ticket.setTime("19:00");
                ticket.setVenue("Opéra de Tunis");
                ticket.setAddress("Rue de Marseille, Tunis");
                ticket.setSeatInfo("Loge, Rangée 1, Siège 3");
                ticket.setExpired(true);
                break;
            default:
                return null;
        }

        return ticket;
    }
}
