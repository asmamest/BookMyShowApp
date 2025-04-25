package com.example.bookmyshow.models;

public class BackendLieu {
    private Long id;
    private String name;
    private GeoPoint mapPosition;
    private int capacity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getMapPosition() {
        return mapPosition;
    }

    public void setMapPosition(GeoPoint mapPosition) {
        this.mapPosition = mapPosition;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
