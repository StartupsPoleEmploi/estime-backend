package fr.poleemploi.estime.commun.enumerations;

public enum MessageInformatifEnum {

    AGEPI_IDF("Si vous êtes domicilé⸱e en Ile de France, veuillez vous rapprocher de votre conseiller référent pour en faire la demande. (Les montants peuvent être légèrement différents)."),
    ASS_DEMANDE_RENOUVELLEMENT("L'ASS peut être cumulée avec vos revenus d'activité pendant 3 mois maximum à condition d'avoir renouvelé vos droits. Pensez à faire votre actualisation mensuelle auprès de Pôle emploi."),
    PPA_AUTOMATIQUE_SI_BENEFICIAIRE_RSA("Si vous êtes bénéficiaire du RSA, vos droits à la prime d’activité seront automatiquement calculés lors de votre prochaine déclaration trimestrielle."),
    CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES("Les montants des prestations familiales peuvent être susceptibles d'évoluer en cas d'événement familial ou si le recalcul annuel survient durant cette période."),
    AGEPI_AM_DELAI_DEMANDE("Cette aide est versée à condition d'en faire la demande dans les 30 jours suivant la date de début de votre contrat."),
    FIN_DE_DROIT_ARE("À l'issu de ce mois, vous ne percevrez plus l'ARE car votre reliquat sera épuisé sauf si un rechargement ou une ouverture de droit ASS a lieu à ce moment-là. Renseignez-vous sur vos potentiels nouveaux droits auprès de votre agence Pôle emploi"),
    ACTUALISATION_ARE("Pensez à faire votre actualisation mensuelle auprès de Pôle emploi pour rester inscrit et recevoir l'allocation d'Aide au Retour à l'Emploi en complément de votre salaire."),
    MONTANT_ARE_AVANT_PAS("Résultat avant l'application du prélèvement à la source."),
    MOYENNE_REVENUS_ENTREPRENEURS("Ce montant est une moyenne mensuelle abattue, calculé sur la base de ce que vous avez déclaré pour l'année en cours."),
    RSA_AIDES_DEPARTEMENTALES("Certains départements proposent des aides complémentaires pour les bénéficiaires du RSA qui reprennent un emploi. Rapprochez-vous de votre conseiller référent pour plus d’informations."),
    DEMANDE_PPA("Votre demande de prime d'activité sera à effectuer au cours du mois d%s");

    private String message;

    MessageInformatifEnum(String message) {
	this.message = message;
    }

    public String getMessage() {
	return message;
    }
}
