package unit.io.services.weather.validator;

import io.services.weather.domain.WeatherRequest;
import io.services.weather.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;


@ExtendWith(MockitoExtension.class)
class WeatherRequestValidator {

    @InjectMocks
    private io.services.weather.validator.WeatherRequestValidator requestValidator;


    @Test
    void shouldValidateRequestSuccessfully() {
        assertThatNoException().isThrownBy(() -> requestValidator.validate(WeatherRequest.builder()
                .city("MEL")
                .country("AU")
                .build()));

    }

    @Test
    void shouldThrowValidationExceptionWhenCityIsEmpty() {
        assertThatException()
                .isThrownBy(() -> requestValidator.validate(WeatherRequest.builder().city("").country("AU").build())).isInstanceOf(ValidationException.class)
                .withMessageContaining("City cannot be empty");

    }


    @Test
    void shouldThrowValidationExceptionWhenCountryIsEmpty() {
        assertThatException()
                .isThrownBy(() -> requestValidator.validate(WeatherRequest.builder().city("MEL").country("").build())).isInstanceOf(ValidationException.class)
                .withMessageContaining("Country cannot be empty");

    }
}