package fr.poleemploi.estime.services.exceptions;

public class ExceptionResponse {

	private int status;
    private String exception;
    private String message;
    private String code;
 
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
