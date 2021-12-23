package fr.poleemploi.estime.commun.enumerations;

public enum NationaliteEnum {

    FRANCAISE("Française"),
    EUROPEEN_OU_SUISSE("Ressortissant européen ou suisse"),
    AUTRE("Autre");
    
    private String valeur;
    
    NationaliteEnum(String valeur) {
        this.valeur = valeur;
    }

    public String getValeur() {
        return valeur;
    } 
}
