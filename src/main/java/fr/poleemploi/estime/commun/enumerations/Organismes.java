package fr.poleemploi.estime.commun.enumerations;

public enum Organismes {
    
    CAF("CAF", "Caisse d'allocation familiales"),
    PE("PE", "Pôle emploi"),
    CPAM("CPAM", "Caisse Primaire d'Assurance Maladie");
    
    private String nomCourt;
    private String nom;
    
    Organismes(String nomCourt, String nom) {
        this.nomCourt = nomCourt;
        this.nom = nom;
    }

    public String getNomCourt() {
        return nomCourt;
    }

    public String getNom() {
        return nom;
    }
}
