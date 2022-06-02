package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
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
    private OpenFiscaMappeurAgepi openFiscaMappeurAgepi;

    @Autowired
    private OpenFiscaMappeurAideMobilite openFiscaMappeurAideMobilite;

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    public OpenFiscaIndividu creerConjointOpenFisca(Personne conjoint, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaIndividu conjointOpenFisca = new OpenFiscaIndividu();
	openFiscaMappeurRessourcesFinancieres.addRessourcesFinancieresPersonne(conjointOpenFisca, conjoint, dateDebutSimulation, numeroMoisSimule);
	return conjointOpenFisca;
    }

    public OpenFiscaIndividu creerDemandeurOpenFisca(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaIndividu demandeurOpenFisca = new OpenFiscaIndividu();
	demandeurOpenFisca.setDateNaissance(openFiscaMappeurPeriode.creerPeriodesOpenFisca(getDateNaissance(demandeurEmploi), dateDebutSimulation, numeroMoisSimule,
		OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	demandeurOpenFisca.setStatutMarital(openFiscaMappeurPeriode.creerPeriodesOpenFisca(getStatutMarital(demandeurEmploi), dateDebutSimulation, numeroMoisSimule,
		OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));

	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	addRessourcesFinancieresDemandeur(demandeurOpenFisca, demandeurEmploi, simulation, numeroMoisSimule, dateDebutSimulation);
	return demandeurOpenFisca;
    }

    public void ajouterPersonneAChargeIndividus(Map<String, OpenFiscaIndividu> individusOpenFisca, List<Personne> personnesACharge, LocalDate dateJour, int numeroMoisSimule) {
	int index = 1;
	for (Personne personneACharge : personnesACharge) {
	    individusOpenFisca.put(ENFANT + index++, creerEnfantOpenFisca(personneACharge, dateJour, numeroMoisSimule));
	}
    }

    private void addRessourcesFinancieresDemandeur(OpenFiscaIndividu demandeurOpenFisca, DemandeurEmploi demandeurEmploi, Simulation simulation, int numeroMoisSimule, LocalDate dateDebutSimulation) {
	if (ressourcesFinancieresUtile.hasAllocationAdultesHandicapes(demandeurEmploi)) {
	    demandeurOpenFisca.setAllocationAdulteHandicape(openFiscaMappeurPeriode.creerPeriodesOpenFiscaAide(demandeurEmploi, simulation,
		    AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode(), dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasAllocationSolidariteSpecifique(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    demandeurOpenFisca.setAllocationSolidariteSpecifique(openFiscaMappeurPeriode.creerPeriodesOpenFiscaAide(demandeurEmploi, simulation,
		    AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasAllocationARE(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    demandeurOpenFisca.setARE(openFiscaMappeurPeriode.creerPeriodesOpenFiscaARE(demandeurEmploi, simulation, dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasRevenusImmobilier(demandeurEmploi)) {
	    demandeurOpenFisca.setRevenusLocatifs(openFiscaMappeurPeriode.creerPeriodesRevenusImmobilier(ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(demandeurEmploi),
		    dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(demandeurEmploi)) {
	    demandeurOpenFisca.setChiffreAffairesIndependant(openFiscaMappeurPeriode.creerPeriodesAnnees(
		    demandeurEmploi.getRessourcesFinancieresAvantSimulation().getChiffreAffairesIndependantDernierExercice(), dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasRevenusMicroEntreprise(demandeurEmploi)) {
	    demandeurOpenFisca.setBeneficesMicroEntreprise(openFiscaMappeurPeriode.creerPeriodesAnnees(
		    demandeurEmploi.getRessourcesFinancieresAvantSimulation().getBeneficesMicroEntrepriseDernierExercice(), dateDebutSimulation, numeroMoisSimule));
	}
	if (ressourcesFinancieresUtile.hasPensionsAlimentaires(demandeurEmploi)) {
	    demandeurOpenFisca.setPensionsAlimentaires(openFiscaMappeurPeriode.creerPeriodesOpenFisca(
		    demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer(), dateDebutSimulation,
		    numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}
	if (ressourcesFinancieresUtile.hasPensionInvalidite(demandeurEmploi)) {
	    demandeurOpenFisca.setPensionInvalidite(
		    openFiscaMappeurPeriode.creerPeriodesOpenFisca(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getPensionInvalidite(),
			    dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    demandeurOpenFisca.setAllocationSupplementaireInvalidite(openFiscaMappeurPeriode.creerPeriodesValeurNulleEgaleZero(
		    demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getAllocationSupplementaireInvalidite(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}

	openFiscaMappeurAgepi.addAgepiOpenFiscaIndividu(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaMappeurAideMobilite.addAideMobiliteOpenFiscaIndividu(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
    }

    private OpenFiscaIndividu creerEnfantOpenFisca(Personne personneACharge, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaIndividu enfant = new OpenFiscaIndividu();
	enfant.setDateNaissance(openFiscaMappeurPeriode.creerPeriodesOpenFisca(
		dateUtile.convertDateToString(dateUtile.getDateNaissanceModifieeEnfantMoinsDUnAn(personneACharge.getInformationsPersonnelles().getDateNaissance()),
			DateUtile.DATE_FORMAT_YYYY_MM_DD),
		dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	enfant.setEnfantACharge(openFiscaMappeurPeriode.creerPeriodesEnfantACharge(dateDebutSimulation));
	openFiscaMappeurRessourcesFinancieres.addRessourcesFinancieresPersonne(enfant, personneACharge, dateDebutSimulation, numeroMoisSimule);
	return enfant;
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
