package com.example.bookmyshow;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.bookmyshow.api.ApiService;
import com.example.bookmyshow.api.ApiClient;

import com.example.bookmyshow.enums.TicketType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketConfirmationActivity extends AppCompatActivity {

    private Button shareTicketButton;
    private Button addToCalendarButton;
    private Button downloadPdfButton;
    private Button homeButton;

    // Vues pour les détails du ticket
    private TextView bookingReferenceTextView;
    private TextView bookingDateTimeTextView;
    private TextView showTitleTextView;
    private TextView showDateTextView;
    private TextView showTimeTextView;
    private TextView showVenueTextView;
    private TextView seatCategoryTextView;
    private TextView seatCountTextView;
    private TextView totalPriceTextView;
    private TextView customerEmailTextView;
    private ImageView qrCodeImageView;

    // Données du ticket
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private int vipQuantity;
    private int premiumQuantity;
    private int standardQuantity;
    private double totalAmount;
    private boolean isStudentDiscount;
    private String customerEmail;
    private String bookingReference;
    private String bookingDateTime;

    // Couleurs - NE PAS initialiser ici
    private int colorPrimary;
    private int colorAccent;
    private int colorBlack;
    private int colorGray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_confirmation);

        // Initialiser les couleurs ici
        colorPrimary = getResources().getColor(R.color.colorPrimary);
        colorAccent = getResources().getColor(R.color.colorAccent);
        colorBlack = Color.BLACK;
        colorGray = Color.GRAY;

        initViews();
        getIntentData();
        generateBookingReference();
        setBookingDateTime();

        updateUI();
        generateQRCode();
        setupButtons();
        sendConfirmationEmail();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        }
    }

    private void initViews() {
        // Boutons
        shareTicketButton = findViewById(R.id.shareTicketButton);
        addToCalendarButton = findViewById(R.id.addToCalendarButton);
        downloadPdfButton = findViewById(R.id.downloadPdfButton);
        homeButton = findViewById(R.id.btn_home);

        // Vues pour les détails du ticket
        bookingReferenceTextView = findViewById(R.id.txt_booking_reference);
        bookingDateTimeTextView = findViewById(R.id.txt_booking_datetime);
        showTitleTextView = findViewById(R.id.txt_show_title);
        showDateTextView = findViewById(R.id.txt_show_date);
        showTimeTextView = findViewById(R.id.txt_show_time);
        showVenueTextView = findViewById(R.id.txt_show_venue);
        seatCategoryTextView = findViewById(R.id.txt_seat_category);
        seatCountTextView = findViewById(R.id.txt_seat_count);
        totalPriceTextView = findViewById(R.id.txt_total_price);
        customerEmailTextView = findViewById(R.id.txt_customer_email);
        qrCodeImageView = findViewById(R.id.img_qr_code);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Récupérer les informations de l'événement
            eventTitle = intent.getStringExtra("eventTitle");
            if (eventTitle == null) eventTitle = "Événement";

            eventDate = intent.getStringExtra("eventDate");
            if (eventDate == null) eventDate = "Date non spécifiée";

            eventLocation = intent.getStringExtra("eventLocation");
            if (eventLocation == null) eventLocation = "Lieu non spécifié";

            // Récupérer les informations des billets
            vipQuantity = intent.getIntExtra("vipQuantity", 0);
            premiumQuantity = intent.getIntExtra("premiumQuantity", 0);
            standardQuantity = intent.getIntExtra("standardQuantity", 0);
            totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
            isStudentDiscount = intent.getBooleanExtra("isStudentDiscount", false);

            // Récupérer l'email du client
            customerEmail = intent.getStringExtra("customerEmail");
            if (customerEmail == null) customerEmail = "email@example.com";
        } else {
            // Valeurs par défaut si l'intent est null
            eventTitle = "Événement";
            eventDate = "Date non spécifiée";
            eventLocation = "Lieu non spécifié";
            customerEmail = "email@example.com";
        }
    }

    private void generateBookingReference() {
        // Générer une référence aléatoire de 8 chiffres
        Random random = new Random();
        int randomNumber = 10000000 + random.nextInt(90000000);
        bookingReference = String.valueOf(randomNumber);
    }

    private void setBookingDateTime() {
        // Obtenir la date et l'heure actuelles
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        bookingDateTime = sdf.format(new Date());
    }

    private void updateUI() {
        // Mettre à jour les vues avec les données
        if (bookingReferenceTextView != null) {
            bookingReferenceTextView.setText("Référence: #" + bookingReference);
        }

        if (bookingDateTimeTextView != null) {
            bookingDateTimeTextView.setText("Réservé le: " + bookingDateTime);
        }

        if (showTitleTextView != null && eventTitle != null) {
            showTitleTextView.setText(eventTitle);
        }

        if (showDateTextView != null && eventDate != null) {
            showDateTextView.setText(eventDate);
        }

        if (showVenueTextView != null && eventLocation != null) {
            showVenueTextView.setText(eventLocation);
        }

        if (totalPriceTextView != null) {
            totalPriceTextView.setText(String.format(Locale.getDefault(), "$%.2f", totalAmount));
        }

        if (customerEmailTextView != null && customerEmail != null) {
            customerEmailTextView.setText(customerEmail);
        }

        // Afficher l'heure (extraire de la date si possible, sinon utiliser une valeur par défaut)
        String time = "20:00";
        if (eventDate != null && eventDate.contains(" ")) {
            String[] parts = eventDate.split(" ");
            if (parts.length > 1) {
                time = parts[1];
            }
        }

        if (showTimeTextView != null) {
            showTimeTextView.setText(time);
        }

        // Afficher toutes les catégories de billets
        StringBuilder categoryText = new StringBuilder();
        StringBuilder countText = new StringBuilder();

        int totalTickets = vipQuantity + premiumQuantity + standardQuantity;

        // Créer dynamiquement les vues pour chaque catégorie de billet
        LinearLayout ticketCategoriesContainer = findViewById(R.id.ticket_categories_container);
        if (ticketCategoriesContainer != null) {
            ticketCategoriesContainer.removeAllViews(); // Nettoyer les vues existantes

            if (vipQuantity > 0) {
                addTicketCategoryView(ticketCategoriesContainer, TicketType.VIP.name(), vipQuantity);
            }

            if (premiumQuantity > 0) {
                addTicketCategoryView(ticketCategoriesContainer, TicketType.PREMIUM.name(), premiumQuantity);
            }

            if (standardQuantity > 0) {
                addTicketCategoryView(ticketCategoriesContainer, TicketType.STANDARD.name(), standardQuantity);
            }
        }

        // Afficher la catégorie principale et le nombre total de places
        if (vipQuantity > 0) {
            categoryText.append(TicketType.VIP.name());
        } else if (premiumQuantity > 0) {
            categoryText.append(TicketType.PREMIUM.name());
        } else if (standardQuantity > 0) {
            categoryText.append(TicketType.STANDARD.name());
        }

        countText.append(totalTickets).append(" place(s)");

        if (seatCategoryTextView != null) {
            seatCategoryTextView.setText(categoryText.toString());
        }

        if (seatCountTextView != null) {
            seatCountTextView.setText(countText.toString());
        }
    }

    private void addTicketCategoryView(LinearLayout container, String category, int quantity) {
        if (container == null || category == null) return;

        View categoryView = getLayoutInflater().inflate(R.layout.item_ticket_category, container, false);
        if (categoryView == null) return;

        TextView categoryNameTextView = categoryView.findViewById(R.id.category_name);
        TextView quantityTextView = categoryView.findViewById(R.id.category_quantity);

        if (categoryNameTextView != null) {
            categoryNameTextView.setText(category);
        }

        if (quantityTextView != null) {
            quantityTextView.setText(quantity + " place(s)");
        }

        container.addView(categoryView);
    }

    private void generateQRCode() {
        if (qrCodeImageView == null) return;

        try {
            // Créer le contenu du QR code
            String qrContent = "REF:" + bookingReference +
                    "\nEVENT:" + (eventTitle != null ? eventTitle : "Non spécifié") +
                    "\nDATE:" + (eventDate != null ? eventDate : "Non spécifiée") +
                    "\nEMAIL:" + (customerEmail != null ? customerEmail : "Non spécifié");

            // Générer le QR code
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Afficher le QR code
            qrCodeImageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la génération du QR code", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur inattendue: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtons() {
        // Bouton de partage
        if (shareTicketButton != null) {
            shareTicketButton.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mon billet pour " + eventTitle);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "J'ai réservé des places pour " + eventTitle +
                                " le " + eventDate +
                                " à " + eventLocation +
                                ". Référence de réservation: #" + bookingReference);
                startActivity(Intent.createChooser(shareIntent, "Partager via"));
            });
        }

        // Bouton d'ajout au calendrier
        if (addToCalendarButton != null) {
            addToCalendarButton.setOnClickListener(v -> {
                Calendar beginTime = Calendar.getInstance();
                // Extraire la date et l'heure de eventDate si possible
                // Pour cet exemple, nous utilisons une date fixe
                beginTime.set(2023, 3, 15, 20, 0); // 15 avril 2023, 20:00
                Calendar endTime = Calendar.getInstance();
                endTime.set(2023, 3, 15, 23, 0); // 15 avril 2023, 23:00

                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, eventTitle)
                        .putExtra(CalendarContract.Events.DESCRIPTION, "Référence de réservation: #" + bookingReference)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation)
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                startActivity(intent);
            });
        }

        // Bouton de téléchargement PDF
        if (downloadPdfButton != null) {
            downloadPdfButton.setOnClickListener(v -> {
                generateAndDownloadPDF();
            });
        }

        // Bouton d'accueil
        if (homeButton != null) {
            homeButton.setOnClickListener(v -> {
                // Retourner à l'écran d'accueil
                try {
                    Intent intent = new Intent(TicketConfirmationActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Si HomeActivity n'existe pas, essayer de revenir à MainActivity
                    try {
                        Intent intent = new Intent(TicketConfirmationActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(this, "Impossible de retourner à l'écran d'accueil", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void resendConfirmationEmail() {
        if (bookingReference == null || customerEmail == null) return;

        ApiService apiService = ApiClient.getApiService();
        if (apiService == null) return;

        Call<String> call = apiService.resendConfirmationEmail(bookingReference);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TicketConfirmationActivity.this,
                            "Email de confirmation renvoyé avec succès",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TicketConfirmationActivity.this,
                            "Erreur lors de l'envoi de l'email",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(TicketConfirmationActivity.this,
                        "Erreur de connexion: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateAndDownloadPDF() {
        try {
            // Créer un document PDF
            PdfDocument document = new PdfDocument();

            // Créer une page
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            // Dessiner sur la page
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            // Dessiner l'en-tête
            paint.setColor(colorPrimary);
            canvas.drawRect(0, 0, 595, 100, paint);

            // Titre
            paint.setColor(Color.WHITE);
            paint.setTextSize(24);
            paint.setFakeBoldText(true);
            canvas.drawText("BILLET DE SPECTACLE", 50, 60, paint);

            // Référence
            paint.setTextSize(16);
            paint.setFakeBoldText(false);
            canvas.drawText("Référence: #" + bookingReference, 50, 85, paint);

            // Ligne de séparation
            paint.setColor(colorGray);
            canvas.drawLine(50, 120, 545, 120, paint);

            // Détails de l'événement
            paint.setColor(colorBlack);
            paint.setTextSize(18);
            paint.setFakeBoldText(true);
            canvas.drawText("Détails de l'événement", 50, 150, paint);

            paint.setFakeBoldText(false);
            paint.setTextSize(14);
            canvas.drawText("Événement:", 50, 180, paint);
            paint.setColor(colorPrimary);

            // Vérifier si eventTitle est null
            if (eventTitle != null) {
                canvas.drawText(eventTitle, 150, 180, paint);
            } else {
                canvas.drawText("Non spécifié", 150, 180, paint);
            }

            paint.setColor(colorBlack);
            canvas.drawText("Date:", 50, 205, paint);
            paint.setColor(colorPrimary);

            // Vérifier si eventDate est null
            if (eventDate != null) {
                canvas.drawText(eventDate, 150, 205, paint);
            } else {
                canvas.drawText("Non spécifiée", 150, 205, paint);
            }

            paint.setColor(colorBlack);
            canvas.drawText("Lieu:", 50, 230, paint);
            paint.setColor(colorPrimary);

            // Vérifier si eventLocation est null
            if (eventLocation != null) {
                canvas.drawText(eventLocation, 150, 230, paint);
            } else {
                canvas.drawText("Non spécifié", 150, 230, paint);
            }

            // Ligne de séparation
            paint.setColor(colorGray);
            canvas.drawLine(50, 250, 545, 250, paint);

            // Détails des billets
            paint.setColor(colorBlack);
            paint.setTextSize(18);
            paint.setFakeBoldText(true);
            canvas.drawText("Détails des billets", 50, 280, paint);

            paint.setFakeBoldText(false);
            paint.setTextSize(14);

            int y = 310;
            if (vipQuantity > 0) {
                paint.setColor(colorBlack);
                canvas.drawText(TicketType.VIP.name() + ":", 50, y, paint);
                paint.setColor(colorPrimary);
                canvas.drawText(vipQuantity + " place(s)", 150, y, paint);
                y += 25;
            }

            if (premiumQuantity > 0) {
                paint.setColor(colorBlack);
                canvas.drawText(TicketType.PREMIUM.name() + ":", 50, y, paint);
                paint.setColor(colorPrimary);
                canvas.drawText(premiumQuantity + " place(s)", 150, y, paint);
                y += 25;
            }

            if (standardQuantity > 0) {
                paint.setColor(colorBlack);
                canvas.drawText(TicketType.STANDARD.name() + ":", 50, y, paint);
                paint.setColor(colorPrimary);
                canvas.drawText(standardQuantity + " place(s)", 150, y, paint);
                y += 25;
            }

            // Ligne de séparation
            paint.setColor(colorGray);
            canvas.drawLine(50, y + 10, 545, y + 10, paint);

            // Prix total
            paint.setColor(colorBlack);
            paint.setTextSize(18);
            paint.setFakeBoldText(true);
            canvas.drawText("Prix total:", 50, y + 40, paint);

            paint.setColor(colorAccent);
            paint.setTextSize(20);
            canvas.drawText("$" + String.format(Locale.getDefault(), "%.2f", totalAmount), 150, y + 40, paint);

            // Ligne de séparation
            paint.setColor(colorGray);
            canvas.drawLine(50, y + 60, 545, y + 60, paint);

            // Informations client
            paint.setColor(colorBlack);
            paint.setTextSize(18);
            paint.setFakeBoldText(true);
            canvas.drawText("Informations client", 50, y + 90, paint);

            paint.setFakeBoldText(false);
            paint.setTextSize(14);
            canvas.drawText("Email:", 50, y + 120, paint);
            paint.setColor(colorPrimary);

            // Vérifier si customerEmail est null
            if (customerEmail != null) {
                canvas.drawText(customerEmail, 150, y + 120, paint);
            } else {
                canvas.drawText("Non spécifié", 150, y + 120, paint);
            }

            paint.setColor(colorBlack);
            canvas.drawText("Date de réservation:", 50, y + 145, paint);
            paint.setColor(colorPrimary);

            // Vérifier si bookingDateTime est null
            if (bookingDateTime != null) {
                canvas.drawText(bookingDateTime, 180, y + 145, paint);
            } else {
                canvas.drawText("Non spécifiée", 180, y + 145, paint);
            }

            // Ajouter le QR code
            try {
                // Récupérer le bitmap du QR code
                if (qrCodeImageView != null && qrCodeImageView.getDrawable() != null) {
                    Bitmap qrBitmap = ((BitmapDrawable) qrCodeImageView.getDrawable()).getBitmap();
                    if (qrBitmap != null) {
                        // Redimensionner si nécessaire
                        Bitmap resizedQR = Bitmap.createScaledBitmap(qrBitmap, 150, 150, false);
                        // Dessiner le QR code
                        canvas.drawBitmap(resizedQR, 400, y + 10, null);

                        // Ajouter un texte sous le QR code
                        paint.setColor(colorBlack);
                        paint.setTextSize(12);
                        paint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText("Scannez ce code à l'entrée", 475, y + 170, paint);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Pied de page
            paint.setColor(colorGray);
            paint.setTextSize(10);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Ce billet est personnel et ne peut être revendu. Merci de présenter une pièce d'identité à l'entrée.",
                    297, 800, paint);

            // Terminer la page
            document.finishPage(page);

            // Enregistrer le PDF
            String fileName = "Ticket_" + bookingReference + ".pdf";
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

            try {
                document.writeTo(new FileOutputStream(file));
                document.close();

                // Ouvrir le PDF
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Aucune application pour ouvrir les PDF n'est installée", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(this, "PDF téléchargé avec succès", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors de la création du PDF", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur inattendue: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendConfirmationEmail() {
        // Vérifier si l'email est valide
        if (customerEmail == null || customerEmail.isEmpty()) {
            Log.e("TicketConfirmation", "Email client non valide");
            return;
        }

        // Cette méthode doit être exécutée dans un thread séparé
        new Thread(() -> {
            try {
                // Configuration de l'email
                final String username = "votre_email@gmail.com"; // À remplacer par votre email
                final String password = "votre_mot_de_passe"; // À remplacer par votre mot de passe

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                // Créer une session avec authentification
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                // Créer le message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(customerEmail));
                message.setSubject("Confirmation de réservation - " + (eventTitle != null ? eventTitle : "Événement"));

                // Corps du message
                StringBuilder emailBody = new StringBuilder();
                emailBody.append("Bonjour,\n\n");
                emailBody.append("Votre réservation pour ").append(eventTitle != null ? eventTitle : "l'événement").append(" a été confirmée.\n\n");
                emailBody.append("Détails de la réservation :\n");
                emailBody.append("Référence : #").append(bookingReference).append("\n");
                emailBody.append("Date : ").append(eventDate != null ? eventDate : "Non spécifiée").append("\n");
                emailBody.append("Lieu : ").append(eventLocation != null ? eventLocation : "Non spécifié").append("\n\n");

                if (vipQuantity > 0) {
                    emailBody.append("VIP : ").append(vipQuantity).append(" place(s)\n");
                }
                if (premiumQuantity > 0) {
                    emailBody.append("Premium : ").append(premiumQuantity).append(" place(s)\n");
                }
                if (standardQuantity > 0) {
                    emailBody.append("Standard : ").append(standardQuantity).append(" place(s)\n");
                }

                emailBody.append("\nPrix total : $").append(String.format(Locale.getDefault(), "%.2f", totalAmount)).append("\n\n");
                emailBody.append("Merci pour votre réservation !\n");
                emailBody.append("L'équipe BookMyShow");

                message.setText(emailBody.toString());

                // Envoyer le message
                Transport.send(message);

                // Afficher un message de succès sur le thread principal
                runOnUiThread(() -> {
                    Toast.makeText(TicketConfirmationActivity.this,
                            "Email de confirmation envoyé à " + customerEmail,
                            Toast.LENGTH_LONG).show();
                });

            } catch (MessagingException e) {
                e.printStackTrace();

                // Afficher un message d'erreur sur le thread principal
                runOnUiThread(() -> {
                    Toast.makeText(TicketConfirmationActivity.this,
                            "Erreur lors de l'envoi de l'email",
                            Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();

                // Afficher un message d'erreur générique sur le thread principal
                runOnUiThread(() -> {
                    Toast.makeText(TicketConfirmationActivity.this,
                            "Erreur inattendue: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}