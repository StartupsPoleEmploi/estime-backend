package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AAH;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASI;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.BENEFICES_MICRO_ENTREPRISE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHIFFRE_AFFAIRES_INDEPENDANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHOMAGE_NET;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DATE_NAISSANCE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT_A_CHARGE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSIONS_ALIMENTAIRES_PERCUES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSION_INVALIDITE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSION_RETRAITE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.REVENUS_LOCATIFS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_BASE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_IMPOSABLE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.STATUT_MARITAL;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenFiscaIndividu {

    @JsonProperty(DATE_NAISSANCE)
    private OpenFiscaPeriodes dateNaissance;
    @JsonProperty(ENFANT_A_CHARGE)
    private OpenFiscaPeriodes enfantACharge;
    @JsonProperty(STATUT_MARITAL)
    private OpenFiscaPeriodes statutMarital;
    @JsonProperty(SALAIRE_BASE)
    private OpenFiscaPeriodes salaireDeBase;
    @JsonProperty(SALAIRE_IMPOSABLE)
    private OpenFiscaPeriodes salaireImposable;
    @JsonProperty(AAH)
    private OpenFiscaPeriodes allocationAdulteHandicape;
    @JsonProperty(ASS)
    private OpenFiscaPeriodes allocationSolidariteSpecifique;
    @JsonProperty(CHOMAGE_NET)
    private OpenFiscaPeriodes chomageNet;
    @JsonProperty(REVENUS_LOCATIFS)
    private OpenFiscaPeriodes revenusLocatifs;
    @JsonProperty(CHIFFRE_AFFAIRES_INDEPENDANT)
    private OpenFiscaPeriodes chiffreAffairesIndependant;
    @JsonProperty(BENEFICES_MICRO_ENTREPRISE)
    private OpenFiscaPeriodes beneficesMicroEntreprise;
    @JsonProperty(PENSIONS_ALIMENTAIRES_PERCUES)
    private OpenFiscaPeriodes pensionsAlimentaires;
    @JsonProperty(PENSION_INVALIDITE)
    private OpenFiscaPeriodes pensionInvalidite;
    @JsonProperty(ASI)
    private OpenFiscaPeriodes allocationSupplementaireInvalidite;
    @JsonProperty(PENSION_RETRAITE)
    private OpenFiscaPeriodes pensionRetraite;

    @JsonProperty(AAH)
    public OpenFiscaPeriodes getAllocationAdulteHandicape() {
	return allocationAdulteHandicape;
    }

    public void setAllocationAdulteHandicape(OpenFiscaPeriodes allocationAdulteHandicape) {
	this.allocationAdulteHandicape = allocationAdulteHandicape;
    }

    @JsonProperty(ASI)
    public OpenFiscaPeriodes getAllocationSupplementaireInvalidite() {
	return allocationSupplementaireInvalidite;
    }

    public void setAllocationSupplementaireInvalidite(OpenFiscaPeriodes allocationSupplementaireInvalidite) {
	this.allocationSupplementaireInvalidite = allocationSupplementaireInvalidite;
    }

    @JsonProperty(ASS)
    public OpenFiscaPeriodes getAllocationSolidariteSpecifique() {
	return allocationSolidariteSpecifique;
    }

    public void setAllocationSolidariteSpecifique(OpenFiscaPeriodes allocationSolidariteSpecifique) {
	this.allocationSolidariteSpecifique = allocationSolidariteSpecifique;
    }

    @JsonProperty(CHOMAGE_NET)
    public OpenFiscaPeriodes getChomageNet() {
	return chomageNet;
    }

    public void setChomageNet(OpenFiscaPeriodes chomageNet) {
	this.chomageNet = chomageNet;
    }

    @JsonProperty(DATE_NAISSANCE)
    public OpenFiscaPeriodes getDateNaissance() {
	return dateNaissance;
    }

    public void setDateNaissance(OpenFiscaPeriodes dateNaissance) {
	this.dateNaissance = dateNaissance;
    }

    @JsonProperty(ENFANT_A_CHARGE)
    public OpenFiscaPeriodes getEnfantACharge() {
	return enfantACharge;
    }

    public void setEnfantACharge(OpenFiscaPeriodes enfantACharge) {
	this.enfantACharge = enfantACharge;
    }

    @JsonProperty(PENSION_INVALIDITE)
    public OpenFiscaPeriodes getPensionInvalidite() {
	return pensionInvalidite;
    }

    public void setPensionInvalidite(OpenFiscaPeriodes pensionInvalidite) {
	this.pensionInvalidite = pensionInvalidite;
    }

    @JsonProperty(REVENUS_LOCATIFS)
    public OpenFiscaPeriodes getRevenusLocatifs() {
	return revenusLocatifs;
    }

    public void setRevenusLocatifs(OpenFiscaPeriodes revenusLocatifs) {
	this.revenusLocatifs = revenusLocatifs;
    }

    @JsonProperty(CHIFFRE_AFFAIRES_INDEPENDANT)
    public OpenFiscaPeriodes getChiffreAffairesIndependant() {
	return chiffreAffairesIndependant;
    }

    public void setChiffreAffairesIndependant(OpenFiscaPeriodes chiffreAffairesIndependant) {
	this.chiffreAffairesIndependant = chiffreAffairesIndependant;
    }

    @JsonProperty(BENEFICES_MICRO_ENTREPRISE)
    public OpenFiscaPeriodes getBeneficesMicroEntreprise() {
	return beneficesMicroEntreprise;
    }

    public void setBeneficesMicroEntreprise(OpenFiscaPeriodes beneficesMicroEntreprise) {
	this.beneficesMicroEntreprise = beneficesMicroEntreprise;
    }

    @JsonProperty(PENSIONS_ALIMENTAIRES_PERCUES)
    public OpenFiscaPeriodes getPensionsAlimentaires() {
	return pensionsAlimentaires;
    }

    public void setPensionsAlimentaires(OpenFiscaPeriodes pensionsAlimentaires) {
	this.pensionsAlimentaires = pensionsAlimentaires;
    }

    @JsonProperty(PENSION_RETRAITE)
    public OpenFiscaPeriodes getPensionRetraite() {
	return pensionRetraite;
    }

    public void setPensionRetraite(OpenFiscaPeriodes pensionRetraite) {
	this.pensionRetraite = pensionRetraite;
    }

    @JsonProperty(SALAIRE_BASE)
    public OpenFiscaPeriodes getSalaireDeBase() {
	return salaireDeBase;
    }

    public void setSalaireDeBase(OpenFiscaPeriodes salaireDeBase) {
	this.salaireDeBase = salaireDeBase;
    }

    @JsonProperty(SALAIRE_IMPOSABLE)
    public OpenFiscaPeriodes getSalaireImposable() {
	return salaireImposable;
    }

    public void setSalaireImposable(OpenFiscaPeriodes salaireImposable) {
	this.salaireImposable = salaireImposable;
    }

    @JsonProperty(STATUT_MARITAL)
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
