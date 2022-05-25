package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenFiscaFamille {

    @JsonProperty("parents")
    private List<String> parents;
    @JsonProperty("enfants")
    private List<String> enfants;
    @JsonProperty("asf")
    private OpenFiscaPeriodes allocationSoutienFamilial;
    @JsonProperty("af")
    private OpenFiscaPeriodes allocationsFamiliales;
    @JsonProperty("cf")
    private OpenFiscaPeriodes complementFamilial;
    @JsonProperty("paje_base")
    private OpenFiscaPeriodes prestationAccueilJeuneEnfant;
    @JsonProperty("rsa")
    private OpenFiscaPeriodes revenuSolidariteActive;
    @JsonProperty("rsa_isolement_recent")
    private OpenFiscaPeriodes rsaIsolementRecent;
    @JsonProperty("ppa")
    private OpenFiscaPeriodes primeActivite;
    @JsonProperty("apl")
    private OpenFiscaPeriodes aidePersonnaliseeLogement;
    @JsonProperty("alf")
    private OpenFiscaPeriodes allocationLogementFamiliale;
    @JsonProperty("als")
    private OpenFiscaPeriodes allocationLogementSociale;
    @JsonProperty("aide_logement")
    private OpenFiscaPeriodes aideLogement;

    @JsonProperty("af")
    public OpenFiscaPeriodes getAllocationsFamiliales() {
	return allocationsFamiliales;
    }

    public void setAllocationsFamiliales(OpenFiscaPeriodes allocationsFamiliales) {
	this.allocationsFamiliales = allocationsFamiliales;
    }

    @JsonProperty("aide_logement")
    public OpenFiscaPeriodes getAideLogement() {
	return aideLogement;
    }

    public void setAideLogement(OpenFiscaPeriodes aideLogement) {
	this.aideLogement = aideLogement;
    }

    @JsonProperty("alf")
    public OpenFiscaPeriodes getAllocationLogementFamiliale() {
	return allocationLogementFamiliale;
    }

    public void setAllocationLogementFamiliale(OpenFiscaPeriodes allocationLogementFamiliale) {
	this.allocationLogementFamiliale = allocationLogementFamiliale;
    }

    @JsonProperty("als")
    public OpenFiscaPeriodes getAllocationLogementSociale() {
	return allocationLogementSociale;
    }

    public void setAllocationLogementSociale(OpenFiscaPeriodes allocationLogementSociale) {
	this.allocationLogementSociale = allocationLogementSociale;
    }

    @JsonProperty("apl")
    public OpenFiscaPeriodes getAidePersonnaliseeLogement() {
	return aidePersonnaliseeLogement;
    }

    public void setAidePersonnaliseeLogement(OpenFiscaPeriodes aidePersonnaliseeLogement) {
	this.aidePersonnaliseeLogement = aidePersonnaliseeLogement;
    }

    @JsonProperty("asf")
    public OpenFiscaPeriodes getAllocationSoutienFamilial() {
	return allocationSoutienFamilial;
    }

    public void setAllocationSoutienFamilial(OpenFiscaPeriodes allocationSoutienFamilial) {
	this.allocationSoutienFamilial = allocationSoutienFamilial;
    }

    @JsonProperty("cf")
    public OpenFiscaPeriodes getComplementFamilial() {
	return complementFamilial;
    }

    public void setComplementFamilial(OpenFiscaPeriodes complementFamilial) {
	this.complementFamilial = complementFamilial;
    }

    @JsonProperty("enfants")
    public List<String> getEnfants() {
	return enfants;
    }

    public void setEnfants(List<String> enfants) {
	this.enfants = enfants;
    }

    @JsonProperty("parents")
    public List<String> getParents() {
	return parents;
    }

    public void setParents(List<String> parents) {
	this.parents = parents;
    }

    @JsonProperty("paje_base")
    public OpenFiscaPeriodes getPrestationAccueilJeuneEnfant() {
	return prestationAccueilJeuneEnfant;
    }

    public void setPrestationAccueilJeuneEnfant(OpenFiscaPeriodes prestationAccueilJeuneEnfant) {
	this.prestationAccueilJeuneEnfant = prestationAccueilJeuneEnfant;
    }

    @JsonProperty("ppa")
    public OpenFiscaPeriodes getPrimeActivite() {
	return primeActivite;
    }

    public void setPrimeActivite(OpenFiscaPeriodes primeActivite) {
	this.primeActivite = primeActivite;
    }

    @JsonProperty("rsa")
    public OpenFiscaPeriodes getRevenuSolidariteActive() {
	return revenuSolidariteActive;
    }

    public void setRevenuSolidariteActive(OpenFiscaPeriodes revenuSolidariteActive) {
	this.revenuSolidariteActive = revenuSolidariteActive;
    }

    @JsonProperty("rsa_isolement_recent")
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
