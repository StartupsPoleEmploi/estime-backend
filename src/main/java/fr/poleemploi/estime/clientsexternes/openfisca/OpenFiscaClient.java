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
import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeur;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurAidesLogement;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPrimeActivite;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurRSA;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

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

    public OpenFiscaRetourSimulation calculerPrimeActivite(SimulationAides simulationAides, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
        String jsonResponse = callApiCalculate(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaResponseMappeurPrimeActivite.getMontantPrimeActivite(jsonResponse, dateDebutSimulation, numeroMoisSimule));
        return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerRsaAvecPrimeActivite(SimulationAides simulationAides, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
        String jsonResponse = callApiCalculate(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        openFiscaRetourSimulation.setMontantRSA(openFiscaResponseMappeurRSA.getMontantRSA(jsonResponse, dateDebutSimulation, numeroMoisSimule));
        openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaResponseMappeurPrimeActivite.getMontantPrimeActivite(jsonResponse, dateDebutSimulation, numeroMoisSimule));
        return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogement(SimulationAides simulationAides, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
        String jsonResponse = callApiCalculate(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        openFiscaRetourSimulation.setMontantAideLogement(openFiscaResponseMappeurAidesLogement.getMontantAideLogement(jsonResponse, dateDebutSimulation, numeroMoisSimule));
        openFiscaRetourSimulation.setTypeAideLogement(openFiscaResponseMappeurAidesLogement.getTypeAideLogement(jsonResponse, dateDebutSimulation, numeroMoisSimule));
        return openFiscaRetourSimulation;
    }

    private String callApiCalculate(SimulationAides simulationAides, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        try {
            JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(openFiscaPayload.toString(), headers);
            return restTemplate.postForObject(openFiscaURI, request, String.class);

        } catch (JSONException e) {
            LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
            throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
        }
    }
}
