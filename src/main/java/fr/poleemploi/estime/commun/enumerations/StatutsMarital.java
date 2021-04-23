package fr.poleemploi.estime.commun.enumerations;

public enum StatutsMarital {
    MARIE("marie"),
    CELIBATAIRE("celibataire");
    
    private String libelle;
    
    StatutsMarital(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
