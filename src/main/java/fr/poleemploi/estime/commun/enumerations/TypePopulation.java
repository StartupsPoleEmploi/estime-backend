package fr.poleemploi.estime.commun.enumerations;

public enum TypePopulation {

    AAH("AAH"),
    ARE("ARE"),
    ASS("ASS"),
    RSA("RSA"),
    AAH_ASS("AAH_ASS"),
    NON_BENEFICIAIRE("Non bénéficiaire d'allocations");
    
    private String libelle;
    
    TypePopulation(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
