package fr.poleemploi.estime.commun.enumerations;

public enum Nationalites {

    FRANCAISE("Française"),
    EUROPEEN_OU_SUISSE("Ressortissant européen ou suisse"),
    AUTRE("Autre");
    
    private String valeur;
    
    Nationalites(String valeur) {
        this.valeur = valeur;
    }

    public String getValeur() {
        return valeur;
    } 
}
