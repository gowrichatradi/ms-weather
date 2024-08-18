package unit.io.services.weather.validator;

import io.services.weather.exception.ValidationException;
import io.services.weather.service.ApiKeyService;
import io.services.weather.validator.ApiKeyValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ApiKeyValidatorTest {


    @Mock
    private ApiKeyService apiKeyService;

    @InjectMocks
    private ApiKeyValidator apiKeyValidator;


    @ParameterizedTest
    @ValueSource(strings = {"A", "DDLKAJDFLASJLADFS", "EAFJKFASELJ876868", "AFdaesfsafdsfa788787"})
    void shouldValidateApiKeySuccessfully(String apiKey) {
        when(apiKeyService.isRateLimitExceeded(any())).thenReturn(Optional.of(0L));
        assertThatNoException().isThrownBy(() -> apiKeyValidator.validate(apiKey));
    }

    @ParameterizedTest
    @CsvSource({
            "'', 'ApiKey cannot be null or empty'",
            "' ', 'ApiKey cannot be null or empty'",
    })
    void shouldThrowValidationExceptionWhenKeyIsEmpty(String apiKey, String exceptionMessage) {
        assertThatException()
                .isThrownBy(() -> apiKeyValidator.validate(apiKey)).isInstanceOf(ValidationException.class)
                .withMessageContaining(exceptionMessage);
    }


    @ParameterizedTest
    @CsvSource({
            "'Invalid!@#', 'ApiKey format is invalid'",
            "'__---__', 'ApiKey format is invalid'",
    })
    void shouldThrowValidationExceptionWhenKeyFormatIsNotCorrect(String apiKey, String exceptionMessage) {
        assertThatException()
                .isThrownBy(() -> apiKeyValidator.validate(apiKey)).isInstanceOf(ValidationException.class)
                .withMessageContaining(exceptionMessage);
    }

    @Test
    void shouldThrowValidationExceptionWhenRateLimitingExceeded() {
        when(apiKeyService.isRateLimitExceeded(any())).thenReturn(Optional.of(1L));
        assertThatException()
                .isThrownBy(() -> apiKeyValidator.validate("DFSJKLDAFS")).isInstanceOf(ValidationException.class)
                .withMessageContaining("No.of requests exceeded on this key");
    }
}