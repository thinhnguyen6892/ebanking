package edu.hcmus.project.ebanking.backoffice.security.authentication;

public class JwtAuthenticationException extends RuntimeException {

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

}
