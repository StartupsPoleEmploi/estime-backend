package fr.poleemploi.estime.services.ressources;

public class AidesCPAM {
    private Float pensionInvalidite;

    public Float getPensionInvalidite() {
	return pensionInvalidite;
    }

    public void setPensionInvalidite(Float pensionInvalidite) {
	this.pensionInvalidite = pensionInvalidite;
    }

    @Override
    public String toString() {
	return "AidesCPAM [pensionInvalidite=" + pensionInvalidite + "]";
    }
}
