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
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    public OpenFiscaFamille creerFamilleOpenFisca(DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeInferieur25ansOptional, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaFamille famille = new OpenFiscaFamille();
	famille.setParents(creerParentsOpenFisca(demandeurEmploi));
	famille.setEnfants(creerPersonnesAChargeOpenFisca(personneAChargeInferieur25ansOptional));
	famille.setAllocationSoutienFamilial(openFiscaPeriodeMappeur.creerPeriodesValeurNulleEgaleZero(ressourcesFinancieresUtile.getAllocationSoutienFamilial(demandeurEmploi),
		dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	famille.setAllocationsFamiliales(openFiscaPeriodeMappeur.creerPeriodesValeurNulleEgaleZero(ressourcesFinancieresUtile.getAllocationsFamiliales(demandeurEmploi),
		dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	famille.setComplementFamilial(openFiscaPeriodeMappeur.creerPeriodesValeurNulleEgaleZero(ressourcesFinancieresUtile.getComplementFamilial(demandeurEmploi),
		dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	famille.setPrestationAccueilJeuneEnfant(
		openFiscaPeriodeMappeur.creerPeriodesValeurNulleEgaleZero(ressourcesFinancieresUtile.getPrestationAccueilJeuneEnfant(demandeurEmploi), dateDebutSimulation,
			numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	    float montantRsaDemandeur = ressourcesFinancieresUtile.getAllocationsRSANet(demandeurEmploi);
	    if (montantRsaDemandeur > 0) {
		famille.setRevenuSolidariteActive(openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAideACalculer(dateDebutSimulation, numeroMoisSimule));
		famille.setRsaIsolementRecent(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(!situationFamilialeUtile.isSeulPlusDe18Mois(demandeurEmploi), dateDebutSimulation,
			numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    }
	}
	famille.setPrimeActivite(openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAideACalculer(dateDebutSimulation, numeroMoisSimule));

	famille.setAidePersonnaliseeLogement(openFiscaPeriodeMappeur.creerPeriodesAllocationsLogement(
		demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement(), dateDebutSimulation, numeroMoisSimule));

	famille.setAllocationLogementFamiliale(openFiscaPeriodeMappeur.creerPeriodesAllocationsLogement(
		demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale(), dateDebutSimulation,
		numeroMoisSimule));

	famille.setAllocationLogementSociale(openFiscaPeriodeMappeur.creerPeriodesAllocationsLogement(
		demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementSociale(), dateDebutSimulation, numeroMoisSimule));
	famille.setAideLogement(openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAideACalculer(dateDebutSimulation, numeroMoisSimule));
	return famille;
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
