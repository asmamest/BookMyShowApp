package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private ImageView ivTogglePassword;
    private ImageView ivToggleConfirmPassword;
    private Button btnSignup;
    private TextView tvLogin;
    private TextView tvWelcome;
    private TextView tvSubtitle;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Get total amount from intent if available
        if (getIntent().hasExtra("totalAmount")) {
            totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
        }

        initViews();
        setupPasswordToggles();
        setupSignupButton();

        // Update subtitle if coming from payment
        if (totalAmount > 0) {
            tvSubtitle.setText(String.format("Inscrivez-vous pour finaliser votre paiement de $%.2f", totalAmount));
        }

        // Setup login text click
        tvLogin.setOnClickListener(v -> finish()); // Go back to login
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvSubtitle = findViewById(R.id.tvSubtitle);
    }

    private void setupPasswordToggles() {
        ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;

            if (isPasswordVisible) {
                // Show password
                etPassword.setTransformationMethod(null);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                // Hide password
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
            }

            // Move cursor to the end of text
            etPassword.setSelection(etPassword.getText().length());
        });

        ivToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;

            if (isConfirmPasswordVisible) {
                // Show password
                etConfirmPassword.setTransformationMethod(null);
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                // Hide password
                etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye);
            }

            // Move cursor to the end of text
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });
    }

    private void setupSignupButton() {
        btnSignup.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateInputs(email, password, confirmPassword)) {
                performSignup(email, password);
            }
        });
    }

    private boolean validateInputs(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            etEmail.setError("L'email est requis");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Le mot de passe est requis");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("La confirmation du mot de passe est requise");
            return false;
        }

        if (!isValidEmail(email)) {
            etEmail.setError("Format d'email invalide");
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Le mot de passe doit contenir au moins 6 caractères");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Les mots de passe ne correspondent pas");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void performSignup(String email, String password) {

        // Simulate network delay
        btnSignup.setEnabled(false);
        btnSignup.setText("Création du compte...");
        String url = "http://10.0.2.2:8080/api/auth/signup";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("confirmPassword", password); // On utilise le même mot de passe ici
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur JSON", Toast.LENGTH_SHORT).show();
            btnSignup.setEnabled(true);
            btnSignup.setText("S'inscrire");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    // Succès : on sauvegarde et redirige comme avant
                    SharedPreferences prefs = getSharedPreferences("BookMyShowPrefs", MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean("isLoggedIn", true)
                            .putString("userEmail", email)
                            .apply();

                    Toast.makeText(SignupActivity.this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();

                    if (totalAmount > 0) {
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finishAffinity();
                    } else {
                        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                },
                error -> {
                    btnSignup.setEnabled(true);
                    btnSignup.setText("S'inscrire");

                    String errorMsg = "Erreur de création de compte";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg = new String(error.networkResponse.data);
                    }
                    Log.e("SIGNUP_ERROR", errorMsg);
                    Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        // Just go back to login screen
        super.onBackPressed();
    }
}
