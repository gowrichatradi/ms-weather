package io.services.weather.validator;

import io.services.weather.domain.WeatherRequest;
import io.services.weather.exception.ValidationException;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class WeatherRequestValidator {

    public void validate(WeatherRequest request) {
        Validation<Seq<String>, WeatherRequest> result = Validation.combine(
                        validateCity(request),
                        validateCountry(request))
                .ap((s1, s2) -> s1);
        if (result.isInvalid()) {
            throw new ValidationException(result.getError().mkString(","));
        }
    }

    private Validation<String, WeatherRequest> validateCity(WeatherRequest request) {
        return Objects.nonNull(request) && StringUtils.isNotBlank(request.getCity()) ? Validation.valid(request) : Validation.invalid("City cannot be empty");
    }


    private Validation<String, WeatherRequest> validateCountry(WeatherRequest request) {
        return Objects.nonNull(request) && StringUtils.isNotBlank(request.getCountry()) ? Validation.valid(request) : Validation.invalid("Country cannot be empty");
    }


}
