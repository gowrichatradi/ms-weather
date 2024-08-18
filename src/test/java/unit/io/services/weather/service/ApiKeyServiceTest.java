package unit.io.services.weather.service;

import io.services.weather.domain.ApiKeyDetails;
import io.services.weather.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {


    @InjectMocks
    private ApiKeyService apiKeyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(apiKeyService, "requestLimit", 5L);
        ReflectionTestUtils.setField(apiKeyService, "rateLimitTime", 100L);
        ReflectionTestUtils.setField(apiKeyService, "apiKeyDetailsMap", new ConcurrentHashMap<>());
    }

    @Test
    void shouldNotExceedRateLimit() {
        String apiKey = "test-api-key";
        Optional<Long> result = Optional.empty();
        for (int i = 0; i < 4; i++) {
            result = apiKeyService.isRateLimitExceeded(apiKey);
        }

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldExceedRateLimit() {
        String apiKey = "test-api-key";
        Optional<Long> result = Optional.empty();
        for (int i = 0; i < 6; i++) {
            result = apiKeyService.isRateLimitExceeded(apiKey);
        }
        assertTrue(result.isPresent());
    }


    @Test
    void shouldResetRateLimitAfterTimeWindow() {
        String apiKey = "test-api-key";
        ConcurrentHashMap<String, ApiKeyDetails> map = new ConcurrentHashMap<>();
        map.put(apiKey, new ApiKeyDetails(10, LocalDateTime.now().minusSeconds(105L)));

        ReflectionTestUtils.setField(apiKeyService, "apiKeyDetailsMap", map);

        assertThat(apiKeyService.isRateLimitExceeded(apiKey)).isEmpty();
    }

    @Test
    void shouldCalculateRemainingMinutesCorrectly() {
        String apiKey = "test-api-key";
        ConcurrentHashMap<String, ApiKeyDetails> map = new ConcurrentHashMap<>();
        map.put(apiKey, new ApiKeyDetails(10, LocalDateTime.now().minusSeconds(10)));

        ReflectionTestUtils.setField(apiKeyService, "apiKeyDetailsMap", map);

        assertThat(apiKeyService.isRateLimitExceeded(apiKey)).isNotEmpty()
                .satisfies(aLong -> assertThat(aLong.get()).isEqualTo(59L));
    }

    @Test
    void shouldReturnEmptyWhenTimeWindowExceeded() {
        String apiKey = "test-api-key";
        ConcurrentHashMap<String, ApiKeyDetails> map = new ConcurrentHashMap<>();
        map.put(apiKey, new ApiKeyDetails(10, LocalDateTime.now().minusSeconds(105L)));

        ReflectionTestUtils.setField(apiKeyService, "apiKeyDetailsMap", map);

        assertThat(apiKeyService.isRateLimitExceeded(apiKey)).isEmpty();
    }


}