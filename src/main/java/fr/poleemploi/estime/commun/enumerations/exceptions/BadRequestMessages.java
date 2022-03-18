package fr.poleemploi.estime.commun.enumerations.exceptions;

public enum BadRequestMessages {

    CODE_AIDE_INCORRECT("Le code aide fourni n'existe pas. Codes aide possibles : %s"), CHAMP_OBLIGATOIRE("Le champ %s est obligatoire"),
    DEMANDEUR_EMPLOI_OBLIGATOIRE("Des informations sur le demandeur d'emploi sont obligatoires."),
    NATIONALITE_INCORRECTE("La nationalité du demandeur d'emploi doit avoir pour valeur : %s"),
    NOMBRE_HEURE_TRAVAILLEES_ZERO("Le nombre d'heures hebdomadaires travaillées ne peut pas être égale à 0"),
    INDIVIDU_OBLIGATOIRE("Des informations de l'individu sont obligatoires."), MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO("Le montant de %s doit être supérieur à 0."),
    RESSOURCES_FINANCIERES_SANS_MONTANT_SOURCE_FINANCIERE(
	    "Si ressources financieres %s de renseignées, un montant doit obligatoirement être renseigné (salaire, montant d'une aide, etc...) "),
    SALAIRE_MENSUEL_BRUT_ZERO("Le salaire mensuel brut ne peut pas être égale à 0"), SALAIRE_MENSUEL_NET_ZERO("Le salaire mensuel net ne peut pas être égale à 0"),
    TYPE_CONTRAT_INCORRECT("Le type de contrat du futur travail du demandeur d'emploi doit avoir pour valeur : %s"),
    VALEUR_INCORRECT_PROCHAINE_DECLARATION_TRIMESTRIELLE("La valeur de prochaine déclaration trimestrielle est incorrect (inférieure à 0 ou supérieur à 3) : %s"),
    EMAIL_INVALIDE("L'email (%s) renseigné est invalide."), EMAIL_OBLIGATOIRE("Un email valide est obligatoire."),
    MAILJET_CONTACT_DEJA_ENREGISTRE("L'adresse email %s est déjà enregistrée");

    private String message;

    BadRequestMessages(String message) {
	this.message = message;
    }

    public String getMessage() {
	return message;
    }
}
