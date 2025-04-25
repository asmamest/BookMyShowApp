package tn.enicarthage.backendandroid.dto.request;

import tn.enicarthage.backendandroid.enums.CategoryEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private String siteUrl;
    private String eventImg;
    private CategoryEvent category;
}
