package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PersonneUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;

@Component
public class OpenFiscaMappeurRessourcesPersonne {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    @Autowired
    private PersonneUtile personneUtile;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    public void addRessourcesFinancieresPersonne(OpenFiscaIndividu personneOpenFisca, Personne personne, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = personne.getRessourcesFinancieres();
	if (ressourcesFinancieres != null) {
	    if (ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(ressourcesFinancieres)) {
		openFiscaMappeurPeriode.creerPeriodesSalairePersonne(personneOpenFisca, personne, dateDebutSimulation, numeroMoisSimule);
	    } else if (ressourcesFinancieresUtile.hasSalaire(ressourcesFinancieres)) {
		personneOpenFisca.setSalaireDeBase(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getSalaire().getMontantBrut(), dateDebutSimulation,
			numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
		personneOpenFisca.setSalaireImposable(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getSalaire().getMontantNet(), dateDebutSimulation,
			numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    }
	    if (ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(ressourcesFinancieres)) {
		personneOpenFisca.setChiffreAffairesIndependant(openFiscaMappeurPeriode
			.creerPeriodesAnnees(ressourcesFinancieresUtile.getRevenusTravailleurIndependant(ressourcesFinancieres), dateDebutSimulation, numeroMoisSimule));
	    }
	    if (ressourcesFinancieresUtile.hasRevenusMicroEntreprise(ressourcesFinancieres)) {
		personneOpenFisca.setBeneficesMicroEntreprise(openFiscaMappeurPeriode
			.creerPeriodesAnnees(ressourcesFinancieresUtile.getRevenusMicroEntreprise(ressourcesFinancieres), dateDebutSimulation, numeroMoisSimule));
	    }
	    if (ressourcesFinancieresUtile.hasPensionRetraite(ressourcesFinancieres)) {
		personneOpenFisca.setPensionRetraite(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieresUtile.getPensionRetraite(ressourcesFinancieres),
			dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    }
	    if (ressourcesFinancieresUtile.hasRevenusImmobilier(ressourcesFinancieres)) {
		personneOpenFisca.setRevenusLocatifs(openFiscaMappeurPeriode
			.creerPeriodesRevenusImmobilier(ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(ressourcesFinancieres), dateDebutSimulation, numeroMoisSimule));
	    }
	    addRessourcesFinancieresCAF(personneOpenFisca, ressourcesFinancieres, dateDebutSimulation, numeroMoisSimule);
	    addRessourcesFinancieresPoleEmploi(personneOpenFisca, personne, dateDebutSimulation, numeroMoisSimule);
	    addRessourcesFinancieresCPAM(personneOpenFisca, ressourcesFinancieres, dateDebutSimulation, numeroMoisSimule);
	}
    }

    private void addRessourcesFinancieresCAF(OpenFiscaIndividu personneOpenFisca, RessourcesFinancieresAvantSimulation ressourcesFinancieres, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	if (ressourcesFinancieres.getAidesCAF() != null && ressourcesFinancieres.getAidesCAF().getAllocationAAH() != null
		&& ressourcesFinancieres.getAidesCAF().getAllocationAAH() > 0) {
	    personneOpenFisca.setAllocationAdulteHandicape(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getAidesCAF().getAllocationAAH(),
		    dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}
    }

    private void addRessourcesFinancieresPoleEmploi(OpenFiscaIndividu personneOpenFisca, Personne personne, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = personne.getRessourcesFinancieres();
	if (personneUtile.hasAllocationARE(personne)) {
	    personneOpenFisca
		    .setChomageNet(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getAllocationMensuelleNet(),
			    dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}
	if (personneUtile.hasAllocationASS(personne)) {
	    personneOpenFisca.setAllocationSolidariteSpecifique(
		    openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationMensuelleNet(), dateDebutSimulation,
			    numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}
    }

    private void addRessourcesFinancieresCPAM(OpenFiscaIndividu personneOpenFisca, RessourcesFinancieresAvantSimulation ressourcesFinancieres, LocalDate dateDebutPeriodeSimulee, int numeroMoisSimule) {
	if (ressourcesFinancieres.getAidesCPAM() != null && ressourcesFinancieres.getAidesCPAM().getPensionInvalidite() != null
		&& ressourcesFinancieres.getAidesCPAM().getPensionInvalidite() > 0) {
	    personneOpenFisca.setPensionInvalidite(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getAidesCPAM().getPensionInvalidite(),
		    dateDebutPeriodeSimulee, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}
    }
}
