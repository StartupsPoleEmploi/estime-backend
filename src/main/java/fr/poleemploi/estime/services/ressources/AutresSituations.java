package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutresSituations {

    private boolean salaire;
    private boolean alternant;
    private boolean formation;
    private boolean cej;
    private boolean ada;
    @JsonProperty("securisation_professionnelle")
    private boolean securisationProfessionnelle;
    @JsonProperty("aucune_ressource")
    private boolean aucuneRessource;
    private boolean autre;
    @JsonProperty("autre_contenu")
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

    @JsonProperty("securisation_professionnelle")
    public boolean isSecurisationProfessionnelle() {
	return securisationProfessionnelle;
    }

    public void setSecurisationProfessionnelle(boolean securisationProfessionnelle) {
	this.securisationProfessionnelle = securisationProfessionnelle;
    }

    @JsonProperty("aucune_ressource")
    public boolean isAucuneRessource() {
	return aucuneRessource;
    }

    public void setAucuneRessource(boolean aucuneRessource) {
	this.aucuneRessource = aucuneRessource;
    }

    public boolean isAutre() {
	return autre;
    }

    public void setAutre(boolean autre) {
	this.autre = autre;
    }

    @JsonProperty("autre_contenu")
    public String getAutreContenu() {
	return autreContenu;
    }

    public void setAutreContenu(String autreContenu) {
	this.autreContenu = autreContenu;
    }

    @Override
    public String toString() {
	return "AutresSituations [salaire=" + salaire + ", alternant=" + alternant + ", formation=" + formation + ", cej=" + cej + ", ada=" + ada + ", securisationProfessionnelle="
		+ securisationProfessionnelle + ", aucuneRessource=" + aucuneRessource + ", autre=" + autre + ", autreContenu=" + autreContenu + "]";
    }
}
