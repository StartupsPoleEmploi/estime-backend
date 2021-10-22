package fr.poleemploi.estime.commun.enumerations;

public enum StatutsOccupationLogement {
    LOCATAIRE_HLM("locataire_hlm"),
    LOCATAIRE_MEUBLE("locataire_meuble"),
    LOCATAIRE_NON_MEUBLE("locataire_vide"),
    LOGE_GRATUITEMENT("loge_gratuitement"),
    PROPRIETAIRE("proprietaire"),
    PROPRIETAIRE_AVEC_EMPRUNT("proprietaire");
    
    private String libelle;
    
    StatutsOccupationLogement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
