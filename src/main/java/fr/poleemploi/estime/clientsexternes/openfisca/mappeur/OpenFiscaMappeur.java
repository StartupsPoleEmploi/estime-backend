package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CONJOINT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLE1;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.INDIVIDUS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.MENAGE1;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.MENAGES;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONObject;

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

    public JSONObject mapDemandeurEmploiToOpenFiscaPayload(Simulation simulationAides, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject payloadOpenFisca = new JSONObject();
	Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional = situationFamilialeUtile.getPersonnesAChargeAgeInferieurAgeLimite(demandeurEmploi, SimulateurAidesCAF.AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA);
	payloadOpenFisca.put(INDIVIDUS, creerIndividusJSON(simulationAides, demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
	payloadOpenFisca.put(FAMILLES, creerFamillesJSON(demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
	payloadOpenFisca.put(MENAGES, creerMenagesJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
	return payloadOpenFisca;
    }

    private JSONObject creerIndividusJSON(Simulation simulationAides, DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional,
                                          LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject individu = new JSONObject();
	individu.put(DEMANDEUR, openFiscaMappeurIndividu.creerDemandeurJSON(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
	if (personneAChargeAgeInferieureAgeLimiteOptional.isPresent()) {
	    openFiscaMappeurIndividu.ajouterPersonneACharge(individu, personneAChargeAgeInferieureAgeLimiteOptional.get(), dateDebutSimulation, numeroMoisSimule);
	}
	if (situationFamilialeUtile.isEnCouple(demandeurEmploi)) {
	    individu.put(CONJOINT, openFiscaMappeurIndividu.creerConjointJSON(demandeurEmploi.getSituationFamiliale().getConjoint(), dateDebutSimulation, numeroMoisSimule));
	}
	return individu;
    }

    private JSONObject creerFamillesJSON(DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject familles = new JSONObject();
	familles.put(FAMILLE1, openFiscaMappeurFamille.creerFamilleJSON(demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
	return familles;
    }

    private JSONObject creerMenagesJSON(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject menages = new JSONObject();
	menages.put(MENAGE1, openFiscaMappeurMenage.creerMenageJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
	return menages;
    }
}
