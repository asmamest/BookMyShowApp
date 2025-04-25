package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsActivity extends AppCompatActivity {

    private RecyclerView paymentMethodsRecyclerView;
    private FloatingActionButton addPaymentMethodFab;
    private View emptyStateView;
    private Button addFirstPaymentMethodButton;

    private List<PaymentMethod> paymentMethods = new ArrayList<>();
    private PaymentMethodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment Methods");

        // Initialize views
        paymentMethodsRecyclerView = findViewById(R.id.paymentMethodsRecyclerView);
        addPaymentMethodFab = findViewById(R.id.addPaymentMethodFab);
        emptyStateView = findViewById(R.id.emptyStateView);
        addFirstPaymentMethodButton = findViewById(R.id.addFirstPaymentMethodButton);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        addPaymentMethodFab.setOnClickListener(v -> showAddPaymentMethodDialog());
        addFirstPaymentMethodButton.setOnClickListener(v -> showAddPaymentMethodDialog());

        // Load payment methods
        loadPaymentMethods();
    }

    private void setupRecyclerView() {
        paymentMethodsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PaymentMethodAdapter(paymentMethods, this::onPaymentMethodClick, this::onPaymentMethodDelete);
        paymentMethodsRecyclerView.setAdapter(adapter);
    }

    private void loadPaymentMethods() {
        // In a real app, this would load from a database or API
        // For demo purposes, we'll add some sample payment methods

        // Clear existing data
        paymentMethods.clear();

        // Add sample data (comment out to test empty state)
        paymentMethods.add(new PaymentMethod("Visa", "•••• •••• •••• 4242", "12/25", R.drawable.ic_visa));
        paymentMethods.add(new PaymentMethod("Mastercard", "•••• •••• •••• 5555", "09/24", R.drawable.ic_mastercard));

        // Update UI
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (paymentMethods.isEmpty()) {
            paymentMethodsRecyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            paymentMethodsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    private void showAddPaymentMethodDialog() {
        // In a real app, this would show a form to add a new payment method
        // For demo purposes, we'll just show a dialog with options

        final String[] paymentTypes = {"Credit Card", "Debit Card", "PayPal", "Google Pay"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Payment Method")
                .setItems(paymentTypes, (dialog, which) -> {
                    // Simulate adding a new payment method
                    String type = paymentTypes[which];
                    Toast.makeText(this, type + " selected", Toast.LENGTH_SHORT).show();

                    // In a real app, this would navigate to a form
                    // For demo, we'll just add a dummy payment method
                    if (which == 0 || which == 1) { // Credit or Debit card
                        PaymentMethod newMethod = new PaymentMethod(
                                type,
                                "•••• •••• •••• " + (1000 + (int)(Math.random() * 9000)),
                                "01/" + (24 + (int)(Math.random() * 5)),
                                which == 0 ? R.drawable.ic_visa : R.drawable.ic_mastercard
                        );
                        paymentMethods.add(newMethod);
                        adapter.notifyItemInserted(paymentMethods.size() - 1);
                        updateEmptyState();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onPaymentMethodClick(PaymentMethod paymentMethod, int position) {
        // In a real app, this would open the payment method details
        Toast.makeText(this, "Edit " + paymentMethod.getType(), Toast.LENGTH_SHORT).show();
    }

    private void onPaymentMethodDelete(PaymentMethod paymentMethod, int position) {
        // Confirm deletion
        new AlertDialog.Builder(this)
                .setTitle("Remove Payment Method")
                .setMessage("Are you sure you want to remove this " + paymentMethod.getType() + "?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    // Remove the payment method
                    paymentMethods.remove(position);
                    adapter.notifyItemRemoved(position);
                    updateEmptyState();
                    Toast.makeText(this, "Payment method removed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Model class for payment methods
    public static class PaymentMethod {
        private String type;
        private String number;
        private String expiry;
        private int iconResId;

        public PaymentMethod(String type, String number, String expiry, int iconResId) {
            this.type = type;
            this.number = number;
            this.expiry = expiry;
            this.iconResId = iconResId;
        }

        public String getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }

        public String getExpiry() {
            return expiry;
        }

        public int getIconResId() {
            return iconResId;
        }
    }

    // Adapter for payment methods
    public static class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {

        private List<PaymentMethod> paymentMethods;
        private OnPaymentMethodClickListener clickListener;
        private OnPaymentMethodDeleteListener deleteListener;

        public interface OnPaymentMethodClickListener {
            void onPaymentMethodClick(PaymentMethod paymentMethod, int position);
        }

        public interface OnPaymentMethodDeleteListener {
            void onPaymentMethodDelete(PaymentMethod paymentMethod, int position);
        }

        public PaymentMethodAdapter(List<PaymentMethod> paymentMethods,
                                    OnPaymentMethodClickListener clickListener,
                                    OnPaymentMethodDeleteListener deleteListener) {
            this.paymentMethods = paymentMethods;
            this.clickListener = clickListener;
            this.deleteListener = deleteListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_payment_method, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PaymentMethod paymentMethod = paymentMethods.get(position);
            holder.bind(paymentMethod, position);
        }

        @Override
        public int getItemCount() {
            return paymentMethods.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            // In a real app, you would have actual view references here

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize views
            }

            public void bind(PaymentMethod paymentMethod, int position) {
                // Bind data to views
                itemView.setOnClickListener(v ->
                        clickListener.onPaymentMethodClick(paymentMethod, position));

                // Setup delete button
                itemView.findViewById(R.id.deleteButton).setOnClickListener(v ->
                        deleteListener.onPaymentMethodDelete(paymentMethod, position));
            }
        }
    }
}