package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AllocationARE {

    private Float allocationJournaliereBrute;
    private Float allocationMensuelleNet;
    private Float salaireJournalierReferenceBrut;
    private Integer nombreJoursRestants;
    @JsonProperty("hasDegressiviteAre")
    private Boolean hasDegressiviteAre;
    private Boolean isTauxPlein;
    private Boolean isTauxReduit;

    public Float getAllocationMensuelleNet() {
	return allocationMensuelleNet;
    }

    public void setAllocationMensuelleNet(Float allocationMensuelleNet) {
	this.allocationMensuelleNet = allocationMensuelleNet;
    }

    public Float getAllocationJournaliereBrute() {
	return allocationJournaliereBrute;
    }

    public void setAllocationJournaliereBrute(Float allocationJournaliereBrute) {
	this.allocationJournaliereBrute = allocationJournaliereBrute;
    }

    public Float getSalaireJournalierReferenceBrut() {
	return salaireJournalierReferenceBrut;
    }

    public void setSalaireJournalierReferenceBrut(Float salaireJournalierReferenceBrut) {
	this.salaireJournalierReferenceBrut = salaireJournalierReferenceBrut;
    }

    public Integer getNombreJoursRestants() {
	return nombreJoursRestants;
    }

    public void setNombreJoursRestants(Integer nombreJoursRestants) {
	this.nombreJoursRestants = nombreJoursRestants;
    }

    public Boolean hasDegressiviteAre() {
	return hasDegressiviteAre;
    }

    @JsonProperty("hasDegressiviteAre")
    public void setHasDegressiviteAre(Boolean hasDegressiviteAre) {
	this.hasDegressiviteAre = hasDegressiviteAre;
    }

    public Boolean isTauxPlein() {
	return isTauxPlein;
    }

    public void setTauxPlein(Boolean isTauxPlein) {
	this.isTauxPlein = isTauxPlein;
    }

    public Boolean isTauxReduit() {
	return isTauxReduit;
    }

    public void setTauxReduit(Boolean isTauxReduit) {
	this.isTauxReduit = isTauxReduit;
    }

    @Override
    public String toString() {
	return "AllocationARE [allocationJournaliereBrute=" + allocationJournaliereBrute + ", allocationMensuelleNet=" + allocationMensuelleNet
		+ ", salaireJournalierReferenceBrut=" + salaireJournalierReferenceBrut + ", nombreJoursRestants=" + nombreJoursRestants + ", hasDegressiviteAre="
		+ hasDegressiviteAre + ", isTauxPlein=" + isTauxPlein + ", isTauxReduit=" + isTauxReduit + "]";
    }
}
