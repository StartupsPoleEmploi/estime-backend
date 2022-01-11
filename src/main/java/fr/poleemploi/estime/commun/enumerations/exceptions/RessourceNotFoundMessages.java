package fr.poleemploi.estime.commun.enumerations.exceptions;

public enum RessourceNotFoundMessages {
	
    AIDE_NOT_FOUND("Il n'existe pas d'aide correspondant au code aide fourni.");

    private String message;
    
    RessourceNotFoundMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
