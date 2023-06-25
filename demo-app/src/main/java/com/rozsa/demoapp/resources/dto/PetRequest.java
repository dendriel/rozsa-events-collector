package com.rozsa.demoapp.resources.dto;

import com.rozsa.demoapp.domain.PetType;
import lombok.Data;

@Data
public class PetRequest {
    private String name;
    private Integer age;
    private String color;
    private PetType type;
}
