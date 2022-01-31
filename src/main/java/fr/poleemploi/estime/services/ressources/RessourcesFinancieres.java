package fr.poleemploi.estime.services.ressources;

public class RessourcesFinancieres {

    private AidesCAF aidesCAF;
    private AidesPoleEmploi aidesPoleEmploi;
    private AidesCPAM aidesCPAM;
    private Boolean hasTravailleAuCoursDerniersMois;
    private Integer nombreMoisTravaillesDerniersMois;
    private Float chiffreAffairesIndependantDernierExercice;
    private Float beneficesMicroEntrepriseDernierExercice;
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

    public Float getChiffreAffairesIndependantDernierExercice() {
	return chiffreAffairesIndependantDernierExercice;
    }

    public void setChiffreAffairesIndependantDernierExercice(Float chiffreAffairesIndependantDernierExercice) {
	this.chiffreAffairesIndependantDernierExercice = chiffreAffairesIndependantDernierExercice;
    }

    public Float getBeneficesMicroEntrepriseDernierExercice() {
	return beneficesMicroEntrepriseDernierExercice;
    }

    public void setBeneficesMicroEntrepriseDernierExercice(Float beneficesMicroEntrepriseDernierExercice) {
	this.beneficesMicroEntrepriseDernierExercice = beneficesMicroEntrepriseDernierExercice;
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
	return "RessourcesFinancieres [aidesCAF=" + aidesCAF + ", aidesPoleEmploi=" + aidesPoleEmploi + ", aidesCPAM=" + aidesCPAM + ", hasTravailleAuCoursDerniersMois="
		+ hasTravailleAuCoursDerniersMois + ", nombreMoisTravaillesDerniersMois=" + nombreMoisTravaillesDerniersMois + ", chiffreAffairesIndependantDernierExercice="
		+ chiffreAffairesIndependantDernierExercice + ", beneficesMicroEntrepriseDernierExercice=" + beneficesMicroEntrepriseDernierExercice + ", revenusImmobilier3DerniersMois="
		+ revenusImmobilier3DerniersMois + ", salaire=" + salaire + ", periodeTravailleeAvantSimulation=" + periodeTravailleeAvantSimulation + "]";
    }
}
