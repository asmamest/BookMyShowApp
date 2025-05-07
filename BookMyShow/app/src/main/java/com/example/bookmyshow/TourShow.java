package com.example.bookmyshow;

import java.util.ArrayList;
import java.util.List;

public class TourShow {
    private int id;
    private String title;
    private String description;
    private String category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        this.tourDates.add(tourDate);
    }
}
