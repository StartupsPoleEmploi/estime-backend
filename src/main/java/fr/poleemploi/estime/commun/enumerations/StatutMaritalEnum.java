package fr.poleemploi.estime.commun.enumerations;

public enum StatutMaritalEnum {
    MARIE("marie"),
    CELIBATAIRE("celibataire");

    private String libelle;

    StatutMaritalEnum(String libelle) {
	this.libelle = libelle;
    }

    public String getLibelle() {
	return libelle;
    }
}
