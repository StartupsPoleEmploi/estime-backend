package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AllocationARE {

    private Float allocationJournaliereBrute;
    private Float allocationMensuelleNet;
    private Float salaireJournalierReferenceBrut;
    private Float nombreJoursRestants;
    @JsonProperty("hasDegressiviteAre")
    private boolean hasDegressiviteAre;
    private boolean isTauxPlein;
    private boolean isTauxReduit;

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

    public Float getNombreJoursRestants() {
	return nombreJoursRestants;
    }

    public void setNombreJoursRestants(Float nombreJoursRestants) {
	this.nombreJoursRestants = nombreJoursRestants;
    }

    public boolean hasDegressiviteAre() {
	return hasDegressiviteAre;
    }

    @JsonProperty("hasDegressiviteAre")
    public void setHasDegressiviteAre(boolean hasDegressiviteAre) {
	this.hasDegressiviteAre = hasDegressiviteAre;
    }

    public boolean isTauxPlein() {
	return isTauxPlein;
    }

    public void setTauxPlein(boolean isTauxPlein) {
	this.isTauxPlein = isTauxPlein;
    }

    public boolean isTauxReduit() {
	return isTauxReduit;
    }

    public void setTauxReduit(boolean isTauxReduit) {
	this.isTauxReduit = isTauxReduit;
    }

    @Override
    public String toString() {
	return "AllocationARE [allocationJournaliereBrute=" + allocationJournaliereBrute + ", allocationMensuelleNet=" + allocationMensuelleNet
		+ ", salaireJournalierReferenceBrut=" + salaireJournalierReferenceBrut + ", nombreJoursRestants=" + nombreJoursRestants + ", hasDegressiviteAre="
		+ hasDegressiviteAre + ", isTauxPlein=" + isTauxPlein + ", isTauxReduit=" + isTauxReduit + "]";
    }
}
