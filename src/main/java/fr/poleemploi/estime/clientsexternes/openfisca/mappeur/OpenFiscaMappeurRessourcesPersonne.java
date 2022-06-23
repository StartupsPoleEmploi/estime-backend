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

    public void addRessourcesFinancieresPersonne(OpenFiscaIndividu personneOpenFisca, Personne personne, LocalDate dateDebutSimulation) {
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = personne.getRessourcesFinancieres();
	if (ressourcesFinancieres != null) {
	    if (ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(ressourcesFinancieres)) {
		openFiscaMappeurPeriode.creerPeriodesSalairePersonne(personneOpenFisca, personne, dateDebutSimulation);
	    } else if (ressourcesFinancieresUtile.hasSalaire(ressourcesFinancieres)) {
		personneOpenFisca.setSalaireDeBase(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getSalaire().getMontantBrut(), dateDebutSimulation));
		personneOpenFisca.setSalaireImposable(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getSalaire().getMontantNet(), dateDebutSimulation));
	    }
	    if (ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(ressourcesFinancieres)) {
		personneOpenFisca.setChiffreAffairesIndependant(
			openFiscaMappeurPeriode.creerPeriodesAnnees(ressourcesFinancieresUtile.getRevenusTravailleurIndependant(ressourcesFinancieres), dateDebutSimulation));
	    }
	    if (ressourcesFinancieresUtile.hasRevenusMicroEntreprise(ressourcesFinancieres)) {
		personneOpenFisca.setBeneficesMicroEntreprise(
			openFiscaMappeurPeriode.creerPeriodesAnnees(ressourcesFinancieresUtile.getRevenusMicroEntreprise(ressourcesFinancieres), dateDebutSimulation));
	    }
	    if (ressourcesFinancieresUtile.hasPensionRetraite(ressourcesFinancieres)) {
		personneOpenFisca.setPensionRetraite(
			openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieresUtile.getPensionRetraite(ressourcesFinancieres), dateDebutSimulation));
	    }
	    if (ressourcesFinancieresUtile.hasRevenusImmobilier(ressourcesFinancieres)) {
		personneOpenFisca.setRevenusLocatifs(openFiscaMappeurPeriode
			.creerPeriodesRevenusImmobilier(ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(ressourcesFinancieres), dateDebutSimulation));
	    }
	    addRessourcesFinancieresCAF(personneOpenFisca, ressourcesFinancieres, dateDebutSimulation);
	    addRessourcesFinancieresPoleEmploi(personneOpenFisca, personne, dateDebutSimulation);
	    addRessourcesFinancieresCPAM(personneOpenFisca, ressourcesFinancieres, dateDebutSimulation);
	}
    }

    private void addRessourcesFinancieresCAF(OpenFiscaIndividu personneOpenFisca, RessourcesFinancieresAvantSimulation ressourcesFinancieres, LocalDate dateDebutSimulation) {
	if (ressourcesFinancieres.getAidesCAF() != null && ressourcesFinancieres.getAidesCAF().getAllocationAAH() != null
		&& ressourcesFinancieres.getAidesCAF().getAllocationAAH() > 0) {
	    personneOpenFisca
		    .setAllocationAdulteHandicape(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getAidesCAF().getAllocationAAH(), dateDebutSimulation));
	}
    }

    private void addRessourcesFinancieresPoleEmploi(OpenFiscaIndividu personneOpenFisca, Personne personne, LocalDate dateDebutSimulation) {
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = personne.getRessourcesFinancieres();
	if (personneUtile.hasAllocationARE(personne)) {
	    personneOpenFisca.setARE(
		    openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getAllocationMensuelleNet(), dateDebutSimulation));
	}
	if (personneUtile.hasAllocationASS(personne)) {
	    personneOpenFisca.setAllocationSolidariteSpecifique(
		    openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationMensuelleNet(), dateDebutSimulation));
	}
    }

    private void addRessourcesFinancieresCPAM(OpenFiscaIndividu personneOpenFisca, RessourcesFinancieresAvantSimulation ressourcesFinancieres, LocalDate dateDebutPeriodeSimulee) {
	if (ressourcesFinancieres.getAidesCPAM() != null && ressourcesFinancieres.getAidesCPAM().getPensionInvalidite() != null
		&& ressourcesFinancieres.getAidesCPAM().getPensionInvalidite() > 0) {
	    personneOpenFisca
		    .setPensionInvalidite(openFiscaMappeurPeriode.creerPeriodesOpenFisca(ressourcesFinancieres.getAidesCPAM().getPensionInvalidite(), dateDebutPeriodeSimulee));
	}
    }
}
