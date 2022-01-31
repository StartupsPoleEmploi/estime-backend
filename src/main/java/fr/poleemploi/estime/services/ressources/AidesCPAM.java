package fr.poleemploi.estime.services.ressources;

public class AidesCPAM {
    private Float pensionInvalidite;
    private Float allocationSupplementaireInvalidite;

    public Float getPensionInvalidite() {
	return pensionInvalidite;
    }

    public void setPensionInvalidite(Float pensionInvalidite) {
	this.pensionInvalidite = pensionInvalidite;
    }

    public Float getAllocationSupplementaireInvalidite() {
	return allocationSupplementaireInvalidite;
    }

    public void setAllocationSupplementaireInvalidite(Float allocationSupplementaireInvalidite) {
	this.allocationSupplementaireInvalidite = allocationSupplementaireInvalidite;
    }

    @Override
    public String toString() {
	return "AllocationsCPAM [pensionInvalidite=" + pensionInvalidite + ", allocationSupplementaireInvalidite="
		+ allocationSupplementaireInvalidite + "]";
    }
}
