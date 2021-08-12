package fr.poleemploi.estime.services.ressources;

public class PeriodeTravailleeAvantSimulation {

    private MoisTravailleAvantSimulation moisMoins1;
    private MoisTravailleAvantSimulation moisMoins2;
    private MoisTravailleAvantSimulation moisMoins3;
    
    public MoisTravailleAvantSimulation getMoisMoins1() {
        return moisMoins1;
    }
    public void setMoisMoins1(MoisTravailleAvantSimulation moisMoins1) {
        this.moisMoins1 = moisMoins1;
    }
    public MoisTravailleAvantSimulation getMoisMoins2() {
        return moisMoins2;
    }
    public void setMoisMoins2(MoisTravailleAvantSimulation moisMoins2) {
        this.moisMoins2 = moisMoins2;
    }
    public MoisTravailleAvantSimulation getMoisMoins3() {
        return moisMoins3;
    }
    public void setMoisMoins3(MoisTravailleAvantSimulation moisMoins3) {
        this.moisMoins3 = moisMoins3;
    }
    
    @Override
    public String toString() {
        return "SalairesAvantSimulation [moisMoins1=" + moisMoins1 + ", moisMoins2=" + moisMoins2 + ", moisMoins3="
                + moisMoins3 + "]";
    }  
}
