package io.services.weather.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiKeyDetails {
    private int requestCount;
    private LocalDateTime firstRequestTime;

    public ApiKeyDetails() {
        this.requestCount = 1;
        this.firstRequestTime = LocalDateTime.now();
    }


    public void incrementRequestCount() {
        this.requestCount++;
    }

    public void reset() {
        this.requestCount = 1;
        this.firstRequestTime = LocalDateTime.now();
    }

}
