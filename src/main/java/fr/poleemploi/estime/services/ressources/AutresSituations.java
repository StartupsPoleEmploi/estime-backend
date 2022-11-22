package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutresSituations {

    private boolean salaire;
    private boolean alternant;
    private boolean formation;
    private boolean independant;
    private boolean cej;
    private boolean ada;
    @JsonProperty("securisationProfessionnelle")
    private boolean securisationProfessionnelle;
    private boolean autre;
    @JsonProperty("autreContenu")
    private String autreContenu;

    public boolean isSalaire() {
	return salaire;
    }

    public void setSalaire(boolean salaire) {
	this.salaire = salaire;
    }

    public boolean isAlternant() {
	return alternant;
    }

    public void setAlternant(boolean alternant) {
	this.alternant = alternant;
    }

    public boolean isFormation() {
	return formation;
    }

    public void setFormation(boolean formation) {
	this.formation = formation;
    }

    public boolean isIndependant() {
	return independant;
    }

    public void setIndependant(boolean independant) {
	this.independant = independant;
    }

    public boolean isCej() {
	return cej;
    }

    public void setCej(boolean cej) {
	this.cej = cej;
    }

    public boolean isAda() {
	return ada;
    }

    public void setAda(boolean ada) {
	this.ada = ada;
    }

    @JsonProperty("securisationProfessionnelle")
    public boolean isSecurisationProfessionnelle() {
	return securisationProfessionnelle;
    }

    public void setSecurisationProfessionnelle(boolean securisationProfessionnelle) {
	this.securisationProfessionnelle = securisationProfessionnelle;
    }

    public boolean isAutre() {
	return autre;
    }

    public void setAutre(boolean autre) {
	this.autre = autre;
    }

    @JsonProperty("autreContenu")
    public String getAutreContenu() {
	return autreContenu;
    }

    public void setAutreContenu(String autreContenu) {
	this.autreContenu = autreContenu;
    }

    @Override
    public String toString() {
	return "AutresSituations [salaire=" + salaire + ", alternant=" + alternant + ", formation=" + formation + ", independant=" + independant + ", cej=" + cej + ", ada=" + ada
		+ ", securisationProfessionnelle=" + securisationProfessionnelle + ", autre=" + autre + ", autreContenu=" + autreContenu + "]";
    }
}
