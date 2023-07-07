package com.rozsa.demoapp.resources;

import com.rozsa.demoapp.domain.Owner;
import com.rozsa.demoapp.resources.dto.OwnerRequest;
import com.rozsa.demoapp.resources.dto.OwnerResponse;
import com.rozsa.demoapp.resources.mapper.OwnerMapper;
import com.rozsa.demoapp.service.OwnerService;
import com.rozsa.events.collector.annotations.BeginCollecting;
import com.rozsa.events.collector.annotations.Collect;
import com.rozsa.events.collector.annotations.CollectParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.rozsa.demoapp.configuration.collector.OwnerFavouritePetFlowKeys.OWNER_FAV_PET_FLOW;
import static com.rozsa.demoapp.configuration.collector.OwnerFavouritePetFlowKeys.OWNER_ID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/owner")
@RestController
public class OwnerResource {

    private final OwnerService ownerService;

    private final OwnerMapper ownerMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Long createOwner(@RequestBody OwnerRequest request) {

        Owner owner = ownerMapper.from(request);

        Long ownerId = ownerService.create(owner);

        return ownerId;
    }

    @BeginCollecting(flow = OWNER_FAV_PET_FLOW)
    @Collect
    @GetMapping("/{id}")
    ResponseEntity<OwnerResponse> getOwner(
            @PathVariable
            @CollectParameter(flow = OWNER_FAV_PET_FLOW, key = OWNER_ID) Long id) {

        Optional<Owner> optOwner = ownerService.findById(id);

        if (optOwner.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OwnerResponse response = ownerMapper.toResponse(optOwner.get());

        return ResponseEntity.ok(response);
    }
}
