package fr.poleemploi.estime.services.ressources;

import java.util.Arrays;

public class PeriodeTravailleeAvantSimulation {

    private MoisTravailleAvantSimulation[] mois;

    public MoisTravailleAvantSimulation[] getMois() {
	return mois;
    }

    public void setMois(MoisTravailleAvantSimulation[] mois) {
	this.mois = mois;
    }

    @Override
    public String toString() {
	return "PeriodeTravailleeAvantSimulation [mois=" + Arrays.toString(mois) + "]";
    }
}
