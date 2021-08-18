package fr.poleemploi.estime.commun.enumerations;

public enum MessagesInformatifs {
    
    AGEPI_IDF("Si vous êtes domicilé⸱e en Ile de France, veuillez vous rapprocher de votre conseiller référent pour en faire la demande. (Les montants peuvent être légèrement diférents)."),
    ASS_DEMANDE_RENOUVELLEMENT("Le droit à l’ASS est calculé pour 6 mois et il peut être renouvelé en fonction de vos ressources. Vous allez recevoir un dossier de demande de renouvellement à compléter."),
    PPA_AUTOMATIQUE_SI_BENEFICIAIRE_RSA("Si vous êtes bénéficiaire du RSA, vos droits à la prime d’activité seront automatiquement calculés lors de votre prochaine déclaration trimestrielle.");
    
    private String message;
    
    MessagesInformatifs(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
