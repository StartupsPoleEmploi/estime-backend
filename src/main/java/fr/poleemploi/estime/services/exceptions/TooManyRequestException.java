package fr.poleemploi.estime.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public TooManyRequestException(String message) {
        super(message);
    }
}
