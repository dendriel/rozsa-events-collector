package com.rozsa.demoapp.performance;

import com.rozsa.demoapp.resources.dto.PetResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PerformanceTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Disabled("This is a performance test to be executed manually (it is slow). Remove this annotation to run the test.")
    @Test
    void perfTestsGetPetByName() {

        // warmup
        for (int i = 0; i < 100; i++) {
            ResponseEntity<PetResponse> response = this.testRestTemplate
                    .getForEntity("/pet?name=Trinity", PetResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        long count = 10000;
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < count; i++) {
            ResponseEntity<PetResponse> response = this.testRestTemplate
                    .getForEntity("/pet?name=Trinity", PetResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
        sw.stop();
        System.out.println("Tests: " + count +
                " - Total: " + sw.getTotalTimeMillis() +
                " ms - avg: " + (sw.getTotalTimeMillis()/(double)count)
                + " ms (" + (sw.getTotalTimeMillis()/(double)count)*1000 + " us)");

        /**
         * Without collect annotations:
         * Tests: 10000 - Total: 3711 ms - avg: 0.3711 ms (371 us)
         * Tests: 10000 - Total: 3773 ms - avg: 0.3773 ms (377.3 us)
         * Tests: 10000 - Total: 3617 ms - avg: 0.3617 ms (361.7 us)
         *
         *
         * With 1 collect annotation:
         * Tests: 10000 - Total: 4503 ms - avg: 0.4503 ms (450 us)
         * Tests: 10000 - Total: 4379 ms - avg: 0.4379 ms (437 us)
         *
         * With 2 collect annotation:
         * Tests: 10000 - Total: 4353 ms - avg: 0.4353 ms (435 us)
         * Tests: 10000 - Total: 4350 ms - avg: 0.435 ms (435 us)
         * Tests: 10000 - Total: 4198 ms - avg: 0.4198 ms (419 us)
         * Tests: 10000 - Total: 4521 ms - avg: 0.4521 ms (452.1 us)
         * Tests: 10000 - Total: 4588 ms - avg: 0.4588 ms (458.8 us)
         * Tests: 10000 - Total: 4454 ms - avg: 0.4454 ms (445.4 us)
         */
    }
}
