package fr.poleemploi.estime.commun.enumerations;

public enum MessagesAlertesAideSociale {

    ASS_DEMANDE_RENOUVELLEMENT("Le droit à l’ASS est calculé pour 6 mois et il peut être renouvelé en fonction de vos ressources. Vous allez recevoir un dossier de demande de renouvellement à compléter.");
    
    private String message;
    
    MessagesAlertesAideSociale(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
