package com.rozsa.demoapp.service;

import com.rozsa.demoapp.domain.Owner;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.repository.OwnerRepository;
import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.annotations.Collect;
import com.rozsa.events.collector.annotations.CollectReturn;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

import static com.rozsa.demoapp.configuration.collector.OwnerFavouritePetFlowKeys.OWNER_FAV_PET_FLOW;


@RequiredArgsConstructor
@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    private final PetService petService;

    private final EventsCollectorManager eventsCollectorManager;


    public Long create(final Owner owner) {
        final Owner savedOwner = ownerRepository.save(owner);
        return savedOwner.getId();
    }

    @CollectReturn(flow = OWNER_FAV_PET_FLOW, scanFields = true)
    public Optional<Owner> findById(final Long id) {
        Optional<Owner> optOwner = ownerRepository.findById(id);

        if (optOwner.isPresent()) {
            Owner owner = optOwner.get();
            Optional<Pet> favouritePet = petService.findFavouritePetByOwnerId(owner.getId());
            owner.setFavouritePet(favouritePet.get());
        }

        return optOwner;
    }
}
