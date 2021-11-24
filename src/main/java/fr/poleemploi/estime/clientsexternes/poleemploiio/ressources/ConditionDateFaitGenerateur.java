package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

public class ConditionDateFaitGenerateur {
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
	return "ConditionDateFaitGenerateur [remplie=" + remplie + ", justification=" + justification + "]";
    }
}
