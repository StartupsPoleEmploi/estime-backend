package fr.poleemploi.estime.services.ressources;

public class AllocationARE {

    private Float allocationJournaliereBrute;
    private Float allocationMensuelleNet;
    private Float salaireJournalierReferenceBrut;
    private Float nombreJoursRestants;

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

    @Override
    public String toString() {
	return "AllocationARE [allocationJournaliereBrute=" + allocationJournaliereBrute + ", allocationMensuelleNet=" + allocationMensuelleNet
		+ ", salaireJournalierReferenceBrut=" + salaireJournalierReferenceBrut + ", nombreJoursRestants=" + nombreJoursRestants + "]";
    }
}
