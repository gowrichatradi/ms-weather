package io.services.weather.validator;


import io.services.weather.exception.ValidationException;
import io.services.weather.service.ApiKeyService;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiKeyValidator {
    private final ApiKeyService apiKeyService;

    public void validate(String apiKey) {
        Validation<Seq<String>, String> result = Validation.combine(
                        validateNullOrEmpty(apiKey),
                        validateFormat(apiKey),
                        isRateLimitExceeded(apiKey))
                .ap((s1, s2, s3) -> s1);
        if (result.isInvalid()) {
            throw new ValidationException(result.getError().mkString(","));
        }
    }


    private Validation<String, String> validateNullOrEmpty(String apiKey) {
        return StringUtils.isNotBlank(apiKey) ? Validation.valid(apiKey) : Validation.invalid("ApiKey cannot be null or empty");
    }

    private Validation<String, String> validateFormat(String apiKey) {
        return apiKey.matches("^[A-Za-z0-9]+$")
                ? Validation.valid(apiKey)
                : Validation.invalid("ApiKey format is invalid");
    }


    private Validation<String, Object> isRateLimitExceeded(String apiKey) {
        return apiKeyService.isRateLimitExceeded(apiKey)
                .filter(aLong -> aLong > 0)
                .map(l -> Validation.invalid("No.of requests exceeded on this key. Please use another another or wait for " + l + " minutes"))
                .orElse(Validation.valid(apiKey));


    }
}
