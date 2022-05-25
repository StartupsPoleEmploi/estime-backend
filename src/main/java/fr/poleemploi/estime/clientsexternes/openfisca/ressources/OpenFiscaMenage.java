package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHARGES_LOCATIVES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.COLOC;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEPCOM;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.LOGEMENT_CHAMBRE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.LOGEMENT_CONVENTIONNE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.LOGEMENT_CROUS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.LOYER;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PERSONNE_DE_REFERENCE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.RESIDENCE_MAYOTTE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.STATUT_OCCUPATION_LOGEMENT;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenFiscaMenage {

    @JsonProperty(PERSONNE_DE_REFERENCE)
    private List<String> personneDeReference;
    @JsonProperty(DEPCOM)
    private OpenFiscaPeriodes depcom;
    @JsonProperty(RESIDENCE_MAYOTTE)
    private OpenFiscaPeriodes residenceMayotte;
    @JsonProperty(STATUT_OCCUPATION_LOGEMENT)
    private OpenFiscaPeriodes statutOccupationLogement;
    @JsonProperty(LOYER)
    private OpenFiscaPeriodes loyer;
    @JsonProperty(CHARGES_LOCATIVES)
    private OpenFiscaPeriodes chargesLocatives;
    @JsonProperty(COLOC)
    private OpenFiscaPeriodes coloc;
    @JsonProperty(LOGEMENT_CHAMBRE)
    private OpenFiscaPeriodes logementChambre;
    @JsonProperty(LOGEMENT_CONVENTIONNE)
    private OpenFiscaPeriodes logementConventionne;
    @JsonProperty(LOGEMENT_CROUS)
    private OpenFiscaPeriodes logementCrous;

    @JsonProperty(CHARGES_LOCATIVES)
    public OpenFiscaPeriodes getChargesLocatives() {
	return chargesLocatives;
    }

    public void setChargesLocatives(OpenFiscaPeriodes chargesLocatives) {
	this.chargesLocatives = chargesLocatives;
    }

    @JsonProperty(COLOC)
    public OpenFiscaPeriodes getColoc() {
	return coloc;
    }

    public void setColoc(OpenFiscaPeriodes coloc) {
	this.coloc = coloc;
    }

    @JsonProperty(DEPCOM)
    public OpenFiscaPeriodes getDepcom() {
	return depcom;
    }

    public void setDepcom(OpenFiscaPeriodes depcom) {
	this.depcom = depcom;
    }

    @JsonProperty(LOGEMENT_CHAMBRE)
    public OpenFiscaPeriodes getLogementChambre() {
	return logementChambre;
    }

    public void setLogementChambre(OpenFiscaPeriodes logementChambre) {
	this.logementChambre = logementChambre;
    }

    @JsonProperty(LOGEMENT_CONVENTIONNE)
    public OpenFiscaPeriodes getLogementConventionne() {
	return logementConventionne;
    }

    public void setLogementConventionne(OpenFiscaPeriodes logementConventionne) {
	this.logementConventionne = logementConventionne;
    }

    @JsonProperty(LOGEMENT_CROUS)
    public OpenFiscaPeriodes getLogementCrous() {
	return logementCrous;
    }

    public void setLogementCrous(OpenFiscaPeriodes logementCrous) {
	this.logementCrous = logementCrous;
    }

    @JsonProperty(LOYER)
    public OpenFiscaPeriodes getLoyer() {
	return loyer;
    }

    public void setLoyer(OpenFiscaPeriodes loyer) {
	this.loyer = loyer;
    }

    @JsonProperty(PERSONNE_DE_REFERENCE)
    public List<String> getPersonneDeReference() {
	return personneDeReference;
    }

    public void setPersonneDeReference(List<String> personneDeReference) {
	this.personneDeReference = personneDeReference;
    }

    @JsonProperty(RESIDENCE_MAYOTTE)
    public OpenFiscaPeriodes getResidenceMayotte() {
	return residenceMayotte;
    }

    public void setResidenceMayotte(OpenFiscaPeriodes residenceMayotte) {
	this.residenceMayotte = residenceMayotte;
    }

    @JsonProperty(STATUT_OCCUPATION_LOGEMENT)
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
