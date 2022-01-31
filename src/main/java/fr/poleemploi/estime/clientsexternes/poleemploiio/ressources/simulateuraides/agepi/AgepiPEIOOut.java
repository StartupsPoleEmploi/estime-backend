package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AgepiPEIOOut {
    private String origine;
    private String dateDepot;	
    private String dateActionReclassement;	
    private String contexte;	
    private String natureContratTravail;	
    private String lieuFormationOuEmploi;	
    private String typeIntensite;	
    private String dateDernierJourIndemnise;
    private int intensite;	
    private int dureePeriodeEmploiOuFormation;	
    private int nombreEnfants;	
    private int nombreEnfantsMoins10Ans;
    private boolean eleveSeulEnfants;	
    private String codeTerritoire;
    private String categorie;
    private int tauxNetDroit;
    private boolean allocationEnCours;
    private int droitAREMinimal;
    private String libelleDroit;
    private ConditionAGEPIAPI conditionsAGEPIAPI;
    //TODO REFACTO JLA nécessaire ??
    @JsonProperty("decisionAGEPIAPI")
    private DecisionAGEPIAPI decisionAGEPIAPI;

    public DecisionAGEPIAPI getDecisionAgepiAPI() {
	return decisionAGEPIAPI;
    }
    public void setDecisionAgepiAPI(DecisionAGEPIAPI decisionAgepiAPI) {
	this.decisionAGEPIAPI = decisionAgepiAPI;
    }
    public String getOrigine() {
	return origine;
    }
    public void setOrigine(String origine) {
	this.origine = origine;
    }
    public String getDateDepot() {
	return dateDepot;
    }
    public void setDateDepot(String dateDepot) {
	this.dateDepot = dateDepot;
    }
    public String getDateActionReclassement() {
	return dateActionReclassement;
    }
    public void setDateActionReclassement(String dateActionReclassement) {
	this.dateActionReclassement = dateActionReclassement;
    }
    public String getContexte() {
	return contexte;
    }
    public void setContexte(String contexte) {
	this.contexte = contexte;
    }
    public String getNatureContratTravail() {
	return natureContratTravail;
    }
    public void setNatureContratTravail(String natureContratTravail) {
	this.natureContratTravail = natureContratTravail;
    }
    public String getLieuFormationOuEmploi() {
	return lieuFormationOuEmploi;
    }
    public void setLieuFormationOuEmploi(String lieuFormationOuEmploi) {
	this.lieuFormationOuEmploi = lieuFormationOuEmploi;
    }
    public String getTypeIntensite() {
	return typeIntensite;
    }
    public void setTypeIntensite(String typeIntensite) {
	this.typeIntensite = typeIntensite;
    }
    public int getIntensite() {
	return intensite;
    }
    public void setIntensite(int intensite) {
	this.intensite = intensite;
    }
    public int getDureePeriodeEmploiOuFormation() {
	return dureePeriodeEmploiOuFormation;
    }
    public void setDureePeriodeEmploiOuFormation(int dureePeriodeEmploiOuFormation) {
	this.dureePeriodeEmploiOuFormation = dureePeriodeEmploiOuFormation;
    }
    public int getNombreEnfants() {
	return nombreEnfants;
    }
    public void setNombreEnfants(int nombreEnfants) {
	this.nombreEnfants = nombreEnfants;
    }
    public int getNombreEnfantsMoins10Ans() {
	return nombreEnfantsMoins10Ans;
    }
    public void setNombreEnfantsMoins10Ans(int nombreEnfantsMoins10Ans) {
	this.nombreEnfantsMoins10Ans = nombreEnfantsMoins10Ans;
    }
    public boolean isEleveSeulEnfants() {
	return eleveSeulEnfants;
    }
    public void setEleveSeulEnfants(boolean eleveSeulEnfants) {
	this.eleveSeulEnfants  = eleveSeulEnfants;
    }
    public String getCodeTerritoire() {
	return codeTerritoire;
    }
    public void setCodeTerritoire(String codeTerritoire) {
	this.codeTerritoire = codeTerritoire;
    }
    public String getCategorie() {
	return categorie;
    }
    public void setCategorie(String categorie) {
	this.categorie = categorie;
    }
    public int getTauxNetDroit() {
	return tauxNetDroit;
    }
    public void setTauxNetDroit(int tauxNetDroit) {
	this.tauxNetDroit = tauxNetDroit;
    }
    public boolean isAllocationEnCours() {
	return allocationEnCours;
    }
    public void setAllocationEnCours(boolean allocationEnCours) {
	this.allocationEnCours = allocationEnCours;
    }
    public int getDroitAREMinimal() {
	return droitAREMinimal;
    }
    public void setDroitAREMinimal(int droitAREMinimal) {
	this.droitAREMinimal = droitAREMinimal;
    }
    public String getLibelleDroit() {
	return libelleDroit;
    }
    public void setLibelleDroit(String libelleDroit) {
	this.libelleDroit = libelleDroit;
    }
    public ConditionAGEPIAPI getConditionsAGEPIAPI() {
	return conditionsAGEPIAPI;
    }
    public void setConditionsAGEPIAPI(ConditionAGEPIAPI conditionsAGEPIAPI) {
	this.conditionsAGEPIAPI = conditionsAGEPIAPI;
    }
    public String getDateDernierJourIndemnise() {
	return dateDernierJourIndemnise;
    }
    public void setDateDernierJourIndemnise(String dateDernierJourIndemnise) {
	this.dateDernierJourIndemnise = dateDernierJourIndemnise;
    }
}
