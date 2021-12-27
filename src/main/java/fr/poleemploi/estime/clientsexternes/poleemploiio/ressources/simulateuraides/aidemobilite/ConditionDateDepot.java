package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.aidemobilite;

public class ConditionDateDepot {
    private boolean remplie;
    private String justification;

    public boolean isRemplie() {
	return remplie;
    }

    public void setRemplie(boolean remplie) {
	this.remplie = remplie;
    }

    public String getJustification() {
	return justification;
    }

    public void setJustification(String justification) {
	this.justification = justification;
    }

    @Override
    public String toString() {
	return "ConditionDateDepot [remplie=" + remplie + ", justification=" + justification + "]";
    }

}
