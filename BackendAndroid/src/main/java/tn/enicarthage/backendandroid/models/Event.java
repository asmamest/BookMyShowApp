package tn.enicarthage.backendandroid.models;

import tn.enicarthage.backendandroid.enums.CategoryEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String description;
    @Column(nullable = true)
    private String siteUrl;

    private String eventImg;

    @Enumerated(EnumType.STRING)
    private CategoryEvent category;

}
