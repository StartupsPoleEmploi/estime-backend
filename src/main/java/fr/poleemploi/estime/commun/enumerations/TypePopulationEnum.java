package fr.poleemploi.estime.commun.enumerations;

public enum TypePopulationEnum {

    AAH("AAH"),
    ARE("ARE"),
    AREF("AREF"),
    ASS("ASS"),
    RSA("RSA"),
    AAH_ASS("AAH_ASS"),
    AAH_ARE("AAH_ARE"),
    NON_BENEFICIAIRE("Non bénéficiaire d'allocations");

    private String libelle;

    TypePopulationEnum(String libelle) {
	this.libelle = libelle;
    }

    public String getLibelle() {
	return libelle;
    }
}
