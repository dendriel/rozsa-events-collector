package com.rozsa.demoapp.domain;

import com.rozsa.events.collector.annotations.CollectField;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static com.rozsa.demoapp.configuration.collector.OwnerFavouritePetFlowKeys.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CollectField(flow = OWNER_FAV_PET_FLOW, key = OWNER_NAME)
    private String name;

    @CollectField(flow = OWNER_FAV_PET_FLOW, key = OWNER_AGE)
    private Integer age;

    @CollectField(flow = OWNER_FAV_PET_FLOW, key = OWNER_GENDER)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Pet> pets;

    @CollectField(scanFields = true)
    @Transient
    private Pet favouritePet;
}

