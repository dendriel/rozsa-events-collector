package com.rozsa.demoapp.resources.dto;

import com.rozsa.demoapp.domain.Gender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerResponse {
    private Long id;
    private String name;
    private Integer age;
    private Gender gender;
    private PetResponse favouritePet;
}
