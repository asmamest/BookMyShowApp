package tn.enicarthage.backendandroid.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.enicarthage.backendandroid.models.GeoPoint;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LieuDTO {
    private Long id;
    private String name;
    private GeoPoint mapPosition;
    private int capacity;
}
