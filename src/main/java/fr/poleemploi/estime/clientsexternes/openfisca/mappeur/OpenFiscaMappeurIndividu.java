package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AAH;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASI;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.BENEFICES_MICRO_ENTREPRISE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHIFFRE_AFFAIRES_INDEPENDANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHOMAGE_NET;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DATE_NAISSANCE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT_A_CHARGE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSIONS_ALIMENTAIRES_PERCUES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSION_INVALIDITE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.REVENUS_LOCATIFS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.STATUT_MARITAL;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.StatutMaritalEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class OpenFiscaMappeurIndividu {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private OpenFiscaMappeurRessourcesPersonne openFiscaMappeurRessourcesFinancieres;

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    public JSONObject creerConjointJSON(Personne conjoint, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject conjointJSON = new JSONObject();
	openFiscaMappeurRessourcesFinancieres.addRessourcesFinancieresPersonne(conjointJSON, conjoint, dateDebutSimulation, numeroMoisSimule);
	return conjointJSON;
    }

    public JSONObject creerDemandeurJSON(Simulation simulationAides, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject demandeurJSON = new JSONObject();
	demandeurJSON.put(DATE_NAISSANCE, openFiscaMappeurPeriode.creerPeriodes(getDateNaissance(demandeurEmploi), dateDebutSimulation, numeroMoisSimule,
		OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	demandeurJSON.put(STATUT_MARITAL, openFiscaMappeurPeriode.creerPeriodes(getStatutMarital(demandeurEmploi), dateDebutSimulation, numeroMoisSimule,
		OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	addRessourcesFinancieresDemandeur(demandeurJSON, demandeurEmploi, simulationAides, numeroMoisSimule, dateDebutSimulation);
	return demandeurJSON;
    }

    public void ajouterPersonneACharge(JSONObject individu, List<Personne> personnesACharge, LocalDate dateJour, int numeroMoisSimule) {
	int index = 1;
	for (Personne personneACharge : personnesACharge) {
	    individu.put(ENFANT + index++, creerEnfantJSON(personneACharge, dateJour, numeroMoisSimule));
	}
    }

    private void addRessourcesFinancieresDemandeur(JSONObject demandeurJSON, DemandeurEmploi demandeurEmploi, Simulation simulationAides, int numeroMoisSimule, LocalDate dateDebutSimulation) {
	if (ressourcesFinancieresUtile.hasAllocationAdultesHandicapes(demandeurEmploi)) {
	    demandeurJSON.put(AAH, openFiscaMappeurPeriode.creerPeriodesAide(demandeurEmploi, simulationAides, AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode(),
		    dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasAllocationSolidariteSpecifique(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    demandeurJSON.put(ASS, openFiscaMappeurPeriode.creerPeriodesAide(demandeurEmploi, simulationAides, AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(),
		    dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasAllocationARE(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    demandeurJSON.put(CHOMAGE_NET,
		    openFiscaMappeurPeriode.creerPeriodesAide(demandeurEmploi, simulationAides, AideEnum.AIDE_RETOUR_EMPLOI.getCode(), dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasRevenusImmobilier(demandeurEmploi)) {
	    demandeurJSON.put(REVENUS_LOCATIFS, openFiscaMappeurPeriode.creerPeriodesRevenusImmobilier(ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(demandeurEmploi),
		    dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(demandeurEmploi)) {
	    demandeurJSON.put(CHIFFRE_AFFAIRES_INDEPENDANT, openFiscaMappeurPeriode
		    .creerPeriodesAnnees(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getChiffreAffairesIndependantDernierExercice(), dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasRevenusMicroEntreprise(demandeurEmploi)) {
	    demandeurJSON.put(BENEFICES_MICRO_ENTREPRISE, openFiscaMappeurPeriode
		    .creerPeriodesAnnees(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getBeneficesMicroEntrepriseDernierExercice(), dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasPensionsAlimentaires(demandeurEmploi)) {
	    demandeurJSON.put(PENSIONS_ALIMENTAIRES_PERCUES,
		    openFiscaMappeurPeriode.creerPeriodes(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer(),
			    dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}
	if (ressourcesFinancieresUtile.hasPensionInvalidite(demandeurEmploi)) {
	    demandeurJSON.put(PENSION_INVALIDITE, openFiscaMappeurPeriode.creerPeriodes(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getPensionInvalidite(),
		    dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    demandeurJSON.put(ASI,
		    openFiscaMappeurPeriode.creerPeriodesValeurNulleEgaleZero(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getAllocationSupplementaireInvalidite(),
			    dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}
    }

    private JSONObject creerEnfantJSON(Personne personneACharge, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject enfant = new JSONObject();
	enfant.put(DATE_NAISSANCE,
		openFiscaMappeurPeriode.creerPeriodes(
			dateUtile.convertDateToString(personneACharge.getInformationsPersonnelles().getDateNaissance(), DateUtile.DATE_FORMAT_YYYY_MM_DD), dateDebutSimulation,
			numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	enfant.put(ENFANT_A_CHARGE, creerEnfantACharge(dateDebutSimulation));
	openFiscaMappeurRessourcesFinancieres.addRessourcesFinancieresPersonne(enfant, personneACharge, dateDebutSimulation, numeroMoisSimule);
	return enfant;
    }

    private JSONObject creerEnfantACharge(LocalDate dateDebutSimulation) {
	JSONObject enfantACharge = new JSONObject();
	enfantACharge.put(String.valueOf(dateDebutSimulation.getYear()), true);
	return enfantACharge;
    }

    private String getStatutMarital(DemandeurEmploi demandeurEmploi) {
	if (demandeurEmploi.getSituationFamiliale().getIsEnCouple().booleanValue()) {
	    return StatutMaritalEnum.MARIE.getLibelle();
	}
	return StatutMaritalEnum.CELIBATAIRE.getLibelle();
    }

    private String getDateNaissance(DemandeurEmploi demandeurEmploi) {
	return dateUtile.convertDateToString(demandeurEmploi.getInformationsPersonnelles().getDateNaissance(), DateUtile.DATE_FORMAT_YYYY_MM_DD);
    }
}
