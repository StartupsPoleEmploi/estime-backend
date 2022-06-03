package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CONJOINT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLE1;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.MENAGE1;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaFamille;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaMenage;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.SimulateurAidesCAF;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class OpenFiscaMappeur {

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private OpenFiscaMappeurFamille openFiscaMappeurFamille;

    @Autowired
    private OpenFiscaMappeurMenage openFiscaMappeurMenage;

    @Autowired
    private OpenFiscaMappeurIndividu openFiscaMappeurIndividu;

    public OpenFiscaRoot mapDemandeurEmploiToOpenFiscaPayload(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRoot payloadOpenFisca = new OpenFiscaRoot();
	Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional = situationFamilialeUtile.getPersonnesAChargeAgeInferieurAgeLimite(demandeurEmploi,
		SimulateurAidesCAF.AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA);
	payloadOpenFisca.setIndividus(creerIndividusOpenFisca(simulation, demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
	payloadOpenFisca.setFamilles(creerFamillesOpenFisca(demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
	payloadOpenFisca.setMenages(creerMenagesOpenFisca(demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
	return payloadOpenFisca;
    }

    private Map<String, OpenFiscaIndividu> creerIndividusOpenFisca(Simulation simulation, DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	Map<String, OpenFiscaIndividu> individus = new HashMap<>();
	individus.put(DEMANDEUR, openFiscaMappeurIndividu.creerDemandeurOpenFisca(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
	if (personneAChargeAgeInferieureAgeLimiteOptional.isPresent()) {
	    openFiscaMappeurIndividu.ajouterPersonneAChargeIndividus(individus, personneAChargeAgeInferieureAgeLimiteOptional.get(), dateDebutSimulation, numeroMoisSimule);
	}
	if (situationFamilialeUtile.isEnCouple(demandeurEmploi)) {
	    individus.put(CONJOINT, openFiscaMappeurIndividu.creerConjointOpenFisca(demandeurEmploi.getSituationFamiliale().getConjoint(), dateDebutSimulation, numeroMoisSimule));
	}
	return individus;
    }

    private Map<String, OpenFiscaFamille> creerFamillesOpenFisca(DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	Map<String, OpenFiscaFamille> familles = new HashMap<>();
	familles.put(FAMILLE1,
		openFiscaMappeurFamille.creerFamilleOpenFisca(demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
	return familles;
    }

    private Map<String, OpenFiscaMenage> creerMenagesOpenFisca(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	Map<String, OpenFiscaMenage> menages = new HashMap<>();
	menages.put(MENAGE1, openFiscaMappeurMenage.creerMenageOpenFisca(demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
	return menages;
    }
}
