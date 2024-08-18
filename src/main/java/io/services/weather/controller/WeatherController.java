package io.services.weather.controller;

import io.services.weather.domain.WeatherRequest;
import io.services.weather.domain.WeatherResponse;
import io.services.weather.entity.WeatherData;
import io.services.weather.service.WeatherService;
import io.services.weather.validator.ApiKeyValidator;
import io.services.weather.validator.WeatherRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class WeatherController {
    public static final String API_KEY = "Api-Key";

    private final WeatherService weatherService;
    private final ApiKeyValidator apiKeyValidator;
    private final WeatherRequestValidator weatherRequestValidator;

    @PostMapping("/weather")
    public Mono<WeatherResponse> weather(@RequestHeader(API_KEY) String apiKey, @RequestBody WeatherRequest weatherRequest) {
        apiKeyValidator.validate(apiKey);
        weatherRequestValidator.validate(weatherRequest);

        log.info("checking weather for the city={}, and country={}", weatherRequest.getCity(), weatherRequest.getCountry());
        return weatherService.fetchWeather(weatherRequest);
    }

}
