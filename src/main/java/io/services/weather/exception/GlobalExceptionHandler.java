package io.services.weather.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.services.weather.domain.GetWeatherError;
import io.services.weather.domain.WeatherResponse;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper mapper;

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<WeatherResponse>> handleValidationException(ValidationException validationException) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WeatherResponse.builder()
                        .error(validationException.getMessage())
                        .build()));
    }


    @ExceptionHandler({CallNotPermittedException.class})
    public Mono<ResponseEntity<WeatherResponse>> handleCallNotPermittedException() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(WeatherResponse.builder()
                        .error("Service is not available")
                        .build()));
    }

    @ExceptionHandler({ClientException.class})
    public Mono<ResponseEntity<WeatherResponse>> handleClientException(ClientException clientException) {
        return Try.of(() -> mapper.readValue(clientException.getMessage(), GetWeatherError.class))
                .filter(Objects::nonNull)
                .map(error -> Mono.just(ResponseEntity
                        .status(clientException.getStatus())
                        .body(WeatherResponse.builder()
                                .error(error.getMessage())
                                .build())))
                .getOrElseGet(throwable -> handleGenericException(clientException));
    }


    @ExceptionHandler({ServiceException.class})
    public Mono<ResponseEntity<WeatherResponse>> handleServiceException(ServiceException serviceException) {
        return Try.of(() -> mapper.readValue(serviceException.getMessage(), GetWeatherError.class))
                .filter(Objects::nonNull)
                .map(error -> Mono.just(ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(WeatherResponse.builder()
                                .error(error.getMessage())
                                .build())))
                .getOrElseGet(throwable -> handleGenericException(serviceException));
    }


    @ExceptionHandler({Exception.class})
    public Mono<ResponseEntity<WeatherResponse>> handleGenericException(Exception e) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(WeatherResponse.builder()
                        .error("Service is not available")
                        .build()));
    }


}
