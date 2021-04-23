package fr.poleemploi.estime.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice  
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleCritereRechercheIncorrectException(BadRequestException ex) {
		ExceptionResponse errorResponse = new ExceptionResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setException(ex.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNonExistanteException(ResourceNotFoundException ex) {
		ExceptionResponse errorResponse = new ExceptionResponse();
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setException(ex.toString());	
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
	
	@ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleInterdictionException(UnauthorizedException ex) {
		ExceptionResponse errorResponse = new ExceptionResponse();
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setException(ex.toString());	
        errorResponse.setCode(ex.getCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
	
	@ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ExceptionResponse> handleErreurServeurException(InternalServerException ex) {
		ExceptionResponse errorResponse = new ExceptionResponse();
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setException(ex.toString());	
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
