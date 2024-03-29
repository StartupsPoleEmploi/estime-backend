package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AllocationASS {

    private Float allocationJournaliereNet;
    private Float allocationMensuelleNet;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateDerniereOuvertureDroit;

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

    public LocalDate getDateDerniereOuvertureDroit() {
	return dateDerniereOuvertureDroit;
    }

    public void setDateDerniereOuvertureDroit(LocalDate dateDerniereOuvertureDroit) {
	this.dateDerniereOuvertureDroit = dateDerniereOuvertureDroit;
    }

    @Override
    public String toString() {
	return "AllocationASS [allocationJournaliereNet=" + allocationJournaliereNet + ", allocationMensuelleNet=" + allocationMensuelleNet + ", dateDerniereOuvertureDroit="
		+ dateDerniereOuvertureDroit + "]";
    }
}
