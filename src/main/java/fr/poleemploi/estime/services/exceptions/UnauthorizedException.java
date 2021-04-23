package fr.poleemploi.estime.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {
	
	private static final long serialVersionUID = 5986644805736634120L;
	
	private final String code;

	public UnauthorizedException(String message) {
		super(message);
		this.code = "NO_CODE";
	}
	
	public UnauthorizedException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
