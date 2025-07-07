package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvMemberSince;
    //private SwitchMaterial switchDarkMode;
    private CardView cardEditProfile;
    private CardView cardPaymentMethods;
    private CardView cardTicketHistory;
    private CardView cardNotifications;
    private CardView cardHelp;
    private CardView cardLogout;
    private Button btnEditProfile;

    private SharedPreferences prefs;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences("BookMyShowPrefs", MODE_PRIVATE);

        initViews();
        setupImagePicker();
        loadUserData();
        // setupDarkModeSwitch();
        setupCardClickListeners();
        setupEditProfileButton();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        setupCardClickListeners();
    }


    private void initViews() {
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        //switchDarkMode = findViewById(R.id.switchDarkMode);
        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardPaymentMethods = findViewById(R.id.cardPaymentMethods);
        cardTicketHistory = findViewById(R.id.cardTicketHistory);
        cardNotifications = findViewById(R.id.cardNotifications);
        cardHelp = findViewById(R.id.cardHelp);
        cardLogout = findViewById(R.id.cardLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            ivProfilePic.setImageURI(selectedImageUri);
                            prefs.edit().putString("profileImageUri", selectedImageUri.toString()).apply();
                        }
                    }
                }
        );

        ivProfilePic.setOnClickListener(v -> {
            showImageSourceDialog();
        });
    }
    private boolean isUserLoggedIn() {
        return prefs.getBoolean("isLoggedIn", false);
    }


    private void loadUserData() {
        boolean isLoggedIn = isUserLoggedIn();

        if (isLoggedIn) {
            String imageUriString = prefs.getString("profileImageUri", null);
            if (imageUriString != null) {
                try {
                    Uri imageUri = Uri.parse(imageUriString);
                    ivProfilePic.setImageURI(imageUri);
                } catch (Exception e) {
                    Log.e("ProfileActivity", "Invalid profile image URI", e);
                }
            }

            String email = prefs.getString("userEmail", "foulen.benfelten@gmail.com");
            String username = prefs.getString("userName", email.split("@")[0]);
            if (username == null || username.trim().isEmpty()) {
                username = email.split("@")[0];
                prefs.edit().putString("userName", username).apply();
            }

            String memberSince = prefs.getString("memberSince", "—");

            tvUsername.setText(username);
            tvEmail.setText(email);
            tvMemberSince.setText(String.format("Membre depuis %s", memberSince));

            btnEditProfile.setVisibility(View.GONE);

        }else {
            tvUsername.setText("Foulen Ben Felten");
            tvEmail.setText("foulen.benfelten@gmail.com");
            tvMemberSince.setText("Membre depuis —");
            btnEditProfile.setVisibility(View.VISIBLE);
            btnEditProfile.setText("Se connecter");
        }
    }

    /*private void setupDarkModeSwitch() {
        boolean isDarkMode = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ThemeManager.setTheme(isChecked);
            prefs.edit().putBoolean("darkMode", isChecked).apply();
        });
    }*/

    private void setupCardClickListeners() {

        boolean isLoggedIn = isUserLoggedIn();

        cardEditProfile.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        cardPaymentMethods.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        cardTicketHistory.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        cardNotifications.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        cardHelp.setVisibility(View.VISIBLE); // On peut toujours accéder à l’aide
        cardLogout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);

        if (isLoggedIn) {
            cardEditProfile.setOnClickListener(v -> {
                showEditProfileBottomSheet();
            });

            cardPaymentMethods.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, PaymentMethodsActivity.class);
                startActivity(intent);
            });

            cardTicketHistory.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, TicketHistoryActivity.class);
                startActivity(intent);
            });

            cardNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, NotificationsSettingsActivity.class);
                startActivity(intent);
            });

            cardHelp.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, HelpSupportActivity.class);
                startActivity(intent);
            });

            cardLogout.setOnClickListener(v -> {
                showLogoutConfirmationDialog();
            });
        }
    }

    private void setupEditProfileButton() {
        btnEditProfile.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                showEditProfileBottomSheet();
            } else {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showEditProfileBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_profile, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView tvCurrentEmail = bottomSheetView.findViewById(R.id.tvCurrentEmail);
        EditText etUsername = bottomSheetView.findViewById(R.id.etUsername);
        EditText etPhone = bottomSheetView.findViewById(R.id.etPhone);
        EditText etBirthDate = bottomSheetView.findViewById(R.id.etBirthDate);
        etPhone.setText(prefs.getString("userPhone", ""));
        etBirthDate.setText(prefs.getString("userBirthDate", ""));

        Button btnSaveChanges = bottomSheetView.findViewById(R.id.btnSaveChanges);
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);

        tvCurrentEmail.setText(tvEmail.getText());
        etUsername.setText(tvUsername.getText());

        btnSaveChanges.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();

            if (!newUsername.isEmpty()) {
                prefs.edit()
                        .putString("userName", newUsername)
                        .putString("userPhone", phone)
                        .putString("userBirthDate", birthDate)
                        .apply();

                tvUsername.setText(newUsername);
                Toast.makeText(ProfileActivity.this, "Profil mis à jour", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(ProfileActivity.this, "Le nom d'utilisateur ne peut pas être vide", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void showLogoutConfirmationDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_logout_confirmation, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button btnConfirmLogout = bottomSheetView.findViewById(R.id.btnConfirmLogout);
        Button btnCancelLogout = bottomSheetView.findViewById(R.id.btnCancelLogout);

        btnConfirmLogout.setOnClickListener(v -> {
            prefs.edit().putBoolean("isLoggedIn", false).apply();
/*
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
            finish();
        });

        btnCancelLogout.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void showImageSourceDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_image_picker, null);
        dialog.setContentView(view);

        Button btnCamera = view.findViewById(R.id.btnCamera);
        Button btnGallery = view.findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(v -> {
            dialog.dismiss();
            openCamera();
        });

        btnGallery.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        dialog.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            imagePickerLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "Caméra non disponible", Toast.LENGTH_SHORT).show();
        }
    }



}
