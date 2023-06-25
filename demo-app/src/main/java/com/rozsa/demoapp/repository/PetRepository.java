package com.rozsa.demoapp.repository;

import com.rozsa.demoapp.domain.Pet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends CrudRepository<Pet, Integer> {

    List<Pet> findPetsByNameContains(String name);

    List<Pet> findPetsByColorEquals(String color);

    Optional<Pet> findFirstByNameEquals(String name);
}
