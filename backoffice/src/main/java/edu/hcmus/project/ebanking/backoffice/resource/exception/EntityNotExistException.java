package edu.hcmus.project.ebanking.backoffice.resource.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityNotExistException extends RuntimeException {
    public EntityNotExistException(String message) {
        super(message);
    }
}
