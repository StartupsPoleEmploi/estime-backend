package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenFiscaIndividu {

    @JsonProperty("date_naissance")
    private OpenFiscaPeriodes dateNaissance;
    @JsonProperty("enfant_a_charge")
    private OpenFiscaPeriodes enfantACharge;
    @JsonProperty("statut_marital")
    private OpenFiscaPeriodes statutMarital;
    @JsonProperty("salaire_de_base")
    private OpenFiscaPeriodes salaireDeBase;
    @JsonProperty("salaire_imposable")
    private OpenFiscaPeriodes salaireImposable;
    @JsonProperty("aah")
    private OpenFiscaPeriodes allocationAdulteHandicape;
    @JsonProperty("ass")
    private OpenFiscaPeriodes allocationSolidariteSpecifique;
    @JsonProperty("chomage_net")
    private OpenFiscaPeriodes chomageNet;
    @JsonProperty("revenus_locatifs")
    private OpenFiscaPeriodes revenusLocatifs;
    @JsonProperty("rpns_autres_revenus")
    private OpenFiscaPeriodes chiffreAffairesIndependant;
    @JsonProperty("rpns_micro_entreprise_benefice")
    private OpenFiscaPeriodes beneficesMicroEntreprise;
    @JsonProperty("pensions_alimentaires_percues")
    private OpenFiscaPeriodes pensionsAlimentaires;
    @JsonProperty("pensions_invalidite")
    private OpenFiscaPeriodes pensionInvalidite;
    @JsonProperty("asi")
    private OpenFiscaPeriodes allocationSupplementaireInvalidite;
    @JsonProperty("retraite_nette")
    private OpenFiscaPeriodes pensionRetraite;

    @JsonProperty("aah")
    public OpenFiscaPeriodes getAllocationAdulteHandicape() {
	return allocationAdulteHandicape;
    }

    public void setAllocationAdulteHandicape(OpenFiscaPeriodes allocationAdulteHandicape) {
	this.allocationAdulteHandicape = allocationAdulteHandicape;
    }

    @JsonProperty("asi")
    public OpenFiscaPeriodes getAllocationSupplementaireInvalidite() {
	return allocationSupplementaireInvalidite;
    }

    public void setAllocationSupplementaireInvalidite(OpenFiscaPeriodes allocationSupplementaireInvalidite) {
	this.allocationSupplementaireInvalidite = allocationSupplementaireInvalidite;
    }

    @JsonProperty("ass")
    public OpenFiscaPeriodes getAllocationSolidariteSpecifique() {
	return allocationSolidariteSpecifique;
    }

    public void setAllocationSolidariteSpecifique(OpenFiscaPeriodes allocationSolidariteSpecifique) {
	this.allocationSolidariteSpecifique = allocationSolidariteSpecifique;
    }

    @JsonProperty("chomage_net")
    public OpenFiscaPeriodes getChomageNet() {
	return chomageNet;
    }

    public void setChomageNet(OpenFiscaPeriodes chomageNet) {
	this.chomageNet = chomageNet;
    }

    @JsonProperty("date_naissance")
    public OpenFiscaPeriodes getDateNaissance() {
	return dateNaissance;
    }

    public void setDateNaissance(OpenFiscaPeriodes dateNaissance) {
	this.dateNaissance = dateNaissance;
    }

    @JsonProperty("enfant_a_charge")
    public OpenFiscaPeriodes getEnfantACharge() {
	return enfantACharge;
    }

    public void setEnfantACharge(OpenFiscaPeriodes enfantACharge) {
	this.enfantACharge = enfantACharge;
    }

    @JsonProperty("pensions_invalidite")
    public OpenFiscaPeriodes getPensionInvalidite() {
	return pensionInvalidite;
    }

    public void setPensionInvalidite(OpenFiscaPeriodes pensionInvalidite) {
	this.pensionInvalidite = pensionInvalidite;
    }

    @JsonProperty("revenus_locatifs")
    public OpenFiscaPeriodes getRevenusLocatifs() {
	return revenusLocatifs;
    }

    public void setRevenusLocatifs(OpenFiscaPeriodes revenusLocatifs) {
	this.revenusLocatifs = revenusLocatifs;
    }

    @JsonProperty("rpns_autres_revenus")
    public OpenFiscaPeriodes getChiffreAffairesIndependant() {
	return chiffreAffairesIndependant;
    }

    public void setChiffreAffairesIndependant(OpenFiscaPeriodes chiffreAffairesIndependant) {
	this.chiffreAffairesIndependant = chiffreAffairesIndependant;
    }

    @JsonProperty("rpns_micro_entreprise_benefice")
    public OpenFiscaPeriodes getBeneficesMicroEntreprise() {
	return beneficesMicroEntreprise;
    }

    public void setBeneficesMicroEntreprise(OpenFiscaPeriodes beneficesMicroEntreprise) {
	this.beneficesMicroEntreprise = beneficesMicroEntreprise;
    }

    @JsonProperty("pensions_alimentaires_percues")
    public OpenFiscaPeriodes getPensionsAlimentaires() {
	return pensionsAlimentaires;
    }

    public void setPensionsAlimentaires(OpenFiscaPeriodes pensionsAlimentaires) {
	this.pensionsAlimentaires = pensionsAlimentaires;
    }

    @JsonProperty("retraite_nette")
    public OpenFiscaPeriodes getPensionRetraite() {
	return pensionRetraite;
    }

    public void setPensionRetraite(OpenFiscaPeriodes pensionRetraite) {
	this.pensionRetraite = pensionRetraite;
    }

    @JsonProperty("salaire_de_base")
    public OpenFiscaPeriodes getSalaireDeBase() {
	return salaireDeBase;
    }

    public void setSalaireDeBase(OpenFiscaPeriodes salaireDeBase) {
	this.salaireDeBase = salaireDeBase;
    }

    @JsonProperty("salaire_imposable")
    public OpenFiscaPeriodes getSalaireImposable() {
	return salaireImposable;
    }

    public void setSalaireImposable(OpenFiscaPeriodes salaireImposable) {
	this.salaireImposable = salaireImposable;
    }

    @JsonProperty("statut_marital")
    public OpenFiscaPeriodes getStatutMarital() {
	return statutMarital;
    }

    public void setStatutMarital(OpenFiscaPeriodes statutMarital) {
	this.statutMarital = statutMarital;
    }

    @Override
    public String toString() {
	return OpenFiscaObjectMapperService.getJsonStringFromObject(this);
    }

}
