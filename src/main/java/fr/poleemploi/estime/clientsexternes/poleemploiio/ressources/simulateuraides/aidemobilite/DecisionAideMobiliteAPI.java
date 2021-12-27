package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.aidemobilite;

public class DecisionAideMobiliteAPI {
    private String dateDecision;
    private String libelleMotifRejet;
    private String nature;
    private float montant;

    public String getDateDecision() {
	return dateDecision;
    }

    public void setDateDecision(String dateDecision) {
	this.dateDecision = dateDecision;
    }

    public String getLibelleMotifRejet() {
	return libelleMotifRejet;
    }

    public void setLibelleMotifRejet(String libelleMotifRejet) {
	this.libelleMotifRejet = libelleMotifRejet;
    }

    public String getNature() {
	return nature;
    }

    public void setNature(String nature) {
	this.nature = nature;
    }

    public float getMontant() {
	return montant;
    }

    public void setMontant(float montant) {
	this.montant = montant;
    }

    @Override
    public String toString() {
	return "DecisionAideMobiliteAPI [dateDecision=" + dateDecision + ", libelleMotifRejet=" + libelleMotifRejet + ", nature=" + nature + ", montant=" + montant + "]";
    }

}
