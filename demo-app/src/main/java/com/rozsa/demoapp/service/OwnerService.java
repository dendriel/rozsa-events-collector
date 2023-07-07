package com.rozsa.demoapp.service;

import com.rozsa.demoapp.domain.Owner;
import com.rozsa.demoapp.repository.OwnerRepository;
import com.rozsa.events.collector.EventsCollectorManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    private final EventsCollectorManager eventsCollectorManager;


    public Long create(final Owner owner) {
        final Owner savedOwner = ownerRepository.save(owner);
        return savedOwner.getId();
    }
}
