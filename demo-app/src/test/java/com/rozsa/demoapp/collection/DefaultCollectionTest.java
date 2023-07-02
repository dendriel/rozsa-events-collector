package com.rozsa.demoapp.collection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.rozsa.demoapp.configuration.collector.DefaultFlowKeys;
import com.rozsa.demoapp.configuration.collector.PetFilterFlowKeys;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.resources.dto.PetRequest;
import com.rozsa.demoapp.resources.dto.PetResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import testutils.EventMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.rozsa.demoapp.domain.PetType.*;
import static org.junit.jupiter.api.Assertions.*;
import static testutils.AsyncTestUtils.verifyAsync;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class DefaultCollectionTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private final List<Pet> petsDb = List.of(
            Pet.builder().id(1L).name("Trinity").age(6).color("Yellow").type(DOG).description("Trinity is a dog of color Yellow and is 6 years old")
                .build(),
            Pet.builder().id(2L).name("Sebastian").age(29).color("Green").type(TURTLE).description("Sebastian is a turtle of color Green and is 29 years old")
                .build(),
            Pet.builder().id(3L).name("Merlin").age(1).color("Gray").type(CAT).description("Merlin is a cat of color Grey and is 1 year old")
                .build()
    );

    /**
     * TODO:
     *
     * Add more test scenarios:
     * - recursive field collection
     * - multiple flow collection
     * - configurable flows
     * - id and submitter bean overriding
     */


    /**
     * Initialize the default flow
     * Collects a parameter value using default flow
     * Submits
     */
    @Test
    void givenAnnotatedCollectionFlow_whenFlowCalled_thenExpectedDataShouldBeCollected() throws InterruptedException {
        final Pet targetPet = petsDb.get(0);
        final String targetName = targetPet.getName();

        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ResponseEntity<PetResponse> response = this.testRestTemplate
                .getForEntity(String.format("/pet?name=%s", targetName), PetResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertPetEqualsResponse(targetPet, response.getBody());

        verifyAsync(1, postRequestedFor(urlMatching("/collect"))
                        .andMatching(EventMatcher.of(Map.of(DefaultFlowKeys.PET_NAME, targetName))),
                1000);
    }

    // TODO: assert custom flow usage. use a custom endpoint for submitting this event data.
    /**
     * Initializes a custom flow
     * Collects parameters values using custom flow
     * Collects return value using a custom collector
     * Submits the custom flow
     */
    @Test
    void givenCollectParamScanFields_whenFlowCalled_thenExpectedDataShouldBeCollected() throws InterruptedException {
        final Pet targetPet = petsDb.get(1);

        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ResponseEntity<PetResponse> response = this.testRestTemplate
                .getForEntity(String.format("/pet/find?name=%s&age=%d&color=%s&type=%s",
                        targetPet.getName(), targetPet.getAge(), targetPet.getColor(), targetPet.getType()), PetResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertPetEqualsResponse(targetPet, response.getBody());

        verifyAsync(1, postRequestedFor(urlMatching("/collect"))
                        .andMatching(EventMatcher.of(Map.of(
                                PetFilterFlowKeys.FILTERS_COLOR, targetPet.getColor(),
                                PetFilterFlowKeys.FILTERS_TYPE, targetPet.getType().name(),
                                PetFilterFlowKeys.FILTERS_AGE, targetPet.getAge(),
                                PetFilterFlowKeys.RESPONSE_NAME, targetPet.getName()
                        ))),
                1000);
    }

    private void assertPetEqualsResponse(Pet pet, PetResponse petResponse) {
        assertEquals(pet.getId(), petResponse.getId());
        assertEquals(pet.getName(), petResponse.getName());
        assertEquals(pet.getAge(), petResponse.getAge());
        assertEquals(pet.getType(), petResponse.getType());
        assertEquals(pet.getColor(), petResponse.getColor());
        assertEquals(pet.getDescription(), petResponse.getDescription());
    }
}
