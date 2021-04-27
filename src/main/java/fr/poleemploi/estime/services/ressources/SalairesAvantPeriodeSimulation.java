package fr.poleemploi.estime.services.ressources;

public class SalairesAvantPeriodeSimulation {

    private float salaireMoisDemandeSimulation;
    private float salaireMoisMoins1MoisDemandeSimulation;
    private float salaireMoisMoins2MoisDemandeSimulation;
    
    public float getSalaireMoisDemandeSimulation() {
        return salaireMoisDemandeSimulation;
    }
    public void setSalaireMoisDemandeSimulation(float salaireMoisDemandeSimulation) {
        this.salaireMoisDemandeSimulation = salaireMoisDemandeSimulation;
    }
    public float getSalaireMoisMoins1MoisDemandeSimulation() {
        return salaireMoisMoins1MoisDemandeSimulation;
    }
    public void setSalaireMoisMoins1MoisDemandeSimulation(float salaireMoisMoins1MoisDemandeSimulation) {
        this.salaireMoisMoins1MoisDemandeSimulation = salaireMoisMoins1MoisDemandeSimulation;
    }
    public float getSalaireMoisMoins2MoisDemandeSimulation() {
        return salaireMoisMoins2MoisDemandeSimulation;
    }
    public void setSalaireMoisMoins2MoisDemandeSimulation(float salaireMoisMoins2MoisDemandeSimulation) {
        this.salaireMoisMoins2MoisDemandeSimulation = salaireMoisMoins2MoisDemandeSimulation;
    }
    @Override
    public String toString() {
        return "SalairesAvantPeriodeSimulation [salaireMoisDemandeSimulation=" + salaireMoisDemandeSimulation
                + ", salaireMoisMoins1MoisDemandeSimulation=" + salaireMoisMoins1MoisDemandeSimulation
                + ", salaireMoisMoins2MoisDemandeSimulation=" + salaireMoisMoins2MoisDemandeSimulation + "]";
    }
}
