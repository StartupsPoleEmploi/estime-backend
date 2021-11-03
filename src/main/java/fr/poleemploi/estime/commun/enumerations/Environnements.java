package fr.poleemploi.estime.commun.enumerations;

public enum Environnements {

    LOCALHOST("localhost"),
    LOCALHOST_IP("127.0.0.1"),
    RECETTE("recette");
    
    private String libelle;
    
    Environnements(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
