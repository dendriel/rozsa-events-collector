package com.rozsa.demoapp.resources.dto;

import com.rozsa.demoapp.domain.Gender;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OwnerRequest {
    private String name;
    private Integer age;
    private Gender gender;
}
