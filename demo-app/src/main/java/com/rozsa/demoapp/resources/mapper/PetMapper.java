package com.rozsa.demoapp.resources.mapper;

import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.resources.dto.PetRequest;
import com.rozsa.demoapp.resources.dto.PetResponse;
import com.rozsa.demoapp.service.model.PetFilter;
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

    public PetResponse mapTo(Pet pet) {
        return PetResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .age(pet.getAge())
                .color(pet.getColor())
                .type(pet.getType())
                .description(pet.getDescription())
                .build();
    }

    public PetFilter mapToFilter(PetRequest request) {
        return PetFilter.builder()
                .name(request.getName())
                .age(request.getAge())
                .color(request.getColor())
                .type(request.getType())
                .build();
    }
}
