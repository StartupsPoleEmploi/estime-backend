package fr.poleemploi.estime.clientsexternes.openfisca;

public class OpenFiscaRetourSimulation {

    private float montantRSA;
    private float montantPrimeActivite;
    private float montantAideLogement;
    private float montantAgepi;
    private float montantAideMobilite;
    private float montantComplementARE;
    private float montantDeductionsComplementARE;
    private float nombreJoursIndemnisesComplementARE;
    private float nombreJoursRestantsARE;
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

    public float getMontantComplementARE() {
	return montantComplementARE;
    }

    public void setMontantComplementARE(float montantComplementARE) {
	this.montantComplementARE = montantComplementARE;
    }

    public float getMontantDeductionsComplementARE() {
	return montantDeductionsComplementARE;
    }

    public void setMontantDeductionsComplementARE(float montantDeductionsComplementARE) {
	this.montantDeductionsComplementARE = montantDeductionsComplementARE;
    }

    public float getNombreJoursIndemnisesComplementARE() {
	return nombreJoursIndemnisesComplementARE;
    }

    public void setNombreJoursIndemnisesComplementARE(float nombreJoursIndemnisesComplementARE) {
	this.nombreJoursIndemnisesComplementARE = nombreJoursIndemnisesComplementARE;
    }

    public float getNombreJoursRestantsARE() {
	return nombreJoursRestantsARE;
    }

    public void setNombreJoursRestantsARE(float nombreJoursRestantsARE) {
	this.nombreJoursRestantsARE = nombreJoursRestantsARE;
    }

    @Override
    public String toString() {
	return "OpenFiscaRetourSimulation [montantRSA=" + montantRSA + ", montantPrimeActivite=" + montantPrimeActivite + ", montantAideLogement=" + montantAideLogement
		+ ", montantAgepi=" + montantAgepi + ", montantAideMobilite=" + montantAideMobilite + ", montantComplementARE=" + montantComplementARE
		+ ", montantDeductionsComplementARE=" + montantDeductionsComplementARE + ", nombreJoursIndemnisesComplementARE=" + nombreJoursIndemnisesComplementARE
		+ ", nombreJoursRestantsComplementARE=" + nombreJoursRestantsARE + ", typeAideLogement=" + typeAideLogement + "]";
    }
}
