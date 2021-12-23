package fr.poleemploi.estime.commun.enumerations;

public enum TypesContratTravailEnum {
    CDI("CDI"),
    CDD("CDD");
    
    private String valeur;
    
    TypesContratTravailEnum(String valeur) {
        this.valeur = valeur;
    }
    
    @Override
    public String toString() {
        return valeur;
    }
}
