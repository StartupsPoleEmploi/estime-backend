package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi;

public class DecisionAGEPIAPI {
    private String dateDecision;
    private float montant;
    private String nature;
    private String libelleMotifRejet;


    public String getDateDecision() {
	return dateDecision;
    }
    public void setDateDecision(String dateDecision) {
	this.dateDecision = dateDecision;
    }
    public float getMontant() {
	return this.montant;
    }
    public void setMontant(float montant) {
	this.montant = montant;
    }
    public String getNature() {
	return nature;
    }
    public void setNature(String nature) {
	this.nature = nature;
    }
    public String getLibelleMotifRejet() {
	return libelleMotifRejet;
    }
    public void setLibelleMotifRejet(String libelleMotifRejet) {
	this.libelleMotifRejet = libelleMotifRejet;
    }

}
