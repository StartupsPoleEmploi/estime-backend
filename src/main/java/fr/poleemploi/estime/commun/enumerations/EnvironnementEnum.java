package fr.poleemploi.estime.commun.enumerations;

public enum EnvironnementEnum {

    LOCALHOST("localhost"),
    TESTS_INTEGRATION("tests"),
    RECETTE("recette");
    
    private String libelle;
    
    EnvironnementEnum(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
