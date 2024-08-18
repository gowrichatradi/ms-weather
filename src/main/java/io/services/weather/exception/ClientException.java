package io.services.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ClientException extends RuntimeException {

    private final String errorMessage;
    private final HttpStatusCode httpStatusCode;

    public ClientException(String errorMessage, HttpStatusCode httpStatusCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.httpStatusCode = httpStatusCode;
    }

    public ClientException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.httpStatusCode = HttpStatus.BAD_REQUEST;

    }

    @Override
    public String getMessage() {
        return errorMessage;
    }


    public HttpStatusCode getStatus() {
        return httpStatusCode;
    }
}
