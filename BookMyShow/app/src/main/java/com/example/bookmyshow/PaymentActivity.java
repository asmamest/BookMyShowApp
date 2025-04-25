package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class PaymentActivity extends AppCompatActivity {

    private ImageButton decreaseQuantityButton;
    private ImageButton increaseQuantityButton;
    private TextView quantityTextView;
    private TextView subtotalTextView;
    private TextView serviceFeeTextView;
    private TextView totalTextView;
    private RadioGroup paymentMethodRadioGroup;
    private RadioButton creditCardRadioButton;
    private RadioButton paypalRadioButton;
    private RadioButton googlePayRadioButton;
    private CardView creditCardDetailsCard;
    private Button payNowButton;

    private int quantity = 1;
    private double ticketPrice = 199.99;
    private double serviceFeePerTicket = 2.99;

    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        setupToolbar();
        initViews();
        setupQuantityButtons();
        setupPaymentMethodRadioGroup();
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
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        quantityTextView = findViewById(R.id.quantityTextView);
        subtotalTextView = findViewById(R.id.subtotalTextView);
        serviceFeeTextView = findViewById(R.id.serviceFeeTextView);
        totalTextView = findViewById(R.id.totalTextView);
        paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);
        creditCardRadioButton = findViewById(R.id.creditCardRadioButton);
        paypalRadioButton = findViewById(R.id.paypalRadioButton);
        googlePayRadioButton = findViewById(R.id.googlePayRadioButton);
        creditCardDetailsCard = findViewById(R.id.creditCardDetailsCard);
        payNowButton = findViewById(R.id.payNowButton);
    }

    private void setupQuantityButtons() {
        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
                updatePrices();
            }
        });

        increaseQuantityButton.setOnClickListener(v -> {
            if (quantity < 10) {
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
                updatePrices();
            } else {
                Toast.makeText(PaymentActivity.this, "Maximum 10 tickets per order", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPaymentMethodRadioGroup() {
        paymentMethodRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.creditCardRadioButton) {
                creditCardDetailsCard.setVisibility(View.VISIBLE);
            } else {
                creditCardDetailsCard.setVisibility(View.GONE);
            }
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
            // Check if user is logged in before proceeding
            if (isUserLoggedIn()) {
                proceedToPayment();
            } else {
                // Redirect to login/signup
                redirectToLogin();
            }
        });
    }

    /**
     * Check if the user is currently logged in
     * @return true if logged in, false otherwise
     */
    private boolean isUserLoggedIn() {
        // Get shared preferences to check login status
        SharedPreferences prefs = getSharedPreferences("BookMyShowPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    /**
     * Redirect user to login/signup screen
     */
    private void redirectToLogin() {
        Intent loginIntent = new Intent(PaymentActivity.this, LoginActivity.class);
        // Pass total amount to show on login screen if needed
        loginIntent.putExtra("totalAmount", calculateTotal());
        loginLauncher.launch(loginIntent);
    }

    /**
     * Process payment and navigate to confirmation
     */
    private void proceedToPayment() {
        // In a real app, we would process the payment here
        // For this demo, we'll just show a success message and navigate to the confirmation screen
        Intent intent = new Intent(PaymentActivity.this, TicketConfirmationActivity.class);
        startActivity(intent);
        finish();
    }

    private void updatePrices() {
        double subtotal = ticketPrice * quantity;
        double serviceFee = serviceFeePerTicket * quantity;
        double total = subtotal + serviceFee;

        subtotalTextView.setText(String.format("$%.2f", subtotal));
        serviceFeeTextView.setText(String.format("$%.2f", serviceFee));
        totalTextView.setText(String.format("$%.2f", total));
        payNowButton.setText(String.format("Pay Now $%.2f", total));
    }

    /**
     * Calculate the total amount for payment
     * @return the total amount
     */
    private double calculateTotal() {
        return (ticketPrice * quantity) + (serviceFeePerTicket * quantity);
    }
}
