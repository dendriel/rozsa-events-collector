package com.rozsa.demoapp.resources;

import com.rozsa.demoapp.domain.Owner;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.resources.dto.OwnerRequest;
import com.rozsa.demoapp.resources.mapper.OwnerMapper;
import com.rozsa.demoapp.service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
