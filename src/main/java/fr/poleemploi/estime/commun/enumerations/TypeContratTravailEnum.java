package fr.poleemploi.estime.commun.enumerations;

public enum TypeContratTravailEnum {
    CDI("CDI", "cdi"), CDD("CDD", "cdd"), INTERIM("interim", "ctt"), IAE("IAE", "cdd");

    private String valeur;
    private String valeurOpenFisca;

    TypeContratTravailEnum(String valeur, String valeurOpenFisca) {
	this.valeur = valeur;
	this.valeurOpenFisca = valeurOpenFisca;
    }

    public String getValeur() {
	return valeur;
    }

    public String getValeurOpenFisca() {
	return valeurOpenFisca;
    }

    @Override
    public String toString() {
	return "valeur : " + valeur + ", valeurOpenFisca : " + valeurOpenFisca;
    }
}
