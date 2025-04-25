package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private ImageView ivTogglePassword;
    private Button btnLogin;
    private TextView tvSignup;
    private TextView tvWelcome;
    private TextView tvSubtitle;

    private boolean isPasswordVisible = false;
    private double totalAmount = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().hasExtra("totalAmount")) {
            totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
        }

        initViews();
        setupPasswordToggle();
        setupLoginButton();

        if (totalAmount > 0) {
            tvSubtitle.setText(String.format("Connectez-vous pour finaliser votre paiement de $%.2f", totalAmount));
        }

        tvSignup.setOnClickListener(v -> goToSignup(v));

        TextView btnSkip = findViewById(R.id.tvSkip);
        btnSkip.setOnClickListener(v -> {
            if (totalAmount > 0) {
                Intent intent = new Intent(LoginActivity.this, TicketConfirmationActivity.class);
                intent.putExtra("totalAmount", totalAmount);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvSubtitle = findViewById(R.id.tvSubtitle);
    }

    private void setupPasswordToggle() {
        ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;

            if (isPasswordVisible) {
                etPassword.setTransformationMethod(null);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
            }

            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                performLogin(email, password);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("L'email est requis");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Le mot de passe est requis");
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

        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void performLogin(String email, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Connexion en cours...");


        String url = "http://10.0.2.2:8080/api/auth/login";

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("email", email);
            loginData.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la création de la requête", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                loginData,
                response -> {
                    SharedPreferences prefs = getSharedPreferences("BookMyShowPrefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("isLoggedIn", true).apply();

                    Toast.makeText(LoginActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();

                    boolean fromOnboarding = getIntent().getBooleanExtra("fromOnboarding", false);

                    if (fromOnboarding) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        setResult(RESULT_OK);
                        finish();
                    }

                },
                error -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Se connecter");

                    String message;

                    Log.e("LoginError", "Erreur de connexion", error);

                    if (error.networkResponse != null) {
                        int code = error.networkResponse.statusCode;
                        byte[] data = error.networkResponse.data;

                        if (code == 401) {
                            message = "Mot de passe incorrect";
                        } else if (code == 404) {
                            message = "Utilisateur non trouvé";
                        } else {
                            message = "Erreur serveur : " + code;
                        }

                        if (data != null) {
                            try {
                                String responseBody = new String(data, "utf-8");
                                Log.e("LoginErrorBody", "Réponse serveur : " + responseBody);
                            } catch (Exception e) {
                                Log.e("LoginErrorParse", "Erreur lors de l'analyse de la réponse", e);
                            }
                        }
                    } else {
                        message = "Erreur réseau. Vérifiez la connexion.";
                    }

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
        );


        Volley.newRequestQueue(this).add(request);
    }

    public void goToSignup(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        if (totalAmount > 0) {
            intent.putExtra("totalAmount", totalAmount);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
