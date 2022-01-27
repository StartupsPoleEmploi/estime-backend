package fr.poleemploi.estime.commun.enumerations;

public enum TypeContratTravailEnum {
    CDI("CDI"),
    CDD("CDD");

    private String valeur;

    TypeContratTravailEnum(String valeur) {
	this.valeur = valeur;
    }

    @Override
    public String toString() {
	return valeur;
    }
}
