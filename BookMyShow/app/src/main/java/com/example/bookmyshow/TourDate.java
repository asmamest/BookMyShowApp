package com.example.bookmyshow;

public class TourDate {
    private String day;
    private String month;
    private String year;
    private String venue;
    private String city;
    private String country;
    private String time;

    public TourDate(String day, String month, String year, String venue, String city, String country, String time) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.venue = venue;
        this.city = city;
        this.country = country;
        this.time = time;

    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getVenue() {
        return venue;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getTime() {
        return time;
    }
}
