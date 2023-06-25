package com.rozsa.demoapp.resources.mapper;

import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.resources.dto.PetRequest;
import org.springframework.stereotype.Component;

@Component
public class PetMapper {

    public Pet mapFrom(PetRequest request) {
        String description = String.format("%s is a %s of color %s and is %d years old",
                request.getName(), request.getType().toString().toLowerCase(), request.getColor(), request.getAge());

        return Pet.builder()
                .name(request.getName())
                .age(request.getAge())
                .color(request.getColor())
                .type(request.getType())
                .description(description)
                .build();
    }
}
