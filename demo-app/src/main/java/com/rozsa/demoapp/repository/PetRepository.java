package com.rozsa.demoapp.repository;

import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.domain.PetType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends CrudRepository<Pet, Long> {

    List<Pet> findPetsByNameContains(String name);

    List<Pet> findPetsByColorEquals(String color);

    Optional<Pet> findFirstByNameEquals(String name);

    Optional<Pet> findPetByNameEqualsAndColorEqualsAndAgeEqualsAndTypeEquals(String name, String color, Integer age, PetType type);

    Optional<Pet> findFirstByOwnerIdAndIsFavourite(Long id, Boolean isFavourite);
}
