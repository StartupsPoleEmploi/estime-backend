package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;


import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CONJOINT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLE1;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.INDIVIDUS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.MENAGE1;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.MENAGES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PERSONNE_DE_REFERENCE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.STATUT_OCCUPATION_LOGEMENT;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.SimulateurAidesCAF;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.SimulationAides;


@Component
public class OpenFiscaMappeur {

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;
    
    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;
    
    @Autowired
    private OpenFiscaMappeurFamille openFiscaMappeurFamilles;

    @Autowired
    private OpenFiscaMappeurIndividu openFiscaMappeurIndividus;

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;
    
    public JSONObject mapDemandeurEmploiToOpenFiscaPayload(SimulationAides simulationAides, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject payloadOpenFisca = new JSONObject();
        Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional = situationFamilialeUtile.getPersonnesAChargeAgeInferieurAgeLimite(demandeurEmploi, SimulateurAidesCAF.AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA);
        payloadOpenFisca.put(INDIVIDUS, creerIndividusJSON(simulationAides, demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
        payloadOpenFisca.put(FAMILLES, creerFamillesJSON(demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
        if(beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi) ) {
            payloadOpenFisca.put(MENAGES, creerMenagesJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
        }
        return payloadOpenFisca;
    }
    
    private JSONObject creerIndividusJSON(SimulationAides simulationAides, DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject individu = new JSONObject();
        individu.put(DEMANDEUR, openFiscaMappeurIndividus.creerDemandeurJSON(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
        if(personneAChargeAgeInferieureAgeLimiteOptional.isPresent()) {
            openFiscaMappeurIndividus.ajouterPersonneACharge(individu, personneAChargeAgeInferieureAgeLimiteOptional.get(), dateDebutSimulation, numeroMoisSimule);
        }
        if(situationFamilialeUtile.isEnCouple(demandeurEmploi)) {
            individu.put(CONJOINT, openFiscaMappeurIndividus.creerConjointJSON(demandeurEmploi.getSituationFamiliale().getConjoint(), dateDebutSimulation, numeroMoisSimule)); 
        }
        return individu;
    }
    
    private JSONObject creerFamillesJSON(DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeAgeInferieureAgeLimiteOptional, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject familles = new JSONObject();
        familles.put(FAMILLE1, openFiscaMappeurFamilles.creerFamilleJSON(demandeurEmploi, personneAChargeAgeInferieureAgeLimiteOptional, dateDebutSimulation, numeroMoisSimule));
        return familles;
    }
    
    private JSONObject creerMenagesJSON(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject menagesJSON = new JSONObject();
        JSONObject menage1JSON = new JSONObject();
        JSONArray personneReferenceJSON = new JSONArray();
        personneReferenceJSON.put("demandeur");
        menage1JSON.put(PERSONNE_DE_REFERENCE, personneReferenceJSON);
        menage1JSON.put(STATUT_OCCUPATION_LOGEMENT, openFiscaPeriodeMappeur.creerPeriodes(informationsPersonnellesUtile.getStatutOccupationLogement(demandeurEmploi), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
        menagesJSON.put(MENAGE1, menage1JSON);
        return menagesJSON;
    }
}
