package io.services.weather.exception;

public class ServiceException extends RuntimeException {

    private final String errorMessage;

    public ServiceException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
