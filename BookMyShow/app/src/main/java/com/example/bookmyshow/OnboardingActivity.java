package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private Button btnGetStarted;
    private TextView tvSkip;
    private OnboardingAdapter adapter;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean("onboarding_completed", false)
                .apply();

        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_onboarding);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("onboarding_completed", false)) {
            navigateToHome();
            return;
        }

        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        tvSkip = findViewById(R.id.tvSkip);

       setupOnboardingItems();

        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);

                if (position == adapter.getItemCount() - 1) {
                    btnGetStarted.setVisibility(View.VISIBLE);
                    tvSkip.setVisibility(View.VISIBLE);
                } else {
                    btnGetStarted.setVisibility(View.GONE);
                    tvSkip.setVisibility(View.VISIBLE);
                }
            }
        });

        setupIndicators();
        setCurrentIndicator(0);

        btnGetStarted.setOnClickListener(v -> {
            navigateToLogin();
        });

        tvSkip.setOnClickListener(v -> {
            navigateToHome();
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem item1 = new OnboardingItem();
        item1.setTitle("Discover Events");
        item1.setDescription("Find the best movies, concerts, plays and events happening around you");
        item1.setImageResId(R.drawable.onboarding_img1);

        OnboardingItem item2 = new OnboardingItem();
        item2.setTitle("Book Tickets Easily");
        item2.setDescription("Select your seats, choose your payment method and get your tickets instantly");
        item2.setImageResId(R.drawable.jj);

        OnboardingItem item3 = new OnboardingItem();
        item3.setTitle("Enjoy the Experience");
        item3.setDescription("Access your tickets anytime, get directions to the venue and share with friends");
        item3.setImageResId(R.drawable.women_talking_movie);

        onboardingItems.add(item1);
        onboardingItems.add(item2);
        onboardingItems.add(item3);

        adapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupIndicators() {
        dots = new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getDrawable(R.drawable.indicator_inactive));
            dots[i].setLayoutParams(params);
            dotsLayout.addView(dots[i]);
        }
    }

    private void setCurrentIndicator(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setImageDrawable(getDrawable(R.drawable.indicator_active));
            } else {
                dots[i].setImageDrawable(getDrawable(R.drawable.indicator_inactive));
            }
        }
    }

    private void navigateToHome() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("onboarding_completed", true).apply();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
         prefs.edit().putBoolean("onboarding_completed", true).apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("fromOnboarding", true);
        startActivity(intent);
        finish();
    }

    public static class OnboardingItem {
        private int imageResId;
        private String title;
        private String description;

        public int getImageResId() {
            return imageResId;
        }

        public void setImageResId(int imageResId) {
            this.imageResId = imageResId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public class OnboardingAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

        private List<OnboardingItem> onboardingItems;

        public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
            this.onboardingItems = onboardingItems;
        }

        @NonNull
        @Override
        public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OnboardingViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.layout_onboarding_page, parent, false
                    )
            );
        }

        @Override
        public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
            holder.bind(onboardingItems.get(position));
        }

        @Override
        public int getItemCount() {
            return onboardingItems.size();
        }

        class OnboardingViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {

            private ImageView imageView;
            private TextView titleTextView;
            private TextView descriptionTextView;

            public OnboardingViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageOnboarding);
                titleTextView = itemView.findViewById(R.id.textTitle);
                descriptionTextView = itemView.findViewById(R.id.textDescription);
            }

            void bind(OnboardingItem onboardingItem) {
                Glide.with(imageView.getContext())
                        .load(onboardingItem.getImageResId())
                        .override(800, 600)
                        .into(imageView);
                imageView.setImageResource(onboardingItem.getImageResId());
                titleTextView.setText(onboardingItem.getTitle());
                descriptionTextView.setText(onboardingItem.getDescription());
            }
        }
    }
}