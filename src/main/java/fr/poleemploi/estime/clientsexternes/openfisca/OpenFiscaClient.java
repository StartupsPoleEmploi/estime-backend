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
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurAideMobilite;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurAidesLogement;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurComplementARE;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPrimeActivite;
import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurRSA;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.SituationAppelOpenFiscaEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

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
    private OpenFiscaMappeurAideMobilite openFiscaMappeurAideMobilite;

    @Autowired
    private OpenFiscaMappeurComplementARE openFiscaMappeurComplementARE;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaClient.class);

    public OpenFiscaRetourSimulation calculerAidesSelonSituation(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule, SituationAppelOpenFiscaEnum situation) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	switch (situation) {
	case AGEPI_AM:
	    openFiscaRetourSimulation = calculerAgepiEtAideMobilite(openFiscaRoot, dateDebutSimulation);
	    break;
	case PPA:
	    openFiscaRetourSimulation = calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule);
	    break;
	case RSA:
	    openFiscaRetourSimulation = calculerRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule);
	    break;
	case AL:
	    openFiscaRetourSimulation = calculerAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule);
	    break;
	case ARE:
	    openFiscaRetourSimulation = calculerComplementARE(openFiscaRoot, dateDebutSimulation, numeroMoisSimule);
	    break;
	default:
	    break;
	}
	return openFiscaRetourSimulation;

    }

    public OpenFiscaRetourSimulation calculerAgepi(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	openFiscaRetourSimulation.setMontantAgepi(openFiscaMappeurAgepi.getMontantAgepi(openFiscaRoot, dateDebutSimulation, 1));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideMobilite(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	openFiscaRetourSimulation.setMontantAideMobilite(openFiscaMappeurAideMobilite.getMontantAideMobilite(openFiscaRoot, dateDebutSimulation, 1));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAgepiEtAideMobilite(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	openFiscaRetourSimulation.setMontantAgepi(openFiscaMappeurAgepi.getMontantAgepi(openFiscaRoot, dateDebutSimulation, 1));
	openFiscaRetourSimulation.setMontantAideMobilite(openFiscaMappeurAideMobilite.getMontantAideMobilite(openFiscaRoot, dateDebutSimulation, 1));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerPrimeActivite(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	openFiscaRetourSimulation.setMontantPrimeActivite(openFiscaMappeurPrimeActivite.getMontantPrimeActivite(openFiscaRoot, dateDebutSimulation, numeroMoisSimule - 1));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerRSA(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	openFiscaRetourSimulation.setMontantRSA(openFiscaMappeurRSA.getMontantRSA(openFiscaRoot, dateDebutSimulation, numeroMoisSimule - 1));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerAideLogement(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	openFiscaRetourSimulation.setMontantAideLogement(openFiscaMappeurAidesLogement.getMontantAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule - 1));
	openFiscaRetourSimulation.setTypeAideLogement(openFiscaMappeurAidesLogement.getTypeAideLogement(openFiscaRoot, dateDebutSimulation, numeroMoisSimule - 1));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRetourSimulation calculerComplementARE(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = new OpenFiscaRetourSimulation();
	openFiscaRetourSimulation.setMontantComplementARE(openFiscaMappeurComplementARE.getMontantComplementARE(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation.setNombreJoursRestantsARE(openFiscaMappeurComplementARE.getNombreJoursRestantsARE(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	openFiscaRetourSimulation
		.setNombreJoursIndemnisesComplementARE(openFiscaMappeurComplementARE.getNombreJoursIndemnisesComplementARE(openFiscaRoot, dateDebutSimulation, numeroMoisSimule));
	return openFiscaRetourSimulation;
    }

    public OpenFiscaRoot callApiCalculate(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	try {
	    OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);
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
