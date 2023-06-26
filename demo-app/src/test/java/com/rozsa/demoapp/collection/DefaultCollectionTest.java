package com.rozsa.demoapp.collection;

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

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.rozsa.demoapp.domain.PetType.*;
import static org.junit.jupiter.api.Assertions.*;

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

        //TODO: replace by verifyAsync
        //TODO: assert submitted event data.
        Thread.sleep(500);

        verify(1, postRequestedFor(urlMatching("/collect")));

//        verifyAsync(1, postRequestedFor(urlMatching("/collect"))
//                        .withRequestBody(matchingJsonPath("$[0].foo", containing(value)))
//                        .withRequestBody(matchingJsonPath("$[0].event_id", matching("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))),
//                1000
//        );
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
