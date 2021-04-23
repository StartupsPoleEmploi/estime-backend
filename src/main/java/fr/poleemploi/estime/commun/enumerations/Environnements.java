package fr.poleemploi.estime.commun.enumerations;

public enum Environnements {
    
    LOCALHOST("localhost"),
    RECETTE("recette");
    
    private String libelle;
    
    Environnements(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
