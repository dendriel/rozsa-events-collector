package com.rozsa.demoapp.resources.dto;

import com.rozsa.demoapp.domain.PetType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PetResponse {
    private Long id;
    private String name;
    private Integer age;
    private String color;
    private PetType type;
    private String description;
}
