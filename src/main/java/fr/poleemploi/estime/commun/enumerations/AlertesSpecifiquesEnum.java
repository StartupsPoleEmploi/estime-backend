package fr.poleemploi.estime.commun.enumerations;

public enum AlertesSpecifiquesEnum {

    ALERTE_1_ASS("à %s,vous devrez faire une demande de renouvellement de votre ASS. Pour la simulation nous prenons la situation d'accord de renouvellement de l'ASS");
    
    String message;
    
    AlertesSpecifiquesEnum(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }
}
