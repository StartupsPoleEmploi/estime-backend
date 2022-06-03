package fr.poleemploi.estime.clientsexternes.openfisca;

public class OpenFiscaRetourSimulation {

    private float montantRSA;
    private float montantPrimeActivite;
    private float montantAideLogement;
    private float montantAgepi;
    private float montantAideMobilite;
    private String typeAideLogement;

    public float getMontantRSA() {
	return montantRSA;
    }

    public void setMontantRSA(float montantRSA) {
	this.montantRSA = montantRSA;
    }

    public float getMontantPrimeActivite() {
	return montantPrimeActivite;
    }

    public void setMontantPrimeActivite(float montantPrimeActivite) {
	this.montantPrimeActivite = montantPrimeActivite;
    }

    public float getMontantAideLogement() {
	return montantAideLogement;
    }

    public void setMontantAideLogement(float montantAideLogement) {
	this.montantAideLogement = montantAideLogement;
    }

    public String getTypeAideLogement() {
	return typeAideLogement;
    }

    public void setTypeAideLogement(String typeAideLogement) {
	this.typeAideLogement = typeAideLogement;
    }

    public float getMontantAgepi() {
	return montantAgepi;
    }

    public void setMontantAgepi(float montantAgepi) {
	this.montantAgepi = montantAgepi;
    }

    public float getMontantAideMobilite() {
	return montantAideMobilite;
    }

    public void setMontantAideMobilite(float montantAideMobilite) {
	this.montantAideMobilite = montantAideMobilite;
    }

    @Override
    public String toString() {
	return "OpenFiscaRetourSimulation [montantRSA=" + montantRSA + ", montantPrimeActivite=" + montantPrimeActivite + ", montantAideLogement=" + montantAideLogement
		+ ", montantAgepi=" + montantAgepi + ", typeAideLogement=" + typeAideLogement + "]";
    }
}
