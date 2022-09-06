package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CONJOINT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaFamille;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class OpenFiscaMappeurFamille {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    @Autowired
    private OpenFiscaMappeurRSA openFiscaMappeurRSA;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    public OpenFiscaFamille creerFamilleOpenFisca(DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeInferieur25ansOptional, LocalDate dateDebutSimulation) {
	OpenFiscaFamille openFiscaFamille = new OpenFiscaFamille();
	openFiscaFamille.setParents(creerParentsOpenFisca(demandeurEmploi));
	openFiscaFamille.setEnfants(creerPersonnesAChargeOpenFisca(personneAChargeInferieur25ansOptional));
	openFiscaFamille.setAllocationSoutienFamilial(
		openFiscaPeriodeMappeur.creerPeriodesValeurNulleEgaleZero(ressourcesFinancieresUtile.getAllocationSoutienFamilial(demandeurEmploi), dateDebutSimulation));
	openFiscaFamille.setAllocationsFamiliales(
		openFiscaPeriodeMappeur.creerPeriodesValeurNulleEgaleZero(ressourcesFinancieresUtile.getAllocationsFamiliales(demandeurEmploi), dateDebutSimulation));
	openFiscaFamille.setComplementFamilial(
		openFiscaPeriodeMappeur.creerPeriodesValeurNulleEgaleZero(ressourcesFinancieresUtile.getComplementFamilial(demandeurEmploi), dateDebutSimulation));
	openFiscaFamille.setPrestationAccueilJeuneEnfant(
		openFiscaPeriodeMappeur.creerPeriodesValeurNulleEgaleZero(ressourcesFinancieresUtile.getPrestationAccueilJeuneEnfant(demandeurEmploi), dateDebutSimulation));
	if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	    openFiscaMappeurRSA.addRSAOpenFiscaIndividu(openFiscaFamille, demandeurEmploi, dateDebutSimulation);
	    openFiscaFamille
		    .setRsaIsolementRecent(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(!situationFamilialeUtile.isSeulPlusDe18Mois(demandeurEmploi), dateDebutSimulation));

	}
	openFiscaFamille.setPrimeActivite(openFiscaPeriodeMappeur.creerPeriodesCalculeesOpenFisca(dateDebutSimulation));
	openFiscaFamille.setAidePersonnaliseeLogement(openFiscaPeriodeMappeur.creerPeriodesAPL(demandeurEmploi, dateDebutSimulation));
	openFiscaFamille.setAllocationLogementFamiliale(openFiscaPeriodeMappeur.creerPeriodesALF(demandeurEmploi, dateDebutSimulation));
	openFiscaFamille.setAllocationLogementSociale(openFiscaPeriodeMappeur.creerPeriodesALS(demandeurEmploi, dateDebutSimulation));
	openFiscaFamille.setAideLogement(openFiscaPeriodeMappeur.creerPeriodesCalculeesOpenFisca(dateDebutSimulation));
	return openFiscaFamille;
    }

    private List<String> creerParentsOpenFisca(DemandeurEmploi demandeurEmploi) {
	List<String> parentsArray = new ArrayList<>();
	parentsArray.add(DEMANDEUR);
	if (situationFamilialeUtile.isEnCouple(demandeurEmploi)) {
	    parentsArray.add(CONJOINT);
	}
	return parentsArray;
    }

    private List<String> creerPersonnesAChargeOpenFisca(Optional<List<Personne>> personneAChargeInferieur25ansOptional) {
	List<String> personnesAChargeArray = new ArrayList<>();
	if (personneAChargeInferieur25ansOptional.isPresent()) {
	    for (int i = 1; i <= personneAChargeInferieur25ansOptional.get().size(); i++) {
		personnesAChargeArray.add(ENFANT + i);
	    }
	}
	return personnesAChargeArray;
    }
}
