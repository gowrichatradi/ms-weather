package io.services.weather.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Builder
public class WeatherData {
    @Id
    private Long id;
    private String city;
    private String country;
    private String data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
