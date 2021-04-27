package fr.poleemploi.estime.commun.enumerations;

public enum TypesContratTravail {
    CDI("CDI"),
    CDD("CDD");
    
    private String valeur;
    
    TypesContratTravail(String valeur) {
        this.valeur = valeur;
    }
    
    @Override
    public String toString() {
        return valeur;
    }
}
