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
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurAidesLogement;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPrimeActivite;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurRSA;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
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
    private OpenFiscaMappeurPrimeActivite openFiscaResponseMappeurPrimeActivite;

    @Autowired
    private OpenFiscaMappeurRSA openFiscaResponseMappeurRSA;

    @Autowired
    private OpenFiscaMappeurAidesLogement openFiscaResponseMappeurAidesLogement;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaClient.class);

    public OpenFiscaRetourSimulation calculerPrimeActivite(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaResponseMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerRSA(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantRSA(openFiscaResponseMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogement(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaResponseMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaResponseMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerRsaAvecPrimeActivite(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantRSA(openFiscaResponseMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaResponseMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogementAvecPrimeActivite(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaResponseMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaResponseMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaResponseMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogementAvecRSA(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantRSA(openFiscaResponseMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaResponseMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaResponseMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogementAvecPrimeActiviteEtRSA(Simulation simulation, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	OpenFiscaRoot openFiscaRoot = callApiCalculate(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	openFiscaRetourSimulation.setMontantRSA(openFiscaResponseMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaResponseMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaResponseMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaResponseMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
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
