package fr.poleemploi.estime.services.ressources;

public class AllocationsCPAM {
    private Float pensionInvalidite;

    public Float getPensionInvalidite() {
        return pensionInvalidite;
    }

    public void setPensionInvalidite(Float pensionInvalidite) {
        this.pensionInvalidite = pensionInvalidite;
    }

    @Override
    public String toString() {
        return "AllocationsCPAM [pensionInvalidite=" + pensionInvalidite + "]";
    }
}
