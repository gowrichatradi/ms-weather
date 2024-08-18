package io.services.msweather.controller;

import io.services.msweather.domain.WeatherRequest;
import io.services.msweather.domain.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class WeatherController {


    @PostMapping("/weather")
    public Mono<WeatherResponse> weather(@RequestBody WeatherRequest weatherRequest) {
        log.info("checking weather for the city={}, and country={}", weatherRequest.getCity(), weatherRequest.getCountry());
        return Mono.just(WeatherResponse.builder()
                .city(weatherRequest.getCity())
                .country(weatherRequest.getCountry())
                .weatherReport("Clear skies \uD83D\uDE0D")
                .build());
    }


}
