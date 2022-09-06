package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Logement {

    private StatutOccupationLogement statutOccupationLogement;
    private Coordonnees coordonnees;
    @JsonProperty("isCrous")
    private boolean isCrous;
    @JsonProperty("isColoc")
    private boolean isColloc;
    @JsonProperty("isConventionne")
    private boolean isConventionne;
    @JsonProperty("isChambre")
    private boolean isChambre;
    private Float montantLoyer;

    public Coordonnees getCoordonnees() {
	return coordonnees;
    }

    public void setCoordonnees(Coordonnees coordonnees) {
	this.coordonnees = coordonnees;
    }

    public StatutOccupationLogement getStatutOccupationLogement() {
	return statutOccupationLogement;
    }

    public void setStatutOccupationLogement(StatutOccupationLogement statutOccupationLogement) {
	this.statutOccupationLogement = statutOccupationLogement;
    }

    public boolean isCrous() {
	return isCrous;
    }

    @JsonProperty("isCrous")
    public void setCrous(boolean isCrous) {
	this.isCrous = isCrous;
    }

    public boolean isColloc() {
	return isColloc;
    }

    @JsonProperty("isColloc")
    public void setColloc(boolean isColloc) {
	this.isColloc = isColloc;
    }

    public boolean isConventionne() {
	return isConventionne;
    }

    @JsonProperty("isConventionne")
    public void setConventionne(boolean isConventionne) {
	this.isConventionne = isConventionne;
    }

    public boolean isChambre() {
	return isChambre;
    }

    @JsonProperty("isChambre")
    public void setChambre(boolean isChambre) {
	this.isChambre = isChambre;
    }

    public Float getMontantLoyer() {
	return montantLoyer;
    }

    public void setMontantLoyer(Float montantLoyer) {
	this.montantLoyer = montantLoyer;
    }

    @Override
    public String toString() {
	return "Logement [statutOccupationLogement=" + statutOccupationLogement + ", coordonnees=" + coordonnees + ", isCrous=" + isCrous + ", isColloc=" + isColloc
		+ ", isConventionne=" + isConventionne + ", isChambre=" + isChambre + ", montantLoyer=" + montantLoyer + "]";
    }

}
