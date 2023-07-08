package com.rozsa.demoapp.collection;

import com.rozsa.demoapp.configuration.collector.DefaultFlowKeys;
import com.rozsa.demoapp.configuration.collector.PetFilterFlowKeys;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.repository.PetRepository;
import com.rozsa.demoapp.resources.dto.PetResponse;
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
import static org.junit.jupiter.api.Assertions.*;
import static testutils.AsyncTestUtils.verifyAsync;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class PetResourceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PetRepository petRepository;

    /**
     * Initialize the default flow
     * Collects a parameter value using default flow
     * Submits
     */
    @Test
    void givenAnnotatedCollectionFlow_whenFlowCalled_thenExpectedDataShouldBeCollected() throws InterruptedException {
        final Pet targetPet = petRepository.findById(1L).orElseThrow();
        final String targetName = targetPet.getName();

        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ResponseEntity<PetResponse> response = this.testRestTemplate
                .getForEntity(String.format("/pet?name=%s", targetName), PetResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

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
        final Pet targetPet = petRepository.findById(2L).orElseThrow();

        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        );

        ResponseEntity<PetResponse> response = this.testRestTemplate
                .getForEntity(String.format("/pet/find?name=%s&age=%d&color=%s&type=%s",
                        targetPet.getName(), targetPet.getAge(), targetPet.getColor(), targetPet.getType()), PetResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verifyAsync(1, postRequestedFor(urlMatching("/collect"))
                        .andMatching(EventMatcher.of(Map.of(
                                PetFilterFlowKeys.FILTERS_COLOR, targetPet.getColor(),
                                PetFilterFlowKeys.FILTERS_TYPE, targetPet.getType().name(),
                                PetFilterFlowKeys.FILTERS_AGE, targetPet.getAge(),
                                PetFilterFlowKeys.RESPONSE_NAME, targetPet.getName()
                        ))),
                1000);
    }
}
