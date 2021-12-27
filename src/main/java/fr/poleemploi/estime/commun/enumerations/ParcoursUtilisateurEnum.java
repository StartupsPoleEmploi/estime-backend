package fr.poleemploi.estime.commun.enumerations;

public enum ParcoursUtilisateurEnum {

    CONNEXION_REFUSEE("Connexion refusée"),
    CONNEXION_REUSSIE("Connexion réussie"),
    SIMULATION_COMMENCEE("Simulation commencée"),
    SIMULATION_EFFECTUEE("Simulation effectuée");
    
    private String parcours;
    
    ParcoursUtilisateurEnum(String parcours) {
        this.parcours = parcours;
    }

    public String getParcours() {
        return parcours;
    }
}
