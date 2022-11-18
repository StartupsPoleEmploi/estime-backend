package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.commun.enumerations.StatutMaritalEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

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
    private OpenFiscaMappeurComplementARE openFiscaMappeurComplementARE;

    @Autowired
    private OpenFiscaMappeurASS openFiscaMappeurASS;

    @Autowired
    private OpenFiscaMappeurAAH openFiscaMappeurAAH;

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    public OpenFiscaIndividu creerConjointOpenFisca(Personne conjoint, LocalDate dateDebutSimulation) {
	OpenFiscaIndividu conjointOpenFisca = new OpenFiscaIndividu();
	openFiscaMappeurRessourcesFinancieres.addRessourcesFinancieresPersonne(conjointOpenFisca, conjoint, dateDebutSimulation);
	return conjointOpenFisca;
    }

    public OpenFiscaIndividu creerDemandeurOpenFisca(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	OpenFiscaIndividu demandeurOpenFisca = new OpenFiscaIndividu();
	demandeurOpenFisca.setDateNaissance(openFiscaMappeurPeriode.creerPeriodesOpenFisca(getDateNaissance(demandeurEmploi), dateDebutSimulation));
	demandeurOpenFisca.setStatutMarital(openFiscaMappeurPeriode.creerPeriodesOpenFisca(getStatutMarital(demandeurEmploi), dateDebutSimulation));
	demandeurOpenFisca.setActivite(openFiscaMappeurPeriode.creerPeriodesActiviteOpenFisca(dateDebutSimulation));

	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
	addRessourcesFinancieresDemandeur(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
	return demandeurOpenFisca;
    }

    public OpenFiscaIndividu creerDemandeurOpenFiscaComplementARE(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	OpenFiscaIndividu demandeurOpenFisca = new OpenFiscaIndividu();
	demandeurOpenFisca.setActivite(openFiscaMappeurPeriode.creerPeriodesActiviteOpenFisca(dateDebutSimulation));

	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
	openFiscaMappeurComplementARE.addComplementAREOpenFiscaIndividuParcoursComplementARE(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);

	return demandeurOpenFisca;
    }

    public void ajouterPersonneAChargeIndividus(Map<String, OpenFiscaIndividu> individusOpenFisca, List<Personne> personnesACharge, LocalDate dateJour) {
	int index = 1;
	for (Personne personneACharge : personnesACharge) {
	    individusOpenFisca.put(ENFANT + index++, creerEnfantOpenFisca(personneACharge, dateJour));
	}
    }

    private void addRessourcesFinancieresDemandeur(OpenFiscaIndividu demandeurOpenFisca, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	if (ressourcesFinancieresUtile.hasRevenusImmobilier(demandeurEmploi)) {
	    demandeurOpenFisca.setRevenusLocatifs(
		    openFiscaMappeurPeriode.creerPeriodesRevenusImmobilier(ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(demandeurEmploi), dateDebutSimulation));
	}
	if (ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(demandeurEmploi)) {
	    demandeurOpenFisca.setChiffreAffairesIndependant(openFiscaMappeurPeriode
		    .creerPeriodesAnnees(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getChiffreAffairesIndependantDernierExercice(), dateDebutSimulation));
	}
	if (ressourcesFinancieresUtile.hasRevenusMicroEntreprise(demandeurEmploi)) {
	    demandeurOpenFisca.setBeneficesMicroEntreprise(openFiscaMappeurPeriode
		    .creerPeriodesAnnees(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getBeneficesMicroEntrepriseDernierExercice(), dateDebutSimulation));
	}
	if (ressourcesFinancieresUtile.hasPensionsAlimentaires(demandeurEmploi)) {
	    demandeurOpenFisca.setPensionsAlimentaires(openFiscaMappeurPeriode.creerPeriodesOpenFisca(
		    demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer(), dateDebutSimulation));
	}
	if (ressourcesFinancieresUtile.hasPensionInvalidite(demandeurEmploi)) {
	    demandeurOpenFisca.setPensionInvalidite(openFiscaMappeurPeriode
		    .creerPeriodesOpenFisca(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCPAM().getPensionInvalidite(), dateDebutSimulation));
	}
	if (ressourcesFinancieresUtile.hasAllocationARE(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    openFiscaMappeurComplementARE.addAREAvantSimulationOpenFiscaIndividu(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
	    openFiscaMappeurComplementARE.addComplementAREOpenFiscaIndividu(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
	}
	if (ressourcesFinancieresUtile.hasAllocationSolidariteSpecifique(demandeurEmploi.getRessourcesFinancieresAvantSimulation())) {
	    openFiscaMappeurASS.addASSOpenFiscaIndividu(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
	}
	if (ressourcesFinancieresUtile.hasAllocationAdultesHandicapes(demandeurEmploi)) {
	    openFiscaMappeurAAH.addAAHOpenFiscaIndividu(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
	}

	openFiscaMappeurAgepi.addAgepiOpenFiscaIndividu(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
	openFiscaMappeurAideMobilite.addAideMobiliteOpenFiscaIndividu(demandeurOpenFisca, demandeurEmploi, dateDebutSimulation);
    }

    private OpenFiscaIndividu creerEnfantOpenFisca(Personne personneACharge, LocalDate dateDebutSimulation) {
	OpenFiscaIndividu enfant = new OpenFiscaIndividu();
	enfant.setDateNaissance(openFiscaMappeurPeriode.creerPeriodesOpenFisca(
		dateUtile.convertDateToString(dateUtile.getDateNaissanceModifieeEnfantMoinsDUnAn(personneACharge.getInformationsPersonnelles().getDateNaissance()),
			DateUtile.DATE_FORMAT_YYYY_MM_DD),
		dateDebutSimulation));
	enfant.setEnfantACharge(openFiscaMappeurPeriode.creerPeriodesEnfantACharge(dateDebutSimulation));
	openFiscaMappeurRessourcesFinancieres.addRessourcesFinancieresPersonne(enfant, personneACharge, dateDebutSimulation);
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
