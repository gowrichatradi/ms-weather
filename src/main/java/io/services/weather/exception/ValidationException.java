package io.services.weather.exception;

public class ValidationException extends RuntimeException {

    private final String validationErrors;

    public ValidationException(String validationErrors) {
        super(validationErrors);
        this.validationErrors = validationErrors;
    }

    @Override
    public String getMessage() {
        return validationErrors;
    }
}
