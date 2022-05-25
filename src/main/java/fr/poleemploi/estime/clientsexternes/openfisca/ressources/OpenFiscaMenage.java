package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenFiscaMenage {

    @JsonProperty("personne_de_reference")
    private List<String> personneDeReference;
    @JsonProperty("depcom")
    private OpenFiscaPeriodes depcom;
    @JsonProperty("residence_mayotte")
    private OpenFiscaPeriodes residenceMayotte;
    @JsonProperty("statut_occupation_logement")
    private OpenFiscaPeriodes statutOccupationLogement;
    @JsonProperty("loyer")
    private OpenFiscaPeriodes loyer;
    @JsonProperty("charges_locatives")
    private OpenFiscaPeriodes chargesLocatives;
    @JsonProperty("coloc")
    private OpenFiscaPeriodes coloc;
    @JsonProperty("logement_chambre")
    private OpenFiscaPeriodes logementChambre;
    @JsonProperty("logement_conventionne")
    private OpenFiscaPeriodes logementConventionne;
    @JsonProperty("logement_crous")
    private OpenFiscaPeriodes logementCrous;

    @JsonProperty("charges_locatives")
    public OpenFiscaPeriodes getChargesLocatives() {
	return chargesLocatives;
    }

    public void setChargesLocatives(OpenFiscaPeriodes chargesLocatives) {
	this.chargesLocatives = chargesLocatives;
    }

    @JsonProperty("coloc")
    public OpenFiscaPeriodes getColoc() {
	return coloc;
    }

    public void setColoc(OpenFiscaPeriodes coloc) {
	this.coloc = coloc;
    }

    @JsonProperty("depcom")
    public OpenFiscaPeriodes getDepcom() {
	return depcom;
    }

    public void setDepcom(OpenFiscaPeriodes depcom) {
	this.depcom = depcom;
    }

    @JsonProperty("logement_chambre")
    public OpenFiscaPeriodes getLogementChambre() {
	return logementChambre;
    }

    public void setLogementChambre(OpenFiscaPeriodes logementChambre) {
	this.logementChambre = logementChambre;
    }

    @JsonProperty("logement_conventionne")
    public OpenFiscaPeriodes getLogementConventionne() {
	return logementConventionne;
    }

    public void setLogementConventionne(OpenFiscaPeriodes logementConventionne) {
	this.logementConventionne = logementConventionne;
    }

    @JsonProperty("logement_crous")
    public OpenFiscaPeriodes getLogementCrous() {
	return logementCrous;
    }

    public void setLogementCrous(OpenFiscaPeriodes logementCrous) {
	this.logementCrous = logementCrous;
    }

    @JsonProperty("loyer")
    public OpenFiscaPeriodes getLoyer() {
	return loyer;
    }

    public void setLoyer(OpenFiscaPeriodes loyer) {
	this.loyer = loyer;
    }

    @JsonProperty("personne_de_reference")
    public List<String> getPersonneDeReference() {
	return personneDeReference;
    }

    public void setPersonneDeReference(List<String> personneDeReference) {
	this.personneDeReference = personneDeReference;
    }

    @JsonProperty("residence_mayotte")
    public OpenFiscaPeriodes getResidenceMayotte() {
	return residenceMayotte;
    }

    public void setResidenceMayotte(OpenFiscaPeriodes residenceMayotte) {
	this.residenceMayotte = residenceMayotte;
    }

    @JsonProperty("statut_occupation_logement")
    public OpenFiscaPeriodes getStatutOccupationLogement() {
	return statutOccupationLogement;
    }

    public void setStatutOccupationLogement(OpenFiscaPeriodes statutOccupationLogement) {
	this.statutOccupationLogement = statutOccupationLogement;
    }

    @Override
    public String toString() {
	return OpenFiscaObjectMapperService.getJsonStringFromObject(this);
    }

}
