package fr.poleemploi.estime.services.ressources;

public class AllocationASS {

    private Float allocationJournaliereNet;
    private Float allocationMensuelleNet;

    public Float getAllocationJournaliereNet() {
	return allocationJournaliereNet;
    }

    public void setAllocationJournaliereNet(Float allocationJournaliereNet) {
	this.allocationJournaliereNet = allocationJournaliereNet;
    }

    public Float getAllocationMensuelleNet() {
	return allocationMensuelleNet;
    }

    public void setAllocationMensuelleNet(Float allocationMensuelleNet) {
	this.allocationMensuelleNet = allocationMensuelleNet;
    }

    @Override
    public String toString() {
	return "AllocationASS [allocationJournaliereNet=" + allocationJournaliereNet + ", allocationMensuelleNet=" + allocationMensuelleNet + "]";
    }
}
