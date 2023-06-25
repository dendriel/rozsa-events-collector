package com.rozsa.demoapp.service;

import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PetService {

    private final PetRepository petRepository;

    public Long create(final Pet pet) {
        petRepository.save(pet);

        return pet.getId();
    }

    public Optional<Pet> getByName(final String name) {
        Optional<Pet> pet = petRepository.findFirstByNameEquals(name);

        return pet;
    }

    public List<String> getDescriptions(final String name, final String color) {
        Iterable<Pet> pets;

        // do not select in production software without limiting results.
        if (StringUtils.hasText(name)) {
            pets = petRepository.findPetsByNameContains(name);
        }
        else if (StringUtils.hasText(color)) {
            pets = petRepository.findPetsByColorEquals(color);
        }
        else {
            pets = petRepository.findAll();
        }

        List<String> descriptions = new ArrayList<>();
        pets.forEach(p ->
                descriptions.add(p.getDescription())
        );

        return descriptions;
    }
}
