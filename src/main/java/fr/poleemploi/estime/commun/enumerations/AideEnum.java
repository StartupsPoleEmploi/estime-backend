package fr.poleemploi.estime.commun.enumerations;

public enum AideEnum {

    AGEPI("AGEPI", "Aide à la Garde d'Enfants pour Parent Isolé"), AIDE_MOBILITE("AM", "Aide à la mobilité"), AIDES_LOGEMENT("AL", "Aides au Logement"),
    AIDE_PERSONNALISEE_LOGEMENT("APL", "Aide Personnalisée au Logement"), AIDE_RETOUR_EMPLOI("ARE", "Allocation d'Aide au Retour à l'Emploi"),
    COMPLEMENT_AIDE_RETOUR_EMPLOI("cARE", "Complément d'allocation d'Aide au Retour à l'Emploi"), CRC("CRC", "Cotisation Retraite Complémentaire"),
    CRDS("CRDS", "Contribution au Remboursement de la Dette Sociale"), CSG("CSG", "Contribution Sociale Généralisée"),
    ALLOCATION_ADULTES_HANDICAPES("AAH", "Allocation aux Adultes Handicapés"), ALLOCATION_LOGEMENT_FAMILIALE("ALF", "Allocation de Logement Familiale"),
    ALLOCATION_LOGEMENT_SOCIALE("ALS", "Allocation de Logement Sociale"), ALLOCATION_SOLIDARITE_SPECIFIQUE("ASS", "Allocation de Solidarité Spécifique"),
    ALLOCATIONS_FAMILIALES("AF", "Allocations Familiales"), ALLOCATION_SOUTIEN_FAMILIAL("ASF", "Allocation de Soutien Familial"), COMPLEMENT_FAMILIAL("CF", "Complément Familial"),
    IMMOBILIER("IMMO", "Revenus immobiliers"), MICRO_ENTREPRENEUR("MICRO", "Revenus micro-entreprise"), PRIME_ACTIVITE("PA", "Prime d'Activité"),
    PENSION_INVALIDITE("PI", "Pension d'Invalidité"), PENSIONS_ALIMENTAIRES("PAF", "Pensions Alimentaires du Foyer"),
    PRESTATION_ACCUEIL_JEUNE_ENFANT("PAJE", "Prestation d'Accueil du Jeune Enfant"), RSA("RSA", "Revenu de Solidarité Active"), SALAIRE("PAIE", "Salaire"),
    TRAVAILLEUR_INDEPENDANT("INDP", "Revenus travailleur indépendant");

    private String code;
    private String nom;

    AideEnum(String code, String nom) {
	this.code = code;
	this.nom = nom;
    }

    public String getCode() {
	return code;
    }

    public String getNom() {
	return nom;
    }
}
