package com.example.bookmyshow;


import java.util.List;

public class TourShow {
    private int id;
    private String title;
    private String category;
    private int imageResId;
    private String description;
    private List<TourDate> tourDates;

    public TourShow() {
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

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TourDate> getTourDates() {
        return tourDates;
    }

    public void setTourDates(List<TourDate> tourDates) {
        this.tourDates = tourDates;
    }
}
