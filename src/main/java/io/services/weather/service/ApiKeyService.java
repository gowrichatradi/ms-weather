package io.services.weather.service;

import io.services.weather.domain.ApiKeyDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiKeyService {

    @Value("${service.weather.api-rate-limit.requests}")
    private long requestLimit;

    @Value("${service.weather.api-rate-limit.time}")
    private long rateLimitTime;

    private final Map<String, ApiKeyDetails> apiKeyDetailsMap = new ConcurrentHashMap<>();


    public Optional<Long> isRateLimitExceeded(String apiKey) {
        var usage = apiKeyDetailsMap.computeIfAbsent(apiKey, key -> new ApiKeyDetails());

        boolean isWithinTimeWindow = isWithinTimeWindow(usage.getFirstRequestTime());
        boolean isRateLimitExceeded = isWithinTimeWindow && usage.getRequestCount() >= requestLimit;


        if (isRateLimitExceeded) {
            return calculateRemainingMinutes(usage.getFirstRequestTime());
        } else {
            updateUsage(isWithinTimeWindow, usage);
            return Optional.empty();
        }

    }

    private Optional<Long> calculateRemainingMinutes(LocalDateTime firstRequestTime) {
        long minutesRemaining = Duration.between(LocalDateTime.now(), firstRequestTime.plusHours(1)).toMinutes();
        return minutesRemaining > 0 ? Optional.of(minutesRemaining) : Optional.empty();
    }

    private void updateUsage(boolean isWithinTimeWindow, ApiKeyDetails keyDetails) {
        if (isWithinTimeWindow) {
            keyDetails.incrementRequestCount();
        } else {
            keyDetails.reset();
        }
    }


    private boolean isWithinTimeWindow(LocalDateTime firstRequestTime) {
        return Duration.between(firstRequestTime, LocalDateTime.now()).compareTo(Duration.ofSeconds(rateLimitTime)) < 0;
    }

}
