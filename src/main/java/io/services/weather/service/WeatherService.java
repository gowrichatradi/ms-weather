package io.services.weather.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.services.weather.domain.*;
import io.services.weather.entity.WeatherData;
import io.services.weather.exception.ClientException;
import io.services.weather.exception.ServiceException;
import io.services.weather.repository.WeatherDataRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;


@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    private final WebClient webClient;
    private final WeatherDataRepository repository;
    private final ObjectMapper mapper;


    @Value("${service.weather.hostname}")
    private String hostname;

    @Value("${service.weather.path}")
    private String path;

    @Value("${service.weather.api-key}")
    private String openWeatherApiKey;

    private static final String HTTPS = "https";
    private static final String Q_QUERY_PARAM = "q";
    private static final String APP_ID = "appid";
    private static final Function<WeatherRequest, WeatherResponse> DEFAULT_RESPONSE = request -> WeatherResponse.builder()
            .city(request.getCity())
            .country(request.getCountry())
            .weatherReport("Weather for the given details is not available. Please try again later")
            .build();

    @CircuitBreaker(name = "GetWeatherApiCircuitBreaker")
    @Retry(name = "GetWeatherApiRetry")
    public Mono<WeatherResponse> fetchWeather(WeatherRequest request) {
        return repository.findWeatherDataByCityAndCountry(request.getCity(), request.getCountry())
                .flatMap(data -> Mono.just(handleResponse(request, data)))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No data found in the database for the city={}, and country={}", request.getCity(), request.getCountry());
                    return fetchWeatherFromApi(request);
                }));
    }


    private Mono<WeatherResponse> fetchWeatherFromApi(WeatherRequest weatherRequest) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(hostname)
                        .path(path)
                        .queryParam(Q_QUERY_PARAM, StringUtils.joinWith(",", weatherRequest.getCity(), weatherRequest.getCountry()))
                        .queryParam(APP_ID, openWeatherApiKey)
                        .build())
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(s -> handleResponse(clientResponse.statusCode(), weatherRequest, s)))
                .doOnSuccess(res -> log.info("Successfully processed the weather data for the city={}, country={}", weatherRequest.getCity(), weatherRequest.getCountry()))
                .doOnError(ex -> log.error("Unable to process the request", ex));
    }

    private Mono<WeatherResponse> handleResponse(HttpStatusCode httpStatusCode, WeatherRequest weatherRequest, String response) {

        if (httpStatusCode.is4xxClientError()) {
            return Mono.error(new ClientException(response, httpStatusCode));
        }


        if (httpStatusCode.is5xxServerError()) {
            return Mono.error(new ServiceException(response));
        }

        return repository.save(WeatherData.builder()
                        .city(weatherRequest.getCity())
                        .country(weatherRequest.getCountry())
                        .data(response)
                        .build())
                .flatMap(data -> Mono.just(handleResponse(weatherRequest, data)));


    }

    private WeatherResponse handleResponse(WeatherRequest request, WeatherData data) {
        return Try.of(() -> mapper.readValue(data.getData(), GetWeatherResponse.class))
                .filter(Objects::nonNull)
                .filter(getWeatherResponse -> StringUtils.equalsIgnoreCase(getWeatherResponse.getName(), data.getCity()))
                .map(getWeatherResponse -> WeatherResponse.builder()
                        .city(getWeatherResponse.getName())
                        .country(getWeatherResponse.getSys().getCountry())
                        .weatherReport(getWeatherResponse.getWeather().stream().map(Weather::getDescription).findFirst().orElse("Weather for the given details is not available. Please try again later"))
                        .build())
                .getOrElseGet(throwable -> {
                    log.error("Failed with the reason:", throwable);
                    return DEFAULT_RESPONSE.apply(request);
                });
    }


}
