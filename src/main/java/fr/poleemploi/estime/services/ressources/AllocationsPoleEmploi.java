package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AllocationsPoleEmploi {
    
    private Float allocationJournaliereNet;
    private Float allocationMensuelleNet;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateDerniereOuvertureDroitASS;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateReliquat;
    private Integer dureeReliquat;

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
    public LocalDate getDateDerniereOuvertureDroitASS() {
        return dateDerniereOuvertureDroitASS;
    }
    public void setDateDerniereOuvertureDroitASS(LocalDate dateDerniereOuvertureDroitASS) {
        this.dateDerniereOuvertureDroitASS = dateDerniereOuvertureDroitASS;
    }
    public LocalDate getDateReliquat() {
        return dateReliquat;
    }
    public void setDateReliquat(LocalDate dateReliquat) {
        this.dateReliquat = dateReliquat;
    }
    public Integer getDureeReliquat() {
        return dureeReliquat;
    }
    public void setDureeReliquat(Integer dureeReliquat) {
        this.dureeReliquat = dureeReliquat;
    }
    @Override
    public String toString() {
        return "AllocationsPoleEmploi [allocationJournaliereNet=" + allocationJournaliereNet
                + ", allocationMensuelleNet=" + allocationMensuelleNet + ", dateDerniereOuvertureDroitASS="
                + dateDerniereOuvertureDroitASS + ", dateReliquat=" + dateReliquat + ", dureeReliquat=" + dureeReliquat
                + "]";
    }
}
