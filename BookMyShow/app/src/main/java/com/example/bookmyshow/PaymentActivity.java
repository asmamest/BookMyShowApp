package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private TextView eventTitleTextView;
    private TextView eventDateTextView;
    private TextView eventVenueTextView;

    // Vues pour les quantités de billets
    private LinearLayout ticketQuantitiesContainer;
    private Map<String, TicketQuantityView> ticketQuantityViews = new HashMap<>();

    private TextView subtotalTextView;
    private TextView serviceFeeTextView;
    private TextView totalTextView;
    private RadioGroup paymentMethodRadioGroup;
    private RadioButton creditCardRadioButton;
    private RadioButton paypalRadioButton;
    private RadioButton googlePayRadioButton;
    private String selectedPaymentMethod = "credit_card"; // Default to credit card

    private CardView creditCardDetailsCard;
    private Button payNowButton;
    private TextView errorMessageTextView;

    // Nouvelles vues pour la remise étudiant
    private CheckBox studentDiscountCheckbox;
    private TextView studentDiscountMessageTextView;
    private LinearLayout studentDiscountLayout;
    private TextView discountTextView;

    // Champs pour les détails de la carte
    private TextInputEditText cardNumberEditText;
    private TextInputEditText expiryDateEditText;
    private TextInputEditText cvvEditText;
    private TextInputEditText cardholderNameEditText;

    // Champ pour l'email (nouveau)
    private TextInputLayout emailInputLayout;
    private TextInputEditText emailEditText;

    // Prix des billets
    private double vipPrice = 39.99;
    private double premiumPrice = 29.99;
    private double standardPrice = 19.99;

    // Quantités de billets
    private int vipQuantity = 0;
    private int premiumQuantity = 0;
    private int standardQuantity = 0;

    // Nombre maximum de billets disponibles par type
    private int maxVipTickets = 10;
    private int maxPremiumTickets = 30;
    private int maxStandardTickets = 45;

    private double serviceFeePerTicket = 2.99;

    // Remise étudiant (10%)
    private boolean isStudentDiscount = false;
    private final double STUDENT_DISCOUNT_PERCENTAGE = 0.10;

    private ActivityResultLauncher<Intent> loginLauncher;

    // Informations de l'événement
    private long eventId;
    private int dateIndex;
    private String eventTitle;
    private String eventDate;
    private String eventLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Récupérer les informations de l'événement depuis l'intent
        eventId = getIntent().getLongExtra("eventId", -1);
        dateIndex = getIntent().getIntExtra("dateIndex", -1);
        eventTitle = getIntent().getStringExtra("eventTitle");
        eventDate = getIntent().getStringExtra("eventDate");
        eventLocation = getIntent().getStringExtra("eventLocation");

        // Récupérer les tickets disponibles
        maxVipTickets = getIntent().getIntExtra("vipTicketsAvailable", maxVipTickets);
        maxPremiumTickets = getIntent().getIntExtra("premiumTicketsAvailable", maxPremiumTickets);
        maxStandardTickets = getIntent().getIntExtra("standardTicketsAvailable", maxStandardTickets);

        // Récupérer les prix des billets
        vipPrice = getIntent().getDoubleExtra("vipPrice", vipPrice);
        premiumPrice = getIntent().getDoubleExtra("premiumPrice", premiumPrice);
        standardPrice = getIntent().getDoubleExtra("standardPrice", standardPrice);

        setupToolbar();
        initViews();
        setupEventDetails();
        setupTicketQuantities();
        setupPaymentMethodRadioGroup();
        setupCardDetailsInputs();
        setupStudentDiscount();
        setupPayNowButton();
        setupLoginLauncher();
        updatePrices();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        // Vues pour les détails de l'événement
        eventTitleTextView = findViewById(R.id.eventTitleTextView);
        eventDateTextView = findViewById(R.id.eventDateTextView);
        eventVenueTextView = findViewById(R.id.eventVenueTextView);

        // Conteneur pour les quantités de billets
        ticketQuantitiesContainer = findViewById(R.id.ticketQuantitiesContainer);

        // Message d'erreur pour la disponibilité des billets
        errorMessageTextView = findViewById(R.id.errorMessageTextView);

        // Vues pour les prix
        subtotalTextView = findViewById(R.id.subtotalTextView);
        serviceFeeTextView = findViewById(R.id.serviceFeeTextView);
        totalTextView = findViewById(R.id.totalTextView);

        // Vues pour la remise étudiant
        studentDiscountCheckbox = findViewById(R.id.studentDiscountCheckbox);
        studentDiscountMessageTextView = findViewById(R.id.studentDiscountMessageTextView);
        studentDiscountLayout = findViewById(R.id.studentDiscountLayout);
        discountTextView = findViewById(R.id.discountTextView);

        // Vues pour les méthodes de paiement
        paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);
        creditCardRadioButton = findViewById(R.id.creditCardRadioButton);
        paypalRadioButton = findViewById(R.id.paypalRadioButton);
        googlePayRadioButton = findViewById(R.id.googlePayRadioButton);
        creditCardDetailsCard = findViewById(R.id.creditCardDetailsCard);

        // Champs pour les détails de la carte
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        cardholderNameEditText = findViewById(R.id.cardholderNameEditText);

        // Champ pour l'email (nouveau)
        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailEditText = findViewById(R.id.emailEditText);

        payNowButton = findViewById(R.id.payNowButton);
    }

    private void setupEventDetails() {
        if (eventTitle != null) {
            eventTitleTextView.setText(eventTitle);
        }

        if (eventDate != null) {
            eventDateTextView.setText(eventDate);
        }

        if (eventLocation != null) {
            eventVenueTextView.setText(eventLocation);
        }
    }

    private void setupTicketQuantities() {
        // Créer les vues pour les quantités de billets
        TicketQuantityView vipQuantityView = new TicketQuantityView(this, "VIP", vipPrice, maxVipTickets);
        TicketQuantityView premiumQuantityView = new TicketQuantityView(this, "Premium", premiumPrice, maxPremiumTickets);
        TicketQuantityView standardQuantityView = new TicketQuantityView(this, "Standard", standardPrice, maxStandardTickets);

        // Ajouter les vues au conteneur
        ticketQuantitiesContainer.addView(vipQuantityView.getView());
        ticketQuantitiesContainer.addView(premiumQuantityView.getView());
        ticketQuantitiesContainer.addView(standardQuantityView.getView());

        // Stocker les vues pour y accéder plus tard
        ticketQuantityViews.put("VIP", vipQuantityView);
        ticketQuantityViews.put("Premium", premiumQuantityView);
        ticketQuantityViews.put("Standard", standardQuantityView);

        // Configurer les écouteurs pour les changements de quantité
        vipQuantityView.setOnQuantityChangedListener(quantity -> {
            vipQuantity = quantity;
            updatePrices();
            validateTicketAvailability();
        });

        premiumQuantityView.setOnQuantityChangedListener(quantity -> {
            premiumQuantity = quantity;
            updatePrices();
            validateTicketAvailability();
        });

        standardQuantityView.setOnQuantityChangedListener(quantity -> {
            standardQuantity = quantity;
            updatePrices();
            validateTicketAvailability();
        });
    }

    private void setupPaymentMethodRadioGroup() {
        // Créer des écouteurs de clic pour chaque CardView
        View creditCardContainer = findViewById(R.id.creditCardContainer);
        View paypalContainer = findViewById(R.id.paypalContainer);
        View googlePayContainer = findViewById(R.id.googlePayContainer);

        // Écouteur pour la carte de crédit
        creditCardContainer.setOnClickListener(v -> {
            creditCardRadioButton.setChecked(true);
            paypalRadioButton.setChecked(false);
            googlePayRadioButton.setChecked(false);
            creditCardDetailsCard.setVisibility(View.VISIBLE);
            selectedPaymentMethod = "credit_card";
        });

        // Écouteur pour PayPal
        paypalContainer.setOnClickListener(v -> {
            creditCardRadioButton.setChecked(false);
            paypalRadioButton.setChecked(true);
            googlePayRadioButton.setChecked(false);
            creditCardDetailsCard.setVisibility(View.GONE);
            selectedPaymentMethod = "paypal";
        });

        // Écouteur pour Google Pay
        googlePayContainer.setOnClickListener(v -> {
            creditCardRadioButton.setChecked(false);
            paypalRadioButton.setChecked(false);
            googlePayRadioButton.setChecked(true);
            creditCardDetailsCard.setVisibility(View.GONE);
            selectedPaymentMethod = "google_pay";
        });

        // Sélectionner par défaut la carte de crédit
        creditCardRadioButton.setChecked(true);
        paypalRadioButton.setChecked(false);
        googlePayRadioButton.setChecked(false);
        creditCardDetailsCard.setVisibility(View.VISIBLE);
        selectedPaymentMethod = "credit_card";
    }

    private void setupStudentDiscount() {
        studentDiscountCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isStudentDiscount = isChecked;
            studentDiscountMessageTextView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            studentDiscountLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updatePrices();
        });
    }

    private void setupCardDetailsInputs() {
        // Masquer les numéros de carte avec des astérisques
        cardNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        cardNumberEditText.setTransformationMethod(null); // Pour afficher les astérisques

        // Masquer le CVV avec des astérisques
        cvvEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // Formater la date d'expiration (MM/YY)
        expiryDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2 && before == 0) {
                    expiryDateEditText.setText(s + "/");
                    expiryDateEditText.setSelection(3);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupLoginLauncher() {
        loginLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        proceedToPayment();
                    } else {
                        Toast.makeText(PaymentActivity.this, "Authentication required to complete payment", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void setupPayNowButton() {
        payNowButton.setOnClickListener(v -> {
            // Vérifier la disponibilité des billets
            if (!validateTicketAvailability()) {
                errorMessageTextView.setVisibility(View.VISIBLE);
                return;
            } else {
                errorMessageTextView.setVisibility(View.GONE);
            }

            // Vérifier la quantité de billets sélectionnée
            if (getTotalQuantity() <= 0) {
                Toast.makeText(this, "Veuillez sélectionner au moins un billet", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérifier que la méthode de paiement a été choisie
            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(this, "Veuillez sélectionner une méthode de paiement", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérifier que l'email est valide
            if (!validateEmail()) {
                return;
            }

            // Traiter selon la méthode de paiement sélectionnée
            switch (selectedPaymentMethod) {
                case "paypal":
                    Toast.makeText(this, "Redirection vers PayPal...", Toast.LENGTH_SHORT).show();
                    Intent paypalIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/signin"));
                    startActivity(paypalIntent);
                    break;

                case "google_pay":
                    Toast.makeText(this, "Ouverture de Google Pay...", Toast.LENGTH_SHORT).show();
                    Intent gpayIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.nbu.paisa.user");
                    if (gpayIntent != null) {
                        startActivity(gpayIntent);
                    } else {
                        Toast.makeText(this, "Google Pay n'est pas installé sur cet appareil", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case "credit_card":
                    // Vérification des champs de la carte
                    if (!validateCardDetails()) {
                        return;
                    }
                    proceedToPayment();
                    break;

                default:
                    Toast.makeText(this, "Méthode de paiement non reconnue", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /**
     * Calcule le nombre total de billets sélectionnés
     */
    private int getTotalQuantity() {
        return vipQuantity + premiumQuantity + standardQuantity;
    }

    /**
     * Vérifie si l'utilisateur est actuellement connecté
     * @return true si connecté, false sinon
     */
    private boolean isUserLoggedIn() {
        // Obtenir les préférences partagées pour vérifier l'état de connexion
        SharedPreferences prefs = getSharedPreferences("BookMyShowPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    /**
     * Rediriger l'utilisateur vers l'écran de connexion/inscription
     */
    private void redirectToLogin() {
        Intent loginIntent = new Intent(PaymentActivity.this, LoginActivity.class);
        // Passer le montant total à afficher sur l'écran de connexion si nécessaire
        loginIntent.putExtra("totalAmount", calculateTotal());
        loginLauncher.launch(loginIntent);
    }

    /**
     * Vérifie la disponibilité des billets demandés
     */
    private boolean validateTicketAvailability() {
        boolean isValid = true;

        // Vérifier les billets VIP
        if (vipQuantity > maxVipTickets) {
            isValid = false;
            errorMessageTextView.setText("Seulement " + maxVipTickets + " billets VIP disponibles");
        }

        // Vérifier les billets Premium
        else if (premiumQuantity > maxPremiumTickets) {
            isValid = false;
            errorMessageTextView.setText("Seulement " + maxPremiumTickets + " billets Premium disponibles");
        }

        // Vérifier les billets Standard
        else if (standardQuantity > maxStandardTickets) {
            isValid = false;
            errorMessageTextView.setText("Seulement " + maxStandardTickets + " billets Standard disponibles");
        }

        // Vérifier le nombre total (si nécessaire)
        if (isValid && getTotalQuantity() > 10) {
            isValid = false;
            errorMessageTextView.setText("Maximum 10 billets par commande");
        }

        return isValid;
    }

    /**
     * Valider l'adresse email
     */
    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("L'adresse email est obligatoire");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Veuillez entrer une adresse email valide");
            return false;
        } else {
            emailInputLayout.setError(null);
            return true;
        }
    }

    private void proceedToPayment() {
        // Dans une vraie application, nous traiterions le paiement ici
        // Pour cette démo, nous allons simplement afficher un message de succès et naviguer vers l'écran de confirmation
        Intent intent = new Intent(PaymentActivity.this, TicketConfirmationActivity.class);

        // Passer les informations de l'événement
        intent.putExtra("eventId", eventId);
        intent.putExtra("dateIndex", dateIndex);
        intent.putExtra("eventTitle", eventTitle);
        intent.putExtra("eventDate", eventDate);
        intent.putExtra("eventLocation", eventLocation);

        // Passer les informations des billets
        intent.putExtra("vipQuantity", vipQuantity);
        intent.putExtra("premiumQuantity", premiumQuantity);
        intent.putExtra("standardQuantity", standardQuantity);
        intent.putExtra("totalAmount", calculateTotal());
        intent.putExtra("isStudentDiscount", isStudentDiscount);

        // Passer l'email du client
        intent.putExtra("customerEmail", emailEditText.getText().toString().trim());

        // Mettre à jour les stocks de billets disponibles
        // Dans une app réelle, cela se ferait côté serveur
        maxVipTickets -= vipQuantity;
        maxPremiumTickets -= premiumQuantity;
        maxStandardTickets -= standardQuantity;

        startActivity(intent);
        finish();
    }

    /**
     * Valider les détails de la carte
     */
    private boolean validateCardDetails() {
        boolean isValid = true;

        // Vérifier le numéro de carte
        String cardNumber = cardNumberEditText.getText().toString();
        if (cardNumber.length() < 16) {
            cardNumberEditText.setError("Numéro de carte invalide");
            isValid = false;
        }

        // Vérifier la date d'expiration
        String expiryDate = expiryDateEditText.getText().toString();
        if (expiryDate.length() < 5 || !expiryDate.contains("/")) {
            expiryDateEditText.setError("Date d'expiration invalide (MM/YY)");
            isValid = false;
        }

        // Vérifier le CVV
        String cvv = cvvEditText.getText().toString();
        if (cvv.length() < 3) {
            cvvEditText.setError("CVV invalide");
            isValid = false;
        }

        // Vérifier le nom du titulaire
        String cardholderName = cardholderNameEditText.getText().toString();
        if (cardholderName.isEmpty()) {
            cardholderNameEditText.setError("Nom du titulaire requis");
            isValid = false;
        }

        return isValid;
    }

    private void updatePrices() {
        double vipSubtotal = vipPrice * vipQuantity;
        double premiumSubtotal = premiumPrice * premiumQuantity;
        double standardSubtotal = standardPrice * standardQuantity;

        double subtotal = vipSubtotal + premiumSubtotal + standardSubtotal;
        double serviceFee = serviceFeePerTicket * getTotalQuantity();

        // Calculer la remise étudiant si applicable
        double discount = 0.0;
        if (isStudentDiscount) {
            discount = subtotal * STUDENT_DISCOUNT_PERCENTAGE;
            discountTextView.setText(String.format("-Dt%.2f", discount));
        }

        double total = subtotal + serviceFee - discount;

        subtotalTextView.setText(String.format("%.2f", subtotal));
        serviceFeeTextView.setText(String.format("Dt%.2f", serviceFee));
        totalTextView.setText(String.format("Dt%.2f", total));

        // Update the pay button text with the total amount
        payNowButton.setText(String.format("Payer Dt%.2f", total));
    }

    /**
     * Calculer le montant total pour le paiement
     * @return le montant total
     */
    private double calculateTotal() {
        double subtotal = (vipPrice * vipQuantity) + (premiumPrice * premiumQuantity) + (standardPrice * standardQuantity);
        double serviceFee = serviceFeePerTicket * getTotalQuantity();
        double discount = isStudentDiscount ? subtotal * STUDENT_DISCOUNT_PERCENTAGE : 0.0;
        return subtotal + serviceFee - discount;
    }

    /**
     * Classe interne pour gérer les vues de quantité de billets
     */
    private static class TicketQuantityView {
        private View view;
        private TextView typeTextView;
        private TextView priceTextView;
        private TextView availabilityTextView;
        private ImageButton decreaseButton;
        private TextView quantityTextView;
        private ImageButton increaseButton;
        private int quantity = 0;
        private int maxQuantity;
        private OnQuantityChangedListener listener;

        public TicketQuantityView(AppCompatActivity activity, String type, double price, int maxQuantity) {
            this.maxQuantity = maxQuantity;

            // Inflater la vue
            view = activity.getLayoutInflater().inflate(R.layout.item_ticket_quantity, null);

            // Initialiser les vues
            typeTextView = view.findViewById(R.id.ticketTypeTextView);
            priceTextView = view.findViewById(R.id.ticketPriceTextView);
            availabilityTextView = view.findViewById(R.id.availabilityTextView);
            decreaseButton = view.findViewById(R.id.decreaseQuantityButton);
            quantityTextView = view.findViewById(R.id.quantityTextView);
            increaseButton = view.findViewById(R.id.increaseQuantityButton);

            // Configurer les vues
            typeTextView.setText(type);
            priceTextView.setText(String.format("Dt%.2f", price));
            quantityTextView.setText(String.valueOf(quantity));

            // Update availability text
            availabilityTextView.setText("Disponible: " + maxQuantity);

            // Configurer les écouteurs
            decreaseButton.setOnClickListener(v -> {
                if (quantity > 0) {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                    if (listener != null) {
                        listener.onQuantityChanged(quantity);
                    }
                }
            });

            increaseButton.setOnClickListener(v -> {
                if (quantity < maxQuantity && quantity < 10) {
                    quantity++;
                    quantityTextView.setText(String.valueOf(quantity));
                    if (listener != null) {
                        listener.onQuantityChanged(quantity);
                    }
                } else if (quantity >= maxQuantity) {
                    Toast.makeText(activity, "Maximum " + maxQuantity + " billets disponibles", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Maximum 10 billets par type", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public View getView() {
            return view;
        }

        public void setOnQuantityChangedListener(OnQuantityChangedListener listener) {
            this.listener = listener;
        }

        public interface OnQuantityChangedListener {
            void onQuantityChanged(int quantity);
        }
    }
}