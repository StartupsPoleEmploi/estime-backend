package fr.poleemploi.estime.services.ressources;

public class SalairesAvantPeriodeSimulation {

    private Salaire salaireMoisDemandeSimulation;
    private Salaire salaireMoisMoins1MoisDemandeSimulation;
    private Salaire salaireMoisMoins2MoisDemandeSimulation;
    
    public Salaire getSalaireMoisDemandeSimulation() {
        return salaireMoisDemandeSimulation;
    }
    public void setSalaireMoisDemandeSimulation(Salaire salaireMoisDemandeSimulation) {
        this.salaireMoisDemandeSimulation = salaireMoisDemandeSimulation;
    }
    public Salaire getSalaireMoisMoins1MoisDemandeSimulation() {
        return salaireMoisMoins1MoisDemandeSimulation;
    }
    public void setSalaireMoisMoins1MoisDemandeSimulation(Salaire salaireMoisMoins1MoisDemandeSimulation) {
        this.salaireMoisMoins1MoisDemandeSimulation = salaireMoisMoins1MoisDemandeSimulation;
    }
    public Salaire getSalaireMoisMoins2MoisDemandeSimulation() {
        return salaireMoisMoins2MoisDemandeSimulation;
    }
    public void setSalaireMoisMoins2MoisDemandeSimulation(Salaire salaireMoisMoins2MoisDemandeSimulation) {
        this.salaireMoisMoins2MoisDemandeSimulation = salaireMoisMoins2MoisDemandeSimulation;
    }
    @Override
    public String toString() {
        return "SalairesAvantPeriodeSimulation [salaireMoisDemandeSimulation=" + salaireMoisDemandeSimulation
                + ", salaireMoisMoins1MoisDemandeSimulation=" + salaireMoisMoins1MoisDemandeSimulation
                + ", salaireMoisMoins2MoisDemandeSimulation=" + salaireMoisMoins2MoisDemandeSimulation + "]";
    }
}
