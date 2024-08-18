package io.services.weather.repository;

import io.services.weather.entity.WeatherData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface WeatherDataRepository extends R2dbcRepository<WeatherData, Long> {
    Mono<WeatherData> findWeatherDataByCityAndCountry(String city, String country);
}
