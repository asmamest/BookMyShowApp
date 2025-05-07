package com.example.bookmyshow.models;


import com.example.bookmyshow.enums.CategoryEvent;

public class BackendEvent {
    private Long id;
    private String title;
    private String description;
    private String siteUrl;
    private String eventImg;
    private CategoryEvent category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getEventImg() {
        return eventImg;
    }

    public void setEventImg(String eventImg) {
        this.eventImg = eventImg;
    }

    public CategoryEvent getCategory() {
        return category;
    }

    public void setCategory(CategoryEvent category) {
        this.category = category;
    }
}
