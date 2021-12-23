package fr.poleemploi.estime.commun.enumerations;

public enum MessageInformatifEnum {
    
    AGEPI_IDF("Si vous êtes domicilé⸱e en Ile de France, veuillez vous rapprocher de votre conseiller référent pour en faire la demande. (Les montants peuvent être légèrement différents)."),
    ASS_DEMANDE_RENOUVELLEMENT("Le droit à l’ASS est calculé pour 6 mois et il peut être renouvelé en fonction de vos ressources. Vous allez recevoir un dossier de demande de renouvellement à compléter."),
    PPA_AUTOMATIQUE_SI_BENEFICIAIRE_RSA("Si vous êtes bénéficiaire du RSA, vos droits à la prime d’activité seront automatiquement calculés lors de votre prochaine déclaration trimestrielle."),
    CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES("Les montants des prestations familiales peuvent être susceptibles d'évoluer en cas d'événement familial ou si le recalcul annuel survient durant cette période.");
    
    private String message;
    
    MessageInformatifEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
