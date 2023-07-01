package com.rozsa.demoapp.collection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.rozsa.demoapp.configuration.collector.DefaultFlowKeys;
import com.rozsa.demoapp.domain.Pet;
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

    private final ObjectMapper om = new ObjectMapper();

    private final List<Pet> petsDb = List.of(
            Pet.builder().id(1L).name("Trinity").age(6).color("Yellow").type(DOG).description("Trinity is a dog of color Yellow and is 6 years old")
                .build(),
            Pet.builder().id(2L).name("Sebastian").age(29).color("Green").type(TURTLE).description("Sebastian is a turtle of color Green and is 29 years old")
                .build(),
            Pet.builder().id(3L).name("Merlin").age(1).color("Gray").type(CAT).description("Merlin is a cat of color Grey and is 1 year old")
                .build()
    );

//    private <T> Map<String, Object> objectToMap(T obj) {
//        return om.convertValue(obj, Map.class);
//    }

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

    private void assertPetEqualsResponse(Pet pet, PetResponse petResponse) {
        assertEquals(pet.getId(), petResponse.getId());
        assertEquals(pet.getName(), petResponse.getName());
        assertEquals(pet.getAge(), petResponse.getAge());
        assertEquals(pet.getType(), petResponse.getType());
        assertEquals(pet.getColor(), petResponse.getColor());
        assertEquals(pet.getDescription(), petResponse.getDescription());
    }
}
