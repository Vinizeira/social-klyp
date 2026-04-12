package com.github.devlucasjava.socialklyp.delivery.rest.advice;

public class InvalidOrExpiredTokenException extends RuntimeException {
    public InvalidOrExpiredTokenException() {
        super("Invalid or expired token");
    }
}
