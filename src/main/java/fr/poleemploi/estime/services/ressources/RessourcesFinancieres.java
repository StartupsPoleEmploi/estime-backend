package fr.poleemploi.estime.services.ressources;

public class RessourcesFinancieres {

    private AidesCAF aidesCAF;
    private AidesPoleEmploi aidesPoleEmploi;
    private AidesCPAM aidesCPAM;
    private Boolean hasTravailleAuCoursDerniersMois;
    private Integer nombreMoisTravaillesDerniersMois;
    private Float beneficesTravailleurIndependantDernierExercice;
    private Float revenusMicroEntreprise3DerniersMois;
    private Float revenusImmobilier3DerniersMois;
    private Salaire salaire;
    private PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation;
    
    public AidesCAF getAidesCAF() {
        return aidesCAF;
    }
    public void setAidesCAF(AidesCAF aidesCAF) {
        this.aidesCAF = aidesCAF;
    }
    public AidesPoleEmploi getAidesPoleEmploi() {
        return aidesPoleEmploi;
    }
    public void setAidesPoleEmploi(AidesPoleEmploi aidesPoleEmploi) {
        this.aidesPoleEmploi = aidesPoleEmploi;
    }
    public AidesCPAM getAidesCPAM() {
        return aidesCPAM;
    }
    public void setAidesCPAM(AidesCPAM aidesCPAM) {
        this.aidesCPAM = aidesCPAM;
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
    public Float getBeneficesTravailleurIndependantDernierExercice() {
        return beneficesTravailleurIndependantDernierExercice;
    }
    public void setBeneficesTravailleurIndependantDernierExercice(Float beneficesTravailleurIndependantDernierExercice) {
        this.beneficesTravailleurIndependantDernierExercice = beneficesTravailleurIndependantDernierExercice;
    }
    public Float getRevenusMicroEntreprise3DerniersMois() {
        return revenusMicroEntreprise3DerniersMois;
    }
    public void setRevenusMicroEntreprise3DerniersMois(Float revenusMicroEntreprise3DerniersMois) {
        this.revenusMicroEntreprise3DerniersMois = revenusMicroEntreprise3DerniersMois;
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
    public PeriodeTravailleeAvantSimulation getPeriodeTravailleeAvantSimulation() {
        return periodeTravailleeAvantSimulation;
    }
    public void setPeriodeTravailleeAvantSimulation(PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation) {
        this.periodeTravailleeAvantSimulation = periodeTravailleeAvantSimulation;
    }
    
    @Override
    public String toString() {
        return "RessourcesFinancieres [aidesCAF=" + aidesCAF + ", aidesPoleEmploi=" + aidesPoleEmploi + ", aidesCPAM="
                + aidesCPAM + ", hasTravailleAuCoursDerniersMois=" + hasTravailleAuCoursDerniersMois
                + ", nombreMoisTravaillesDerniersMois=" + nombreMoisTravaillesDerniersMois
                + ", beneficesTravailleurIndependantDernierExercice=" + beneficesTravailleurIndependantDernierExercice
                + ", revenusMicroEntreprise3DerniersMois=" + revenusMicroEntreprise3DerniersMois
                + ", revenusImmobilier3DerniersMois=" + revenusImmobilier3DerniersMois + ", salaire=" + salaire
                + ", periodeTravailleeAvantSimulation=" + periodeTravailleeAvantSimulation + "]";
    }    
}
