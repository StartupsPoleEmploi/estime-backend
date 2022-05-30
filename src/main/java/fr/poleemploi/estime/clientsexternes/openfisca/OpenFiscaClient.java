package fr.poleemploi.estime.clientsexternes.openfisca;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.github.tsohr.JSONException;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeur;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurAgepi;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurAidesLogement;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPrimeActivite;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurRSA;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.SituationAppelOpenFiscaEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class OpenFiscaClient {

    @Value("${openfisca-api-uri}")
    private String openFiscaURI;

    @Autowired
    private OpenFiscaMappeur openFiscaMappeur;

    @Autowired
    private OpenFiscaMappeurPrimeActivite openFiscaMappeurPrimeActivite;

    @Autowired
    private OpenFiscaMappeurAgepi openFiscaMappeurAgepi;

    @Autowired
    private OpenFiscaMappeurRSA openFiscaMappeurRSA;

    @Autowired
    private OpenFiscaMappeurAidesLogement openFiscaMappeurAidesLogement;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaClient.class);

    public OpenFiscaRetourSimulation calculerAidesSelonSituation(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule, SituationAppelOpenFiscaEnum situation) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	switch (situation) {
	case AGEPI:
	    openFiscaRetourSimulation = calculerAgepi(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    break;
	case PPA:
	    openFiscaRetourSimulation = calculerPrimeActivite(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    break;
	case RSA:
	    openFiscaRetourSimulation = calculerRSA(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    break;
	case AL:
	    openFiscaRetourSimulation = calculerAideLogement(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    break;
	case RSA_PPA:
	    openFiscaRetourSimulation = calculerRsaAvecPrimeActivite(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    break;
	case AL_PPA:
	    openFiscaRetourSimulation = calculerAideLogementAvecPrimeActivite(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    break;
	case AL_RSA:
	    openFiscaRetourSimulation = calculerAideLogementAvecRSA(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    break;
	case AL_PPA_RSA:
	    openFiscaRetourSimulation = calculerAideLogementAvecPrimeActiviteEtRSA(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    break;
	default:
	    break;
	}
	return openFiscaRetourSimulation;

    }

    public OpenFiscaRetourSimulation calculerAgepi(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantAgepi(openFiscaMappeurAgepi.getMontantAgepi(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerPrimeActivite(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerRSA(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantRSA(openFiscaMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogement(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerRsaAvecPrimeActivite(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantRSA(openFiscaMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogementAvecPrimeActivite(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogementAvecRSA(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantRSA(openFiscaMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogementAvecPrimeActiviteEtRSA(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantRSA(openFiscaMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    private OpenFiscaRoot callApiCalculate(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<String> request = new HttpEntity<>(openFiscaPayload.toString(), headers);
	    return restTemplate.postForObject(openFiscaURI, request, OpenFiscaRoot.class);
	} catch (JSONException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }
}
