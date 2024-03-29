package fr.poleemploi.estime.commun.enumerations.exceptions;

public enum InternalServerMessages {

    ACCES_APPLICATION_IMPOSSIBLE("Erreur technique : accès à l'application impossible."),
    SIMULATION_IMPOSSIBLE("Erreur Technique : impossible d'effectuer votre simulation."),
    IDENTIFICATION_IMPOSSIBLE("Erreur Technique : impossible de vous identifier."),
    SERIALIZATION_OPENFISCA_IMPOSSIBLE("Erreur Technique : la serialization de l'objet OpenFiscaRoot a échoué.");

    private String message;

    InternalServerMessages(String message) {
	this.message = message;
    }

    public String getMessage() {
	return message;
    }
}
