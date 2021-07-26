package fr.poleemploi.estime.services.ressources;

public class SalairesAvantPeriodeSimulation {

    private SalaireAvantPeriodeSimulation salaireMoisDemandeSimulation;
    private SalaireAvantPeriodeSimulation salaireMoisMoins1MoisDemandeSimulation;
    private SalaireAvantPeriodeSimulation salaireMoisMoins2MoisDemandeSimulation;

    public SalaireAvantPeriodeSimulation getSalaireMoisDemandeSimulation() {
        return salaireMoisDemandeSimulation;
    }

    public void setSalaireMoisDemandeSimulation(SalaireAvantPeriodeSimulation salaireMoisDemandeSimulation) {
        this.salaireMoisDemandeSimulation = salaireMoisDemandeSimulation;
    }

    public SalaireAvantPeriodeSimulation getSalaireMoisMoins1MoisDemandeSimulation() {
        return salaireMoisMoins1MoisDemandeSimulation;
    }

    public void setSalaireMoisMoins1MoisDemandeSimulation(
            SalaireAvantPeriodeSimulation salaireMoisMoins1MoisDemandeSimulation) {
        this.salaireMoisMoins1MoisDemandeSimulation = salaireMoisMoins1MoisDemandeSimulation;
    }

    public SalaireAvantPeriodeSimulation getSalaireMoisMoins2MoisDemandeSimulation() {
        return salaireMoisMoins2MoisDemandeSimulation;
    }

    public void setSalaireMoisMoins2MoisDemandeSimulation(
            SalaireAvantPeriodeSimulation salaireMoisMoins2MoisDemandeSimulation) {
        this.salaireMoisMoins2MoisDemandeSimulation = salaireMoisMoins2MoisDemandeSimulation;
    }

    @Override
    public String toString() {
        return "SalairesAvantPeriodeSimulation [salaireMoisDemandeSimulation=" + salaireMoisDemandeSimulation
                + ", salaireMoisMoins1MoisDemandeSimulation=" + salaireMoisMoins1MoisDemandeSimulation
                + ", salaireMoisMoins2MoisDemandeSimulation=" + salaireMoisMoins2MoisDemandeSimulation + "]";
    }
}
