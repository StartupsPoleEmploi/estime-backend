package fr.poleemploi.estime.commun.enumerations.exceptions;

public enum InternalServerMessages {

    ACCES_APPLICATION_IMPOSSIBLE("Erreur technique : accès à l'application impossible."), SIMULATION_IMPOSSIBLE("Erreur Technique : impossible d'effectuer votre simulation."),
    IDENTIFICATION_IMPOSSIBLE("Erreur Technique : impossible de vous identifier."),
    MAILJET_AJOUT_CONTACT_IMPOSSIBLE("Erreur Technique : impossible d'ajouter un contact à la liste mailjet.");

    private String message;

    InternalServerMessages(String message) {
	this.message = message;
    }

    public String getMessage() {
	return message;
    }
}
