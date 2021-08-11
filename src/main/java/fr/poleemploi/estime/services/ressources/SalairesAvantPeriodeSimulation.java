package fr.poleemploi.estime.services.ressources;

public class SalairesAvantPeriodeSimulation {

    private MoisTravailleAvantPeriodeSimulation moisDemandeSimulation;
    private MoisTravailleAvantPeriodeSimulation moisMoins1MoisDemandeSimulation;
    private MoisTravailleAvantPeriodeSimulation moisMoins2MoisDemandeSimulation;
    
    public MoisTravailleAvantPeriodeSimulation getMoisDemandeSimulation() {
        return moisDemandeSimulation;
    }
    public void setMoisDemandeSimulation(MoisTravailleAvantPeriodeSimulation moisDemandeSimulation) {
        this.moisDemandeSimulation = moisDemandeSimulation;
    }
    public MoisTravailleAvantPeriodeSimulation getMoisMoins1MoisDemandeSimulation() {
        return moisMoins1MoisDemandeSimulation;
    }
    public void setMoisMoins1MoisDemandeSimulation(MoisTravailleAvantPeriodeSimulation moisMoins1MoisDemandeSimulation) {
        this.moisMoins1MoisDemandeSimulation = moisMoins1MoisDemandeSimulation;
    }
    public MoisTravailleAvantPeriodeSimulation getMoisMoins2MoisDemandeSimulation() {
        return moisMoins2MoisDemandeSimulation;
    }
    public void setMoisMoins2MoisDemandeSimulation(MoisTravailleAvantPeriodeSimulation moisMoins2MoisDemandeSimulation) {
        this.moisMoins2MoisDemandeSimulation = moisMoins2MoisDemandeSimulation;
    }
    
    @Override
    public String toString() {
        return "SalairesAvantPeriodeSimulation [moisDemandeSimulation=" + moisDemandeSimulation
                + ", moisMoins1MoisDemandeSimulation=" + moisMoins1MoisDemandeSimulation
                + ", moisMoins2MoisDemandeSimulation=" + moisMoins2MoisDemandeSimulation + "]";
    }
}
