package tn.enicarthage.backendandroid.models;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class GeoPoint {
    private double latitude;
    private double longitude;

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


}

