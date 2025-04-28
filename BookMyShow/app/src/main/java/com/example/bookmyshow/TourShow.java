package com.example.bookmyshow;

import java.util.ArrayList;
import java.util.List;

public class TourShow {
    private int id;
    private String title;
    private String category;
    private String description;
    private int imageResId;
    private List<TourDate> tourDates;

    public TourShow() {
        this.tourDates = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public List<TourDate> getTourDates() {
        return tourDates;
    }

    public void setTourDates(List<TourDate> tourDates) {
        this.tourDates = tourDates;
    }

    public void addTourDate(TourDate tourDate) {
        if (this.tourDates == null) {
            this.tourDates = new ArrayList<>();
        }
        this.tourDates.add(tourDate);
    }

    public int getDateCount() {
        return tourDates != null ? tourDates.size() : 0;
    }

    public int getUniqueVenueCount() {
        if (tourDates == null || tourDates.isEmpty()) {
            return 0;
        }

        List<String> uniqueVenues = new ArrayList<>();
        for (TourDate date : tourDates) {
            String venueKey = date.getVenue() + "-" + date.getCity();
            if (!uniqueVenues.contains(venueKey)) {
                uniqueVenues.add(venueKey);
            }
        }
        return uniqueVenues.size();
    }
}
