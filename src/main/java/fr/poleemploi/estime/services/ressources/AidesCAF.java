package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AidesCAF {

    private Float allocationAAH;
    private Float allocationRSA;
    private Float primeActivite;
    @JsonProperty("hasPrimeActivite")
    private Boolean hasPrimeActivite;
    private AidesLogement aidesLogement;
    private AidesFamiliales aidesFamiliales;
    private Integer prochaineDeclarationTrimestrielle;

    public AidesFamiliales getAidesFamiliales() {
	return aidesFamiliales;
    }

    public void setAidesFamiliales(AidesFamiliales aidesFamiliales) {
	this.aidesFamiliales = aidesFamiliales;
    }

    public Integer getProchaineDeclarationTrimestrielle() {
	return prochaineDeclarationTrimestrielle;
    }

    public void setProchaineDeclarationTrimestrielle(Integer prochaineDeclarationTrimestrielle) {
	this.prochaineDeclarationTrimestrielle = prochaineDeclarationTrimestrielle;
    }

    public Float getAllocationAAH() {
	return allocationAAH;
    }

    public void setAllocationAAH(Float allocationAAH) {
	this.allocationAAH = allocationAAH;
    }

    public Float getAllocationRSA() {
	return allocationRSA;
    }

    public void setAllocationRSA(Float allocationRSA) {
	this.allocationRSA = allocationRSA;
    }

    public Float getPrimeActivite() {
	return primeActivite;
    }

    public void setPrimeActivite(Float primeActivite) {
	this.primeActivite = primeActivite;
    }

    public AidesLogement getAidesLogement() {
	return aidesLogement;
    }

    public void setAidesLogement(AidesLogement aidesLogement) {
	this.aidesLogement = aidesLogement;
    }

    @JsonProperty("hasPrimeActivite")
    public Boolean hasPrimeActivite() {
	return hasPrimeActivite;
    }

    public void setHasPrimeActivite(Boolean hasPrimeActivite) {
	this.hasPrimeActivite = hasPrimeActivite;
    }

    @Override
    public String toString() {
	return "AidesCAF [allocationAAH=" + allocationAAH + ", allocationRSA=" + allocationRSA + ", primeActivite=" + primeActivite + ", hasPrimeActivite=" + hasPrimeActivite
		+ ", aidesLogement=" + aidesLogement + ", aidesFamiliales=" + aidesFamiliales + ", prochaineDeclarationTrimestrielle=" + prochaineDeclarationTrimestrielle + "]";
    }
}
