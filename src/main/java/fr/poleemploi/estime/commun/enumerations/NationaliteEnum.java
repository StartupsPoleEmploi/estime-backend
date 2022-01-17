package fr.poleemploi.estime.commun.enumerations;

public enum NationaliteEnum {

    FRANCAISE("Française"), RESSORTISSANT_UNION_EUROPEENNE("Ressortissant de l'Union Européenne"),
    RESSORTISSANT_ESPACE_ECONOMIQUE_EUROPEEN("Ressortissant de l'Espace Économique Européen"), SUISSE("Suisse"), AUTRE("Autre");

    private String valeur;

    NationaliteEnum(String valeur) {
	this.valeur = valeur;
    }

    public String getValeur() {
	return valeur;
    }
}
