package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

public class DecisionAgepiAPI {
	private String dateDecision;
	private float montant;
	private String nature;
	
	
	public String getDateDecision() {
		return dateDecision;
	}
	public void setDateDecision(String dateDecision) {
		this.dateDecision = dateDecision;
	}
	public float getMontant() {
		return montant;
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
	
}
