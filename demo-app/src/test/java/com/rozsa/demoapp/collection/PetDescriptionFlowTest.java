package com.rozsa.demoapp.collection;

import com.rozsa.demoapp.configuration.collector.PetDescriptionFlowKeys;
import com.rozsa.demoapp.domain.Owner;
import com.rozsa.demoapp.domain.Pet;
import com.rozsa.demoapp.mocks.DatabaseData;
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
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.rozsa.demoapp.configuration.collector.PetDescriptionFlowKeys.*;
import static com.rozsa.demoapp.mocks.DatabaseData.PETS_DB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static testutils.AsyncTestUtils.verifyAsync;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class PetDescriptionFlowTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * Initializes a custom flow
     * Collects parameters values using custom flow
     * Submits the flow using customized configuration
     */
    @Test
    void givenFlowHasCustomConfig_whenFlowCalled_thenCustomConfigShouldBeUsed() throws InterruptedException {
        stubFor(post(urlMatching("/collect"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                )
        );

        final String targetName = "Jouse";
        final String targetColor = "Yellow";

        ResponseEntity<String[]> response = this.testRestTemplate.getForEntity(
                String.format("/pet/description?name=%s&color=%s", targetName, targetColor), String[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verifyAsync(1, postRequestedFor(urlMatching("/collect/pet/description"))
                        .withHeader(HttpEventsSubmitter.flowNameHeader, equalTo(PET_DESC_FLOW))
                        .andMatching(EventMatcher.of(Map.of(
                                PET_NAME, targetName,
                                PET_COLOR, targetColor
                        ), "custom_flow_id_key")),
                1000);
    }

}
