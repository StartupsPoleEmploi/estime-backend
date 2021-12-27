package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConditionAGEPIAPI {
	private ConditionDureeEntreFaitsGenerateurs conditionDureeEntreFaitsGenerateurs;
	private ConditionInscription conditionInscription;
	private ConditionGardeEnfant conditionGardeEnfant;
	private ConditionChampTerritorial conditionChampTerritorial;
	private ConditionDuree conditionDuree;
	private ConditionRessource conditionRessource;
	private ConditionDateDepot conditionDateDepot;
	
	public ConditionDureeEntreFaitsGenerateurs getConditionDureeEntreFaitsGenerateurs() {
		return conditionDureeEntreFaitsGenerateurs;
	}
	public void setConditionDureeEntreFaitsGenerateurs(
			ConditionDureeEntreFaitsGenerateurs conditionDureeEntreFaitsGenerateurs) {
		this.conditionDureeEntreFaitsGenerateurs = conditionDureeEntreFaitsGenerateurs;
	}
	public ConditionInscription getConditionInscription() {
		return conditionInscription;
	}
	public void setConditionInscription(ConditionInscription conditionInscription) {
		this.conditionInscription = conditionInscription;
	}
	public ConditionGardeEnfant getConditionGardeEnfant() {
		return conditionGardeEnfant;
	}
	public void setConditionGardeEnfant(ConditionGardeEnfant conditionGardeEnfant) {
		this.conditionGardeEnfant = conditionGardeEnfant;
	}
	public ConditionChampTerritorial getConditionChampTerritorial() {
		return conditionChampTerritorial;
	}
	public void setConditionChampTerritorial(ConditionChampTerritorial conditionChampTerritorial) {
		this.conditionChampTerritorial = conditionChampTerritorial;
	}
	public ConditionDuree getConditionDuree() {
		return conditionDuree;
	}
	public void setConditionDuree(ConditionDuree conditionDuree) {
		this.conditionDuree = conditionDuree;
	}
	public ConditionRessource getConditionRessource() {
		return conditionRessource;
	}
	public void setConditionRessource(ConditionRessource conditionRessource) {
		this.conditionRessource = conditionRessource;
	}
	public ConditionDateDepot getConditionDateDepot() {
		return conditionDateDepot;
	}
	public void setConditionDateDepot(ConditionDateDepot conditionDateDepot) {
		this.conditionDateDepot = conditionDateDepot;
	}
}
