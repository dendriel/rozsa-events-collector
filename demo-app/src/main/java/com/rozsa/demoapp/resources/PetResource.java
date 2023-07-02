package com.rozsa.demoapp.resources;

import com.rozsa.demoapp.configuration.collector.PetFilterFlowKeys;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.domain.PetType;
import com.rozsa.demoapp.resources.dto.PetRequest;
import com.rozsa.demoapp.resources.dto.PetResponse;
import com.rozsa.demoapp.resources.mapper.PetMapper;
import com.rozsa.demoapp.service.PetService;
import com.rozsa.demoapp.service.model.PetFilter;
import com.rozsa.events.collector.annotations.BeginCollecting;
import com.rozsa.events.collector.annotations.CollectReturn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/pet")
public class PetResource {

    private final PetService petService;

    private final PetMapper petMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createPet(@RequestBody PetRequest petRequest) {
        log.info("Create Pet: {}", petRequest);

        Pet pet = petMapper.mapFrom(petRequest);

        Long id = petService.create(pet);
        return id;
    }

    @BeginCollecting
    @GetMapping
    public ResponseEntity<PetResponse> getPetByName(@RequestParam(required = false) String name) {
        Optional<Pet> optPet = petService.getByName(name);
        if (optPet.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PetResponse response = petMapper.mapTo(optPet.get());

        return ResponseEntity.ok(response);
    }

    @GetMapping( "/all")
    public List<String> getPetsDescription(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color
    ) {
        return petService.getDescriptions(name, color);
    }

    @BeginCollecting(flow = PetFilterFlowKeys.FLOW_NAME)
    @CollectReturn(flow = PetFilterFlowKeys.FLOW_NAME, collector = PetFilterFlowKeys.PET_RESPONSE_COLLECTOR)
    @GetMapping("/find")
    public ResponseEntity<PetResponse> findPetByFilter(
            @RequestParam String name, @RequestParam String color, @RequestParam Integer age, @RequestParam PetType type
    ) {
        PetFilter filter = PetFilter.builder()
                .name(name)
                .color(color)
                .age(age)
                .type(type)
                .build();

        Optional<Pet> optPet = petService.findPetByFilters(filter);

        if (optPet.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PetResponse response = petMapper.mapTo(optPet.get());
        return ResponseEntity.ok(response);
    }
}
