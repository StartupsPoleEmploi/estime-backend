package fr.poleemploi.estime.commun.enumerations.exceptions;

public enum LoggerMessages {

    DETAIL_REQUETE_HTTP("Erreur Technique HttpClientErrorException : erreur %s avec requête %s"),
    ENREGISTREMENT_SUIVI_UTILISATEUR_IMPOSSIBLE_USER_INFO_KO("Erreur Technique : enregistrement suivi utilisateur non effectué car problème récupération du userInfo"),
    USER_INFO_KO("Erreur Technique : impossible de récupérer infos individu"),
    RETOUR_SERVICE_KO("Erreur Technique : erreur %s en retour du service %s."),
    SIMULATION_IMPOSSIBLE_CODE_AIDE_INEXISTANT ("Erreur Technique : impossible d'effectuer la simulation car le code dl'aide n'exitse pas."),
    SIMULATION_IMPOSSIBLE_MONTANT_ASS_SIMULE_KO ("Erreur Technique : impossible d'effectuer la simulation car les montants ASS simulés sur les premiers mois non présents."),
    SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE("Erreur technique : simulation impossible dûe à l'erreur : %s"),
    SIMULATION_IMPOSSIBLE_DEMANDE_REFUSEE("Erreur Technique : impossible d'effectuer la simulation car la demande a été refusée."),
    SUIVI_UTILISATEUR_ENREGISTREMENT_KO("Erreur Technique : enregistrement utilisateur impossible dûe à l'erreur : %s.");

    private String message;

    LoggerMessages(String message) {
	this.message = message;
    }

    public String getMessage() {
	return message;
    }
}
