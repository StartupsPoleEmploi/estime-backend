package fr.poleemploi.estime.services.ressources;

public class RessourcesFinancieres {

    private AllocationsCAF allocationsCAF;
    private AllocationsPoleEmploi allocationsPoleEmploi;
    private AllocationsCPAM allocationsCPAM;
    private Boolean hasTravailleAuCoursDerniersMois;
    private Integer nombreMoisTravaillesDerniersMois;
    private Float revenusCreateurEntreprise3DerniersMois;
    private Float revenusImmobilier3DerniersMois;
    private Salaire salaire;
    private SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation;
    
    public AllocationsCAF getAllocationsCAF() {
        return allocationsCAF;
    }
    public void setAllocationsCAF(AllocationsCAF allocationsCAF) {
        this.allocationsCAF = allocationsCAF;
    }
    public AllocationsPoleEmploi getAllocationsPoleEmploi() {
        return allocationsPoleEmploi;
    }
    public void setAllocationsPoleEmploi(AllocationsPoleEmploi allocationsPoleEmploi) {
        this.allocationsPoleEmploi = allocationsPoleEmploi;
    }
    public AllocationsCPAM getAllocationsCPAM() {
        return allocationsCPAM;
    }
    public void setAllocationsCPAM(AllocationsCPAM allocationsCPAM) {
        this.allocationsCPAM = allocationsCPAM;
    }
    public Boolean getHasTravailleAuCoursDerniersMois() {
        return hasTravailleAuCoursDerniersMois;
    }
    public void setHasTravailleAuCoursDerniersMois(Boolean hasTravailleAuCoursDerniersMois) {
        this.hasTravailleAuCoursDerniersMois = hasTravailleAuCoursDerniersMois;
    }
    public Integer getNombreMoisTravaillesDerniersMois() {
        return nombreMoisTravaillesDerniersMois;
    }
    public void setNombreMoisTravaillesDerniersMois(Integer nombreMoisTravaillesDerniersMois) {
        this.nombreMoisTravaillesDerniersMois = nombreMoisTravaillesDerniersMois;
    }
    public Float getRevenusCreateurEntreprise3DerniersMois() {
        return revenusCreateurEntreprise3DerniersMois;
    }
    public void setRevenusCreateurEntreprise3DerniersMois(Float revenusCreateurEntreprise3DerniersMois) {
        this.revenusCreateurEntreprise3DerniersMois = revenusCreateurEntreprise3DerniersMois;
    }
    public Float getRevenusImmobilier3DerniersMois() {
        return revenusImmobilier3DerniersMois;
    }
    public void setRevenusImmobilier3DerniersMois(Float revenusImmobilier3DerniersMois) {
        this.revenusImmobilier3DerniersMois = revenusImmobilier3DerniersMois;
    }
    public Salaire getSalaire() {
        return salaire;
    }
    public void setSalaire(Salaire salaire) {
        this.salaire = salaire;
    }
    public SalairesAvantPeriodeSimulation getSalairesAvantPeriodeSimulation() {
        return salairesAvantPeriodeSimulation;
    }
    public void setSalairesAvantPeriodeSimulation(SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation) {
        this.salairesAvantPeriodeSimulation = salairesAvantPeriodeSimulation;
    }
    @Override
    public String toString() {
        return "RessourcesFinancieres [allocationsCAF=" + allocationsCAF + ", allocationsPoleEmploi="
                + allocationsPoleEmploi + ", allocationsCPAM=" + allocationsCPAM + ", hasTravailleAuCoursDerniersMois="
                + hasTravailleAuCoursDerniersMois + ", nombreMoisTravaillesDerniersMois="
                + nombreMoisTravaillesDerniersMois + ", revenusCreateurEntreprise3DerniersMois="
                + revenusCreateurEntreprise3DerniersMois + ", revenusImmobilier3DerniersMois="
                + revenusImmobilier3DerniersMois + ", salaire=" + salaire + ", salairesAvantPeriodeSimulation="
                + salairesAvantPeriodeSimulation + "]";
    }
}
