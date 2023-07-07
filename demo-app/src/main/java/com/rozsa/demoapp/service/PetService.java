package com.rozsa.demoapp.service;

import com.rozsa.demoapp.configuration.collector.DefaultFlowKeys;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.repository.PetRepository;
import com.rozsa.demoapp.service.model.PetFilter;
import com.rozsa.events.collector.annotations.BeginCollecting;
import com.rozsa.events.collector.annotations.Collect;
import com.rozsa.events.collector.annotations.CollectParameter;
import com.rozsa.events.collector.annotations.CollectReturn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.rozsa.demoapp.configuration.collector.FindFavouritePetFlowKeys.FIND_FAV_PET_FLOW;
import static com.rozsa.demoapp.configuration.collector.FindFavouritePetFlowKeys.OWNER_ID;

@RequiredArgsConstructor
@Service
public class PetService {

    private final PetRepository petRepository;

    public Long create(final Pet pet) {
        petRepository.save(pet);

        return pet.getId();
    }

    @Collect
    public Optional<Pet> getByName(@CollectParameter(DefaultFlowKeys.PET_NAME) final String name) {
        Optional<Pet> pet = petRepository.findFirstByNameEquals(name);

        return pet;
    }

    @Collect
    public List<String> getDescriptions(
            @CollectParameter("nameFilter") final String name,
            @CollectParameter("colorFilter") final String color
    ) {
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

    @Collect
    public Optional<Pet> findPetByFilters(@CollectParameter(scanFields = true) final PetFilter filter) {
        Optional<Pet> pet = petRepository
                .findPetByNameEqualsAndColorEqualsAndAgeEqualsAndTypeEquals(
                        filter.getName(),
                        filter.getColor(),
                        filter.getAge(),
                        filter.getType()
                );

        return pet;
    }

    @BeginCollecting(flow = FIND_FAV_PET_FLOW)
    @Collect
    @CollectReturn(flow = FIND_FAV_PET_FLOW, scanFields = true)
    public Optional<Pet> findFavouritePetByOwnerId(
            @CollectParameter(flow = FIND_FAV_PET_FLOW, key = OWNER_ID) final Long ownerId
    ) {
        return petRepository.findFirstByOwnerIdAndIsFavourite(ownerId, true);
    }
}
