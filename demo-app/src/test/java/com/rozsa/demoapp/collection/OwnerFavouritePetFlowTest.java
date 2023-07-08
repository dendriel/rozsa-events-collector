package com.rozsa.demoapp.collection;

import com.rozsa.demoapp.configuration.collector.FindFavouritePetFlowKeys;
import com.rozsa.demoapp.domain.Owner;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.repository.OwnerRepository;
import com.rozsa.demoapp.repository.PetRepository;
import com.rozsa.demoapp.resources.dto.OwnerResponse;
import com.rozsa.events.collector.HttpEventsSubmitter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import testutils.EventMatcher;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.rozsa.demoapp.configuration.collector.OwnerFavouritePetFlowKeys.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static testutils.AsyncTestUtils.verifyAsync;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class OwnerFavouritePetFlowTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

    /**
     * Initializes a custom flow
     * Collects parameters values using custom flow
     * Collects return value inside an optional
     * Do recursive field collection (over opt value)
     * Multi-flow collection
     * Submits the custom flow
     */
    @Test
    void givenCollectReturnRecursiveScanFields_whenFlowCalled_thenExpectedDataShouldBeCollected() throws InterruptedException {
        final Owner targetOwner = ownerRepository.findById(1L).orElseThrow();
        final Pet targetPet = petRepository.findById(4L).orElseThrow();

        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ResponseEntity<OwnerResponse> response = this.testRestTemplate.getForEntity("/owner/1", OwnerResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verifyAsync(1, postRequestedFor(urlMatching("/collect"))
                        .withHeader(HttpEventsSubmitter.flowNameHeader, equalTo(OWNER_FAV_PET_FLOW))
                        .andMatching(EventMatcher.of(Map.of(
                                OWNER_ID, targetOwner.getId().intValue(),
                                OWNER_NAME, targetOwner.getName(),
                                OWNER_GENDER, targetOwner.getGender().name(),
                                OWNER_AGE, targetOwner.getAge(),
                                OWNER_FAV_PET_NAME, targetPet.getName(),
                                OWNER_FAV_PET_ID, targetPet.getId().intValue()
                        ))),
                1000);

        verifyAsync(1, postRequestedFor(urlMatching("/collect"))
                        .withHeader(HttpEventsSubmitter.flowNameHeader, equalTo(FindFavouritePetFlowKeys.FIND_FAV_PET_FLOW))
                        .andMatching(EventMatcher.of(Map.of(
                                FindFavouritePetFlowKeys.OWNER_ID, targetOwner.getId().intValue(),
                                FindFavouritePetFlowKeys.PET_DESCRIPTION, targetPet.getDescription()
                        ))),
                1000);
    }
}
