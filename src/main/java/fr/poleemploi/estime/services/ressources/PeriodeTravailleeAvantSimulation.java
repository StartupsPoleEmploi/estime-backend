package fr.poleemploi.estime.services.ressources;

import java.util.Map;

public class PeriodeTravailleeAvantSimulation {

    private Map<String, MoisTravailleAvantSimulation> mois;

    public Map<String, MoisTravailleAvantSimulation> getMois() {
	return mois;
    }

    public void setMois(Map<String, MoisTravailleAvantSimulation> mois) {
	this.mois = mois;
    }

    @Override
    public String toString() {
	return "PeriodeTravailleeAvantSimulation [mois=" + mois + "]";
    }

}
