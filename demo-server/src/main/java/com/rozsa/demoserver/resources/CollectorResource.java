package com.rozsa.demoserver.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/collect")
@RestController
public class CollectorResource {

    @PostMapping
    public void collect(@RequestBody List<Map<String, Object>> events) {
        log.info("{}", events);
    }
}
