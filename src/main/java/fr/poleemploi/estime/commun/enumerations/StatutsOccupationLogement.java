package fr.poleemploi.estime.commun.enumerations;

public enum StatutsOccupationLogement {
    LOGE_GRATUITEMENT("loge_gratuitement"),
    LOCATAIRE_VIDE("locataire_vide");
    
    private String libelle;
    
    StatutsOccupationLogement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
