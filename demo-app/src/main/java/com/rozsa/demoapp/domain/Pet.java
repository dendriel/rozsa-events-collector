package com.rozsa.demoapp.domain;

import com.rozsa.events.collector.annotations.CollectField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.rozsa.demoapp.configuration.collector.OwnerFavouritePetFlowKeys.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Pet {
    @CollectField(flow = OWNER_FAV_PET_FLOW, key = OWNER_FAV_PET_ID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @CollectField(flow = OWNER_FAV_PET_FLOW, key = OWNER_FAV_PET_NAME)
    private String name;

    private Integer age;

    private String color;

    @Enumerated(EnumType.STRING)
    private PetType type;

    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id")
    private Owner owner;

    @Column(columnDefinition = "boolean default false")
    private boolean isFavourite;
}
