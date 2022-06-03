package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AIDE_LOGEMENT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ALF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ALS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.APL;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANTS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PARENTS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PPA;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PRESTATION_ACCUEIL_JEUNE_ENFANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.RSA;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.RSA_ISOLEMENT_RECENT;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenFiscaFamille {

    @JsonProperty(PARENTS)
    private List<String> parents;
    @JsonProperty(ENFANTS)
    private List<String> enfants;
    @JsonProperty(ASF)
    private OpenFiscaPeriodes allocationSoutienFamilial;
    @JsonProperty(AF)
    private OpenFiscaPeriodes allocationsFamiliales;
    @JsonProperty(CF)
    private OpenFiscaPeriodes complementFamilial;
    @JsonProperty(PRESTATION_ACCUEIL_JEUNE_ENFANT)
    private OpenFiscaPeriodes prestationAccueilJeuneEnfant;
    @JsonProperty(RSA)
    private OpenFiscaPeriodes revenuSolidariteActive;
    @JsonProperty(RSA_ISOLEMENT_RECENT)
    private OpenFiscaPeriodes rsaIsolementRecent;
    @JsonProperty(PPA)
    private OpenFiscaPeriodes primeActivite;
    @JsonProperty(APL)
    private OpenFiscaPeriodes aidePersonnaliseeLogement;
    @JsonProperty(ALF)
    private OpenFiscaPeriodes allocationLogementFamiliale;
    @JsonProperty(ALS)
    private OpenFiscaPeriodes allocationLogementSociale;
    @JsonProperty(AIDE_LOGEMENT)
    private OpenFiscaPeriodes aideLogement;

    @JsonProperty(AF)
    public OpenFiscaPeriodes getAllocationsFamiliales() {
	return allocationsFamiliales;
    }

    public void setAllocationsFamiliales(OpenFiscaPeriodes allocationsFamiliales) {
	this.allocationsFamiliales = allocationsFamiliales;
    }

    @JsonProperty(AIDE_LOGEMENT)
    public OpenFiscaPeriodes getAideLogement() {
	return aideLogement;
    }

    public void setAideLogement(OpenFiscaPeriodes aideLogement) {
	this.aideLogement = aideLogement;
    }

    @JsonProperty(ALF)
    public OpenFiscaPeriodes getAllocationLogementFamiliale() {
	return allocationLogementFamiliale;
    }

    public void setAllocationLogementFamiliale(OpenFiscaPeriodes allocationLogementFamiliale) {
	this.allocationLogementFamiliale = allocationLogementFamiliale;
    }

    @JsonProperty(ALS)
    public OpenFiscaPeriodes getAllocationLogementSociale() {
	return allocationLogementSociale;
    }

    public void setAllocationLogementSociale(OpenFiscaPeriodes allocationLogementSociale) {
	this.allocationLogementSociale = allocationLogementSociale;
    }

    @JsonProperty(APL)
    public OpenFiscaPeriodes getAidePersonnaliseeLogement() {
	return aidePersonnaliseeLogement;
    }

    public void setAidePersonnaliseeLogement(OpenFiscaPeriodes aidePersonnaliseeLogement) {
	this.aidePersonnaliseeLogement = aidePersonnaliseeLogement;
    }

    @JsonProperty(ASF)
    public OpenFiscaPeriodes getAllocationSoutienFamilial() {
	return allocationSoutienFamilial;
    }

    public void setAllocationSoutienFamilial(OpenFiscaPeriodes allocationSoutienFamilial) {
	this.allocationSoutienFamilial = allocationSoutienFamilial;
    }

    @JsonProperty(CF)
    public OpenFiscaPeriodes getComplementFamilial() {
	return complementFamilial;
    }

    public void setComplementFamilial(OpenFiscaPeriodes complementFamilial) {
	this.complementFamilial = complementFamilial;
    }

    @JsonProperty(ENFANTS)
    public List<String> getEnfants() {
	return enfants;
    }

    public void setEnfants(List<String> enfants) {
	this.enfants = enfants;
    }

    @JsonProperty(PARENTS)
    public List<String> getParents() {
	return parents;
    }

    public void setParents(List<String> parents) {
	this.parents = parents;
    }

    @JsonProperty(PRESTATION_ACCUEIL_JEUNE_ENFANT)
    public OpenFiscaPeriodes getPrestationAccueilJeuneEnfant() {
	return prestationAccueilJeuneEnfant;
    }

    public void setPrestationAccueilJeuneEnfant(OpenFiscaPeriodes prestationAccueilJeuneEnfant) {
	this.prestationAccueilJeuneEnfant = prestationAccueilJeuneEnfant;
    }

    @JsonProperty(PPA)
    public OpenFiscaPeriodes getPrimeActivite() {
	return primeActivite;
    }

    public void setPrimeActivite(OpenFiscaPeriodes primeActivite) {
	this.primeActivite = primeActivite;
    }

    @JsonProperty(RSA)
    public OpenFiscaPeriodes getRevenuSolidariteActive() {
	return revenuSolidariteActive;
    }

    public void setRevenuSolidariteActive(OpenFiscaPeriodes revenuSolidariteActive) {
	this.revenuSolidariteActive = revenuSolidariteActive;
    }

    @JsonProperty(RSA_ISOLEMENT_RECENT)
    public OpenFiscaPeriodes getRsaIsolementRecent() {
	return rsaIsolementRecent;
    }

    public void setRsaIsolementRecent(OpenFiscaPeriodes rsaIsolementRecent) {
	this.rsaIsolementRecent = rsaIsolementRecent;
    }

    @Override
    public String toString() {
	return OpenFiscaObjectMapperService.getJsonStringFromObject(this);
    }

}
