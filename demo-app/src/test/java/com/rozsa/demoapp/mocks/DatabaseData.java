package com.rozsa.demoapp.mocks;

import com.rozsa.demoapp.domain.Owner;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rozsa.demoapp.domain.PetType.*;
import static com.rozsa.demoapp.domain.PetType.CAT;

@Service
public class DatabaseData {

    @Autowired
    private OwnerRepository ownerRepository;

    public static final List<Pet> PETS_DB = List.of(
            Pet.builder().id(1L).name("Trinity").age(6).color("Yellow").type(DOG).description("Trinity is a dog of color Yellow and is 6 years old")
                    .owner(Owner.builder().id(3L).build())
                    .build(),
            Pet.builder().id(2L).name("Sebastian").age(29).color("Green").type(TURTLE).description("Sebastian is a turtle of color Green and is 29 years old")
                    .owner(Owner.builder().id(2L).build())
                    .build(),
            Pet.builder().id(3L).name("Merlin").age(1).color("Gray").type(CAT).description("Merlin is a cat of color Grey and is 1 year old")
                    .owner(Owner.builder().id(1L).build())
                    .build(),
            Pet.builder().id(4L).name("Toby").age(1).color("White").type(CAT).description("Toby is a cat of color White and is 2 year old")
                    .owner(Owner.builder().id(1L).build())
                    .build()
    );

    public Owner getOwnerById(final Long id) {
        return ownerRepository.findById(id).orElseThrow();
    }
}
