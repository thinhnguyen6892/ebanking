package edu.hcmus.project.ebanking.backoffice.resource.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ConnectException extends RuntimeException {

    public ConnectException(Exception e) {
        super(e);
    }

    public ConnectException(String message) {
        super(message);
    }
}
