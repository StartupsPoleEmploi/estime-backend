package fr.poleemploi.estime.clientsexternes.openfisca;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

public class AgepiApiClient {

    @Value("${agepi-api-uri}")
    private String agepiApiURI;
    
    @Autowired
    private RestTemplate restTemplate;
    
	private static final Logger LOGGER = LoggerFactory.getLogger(AgepiApiClient.class);

	public String envoyerDonneesDemandeurEmploi(DemandeurEmploi demandeurEmploi) {
		return callAgepiApi(demandeurEmploi);
	}
	
	public String callAgepiApi(DemandeurEmploi demandeurEmploi){
		try {
			JSONObject utilisateurAgepi = creerJsonDemandeur(demandeurEmploi);
			HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            this.restTemplate = new RestTemplate();

            HttpEntity<String> request = new HttpEntity<>(utilisateurAgepi.toString(), headers);
            return restTemplate.postForObject(agepiApiURI, request, String.class);
		} catch (JSONException e) {
			LOGGER.error( String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
            throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
		}
		
	}

	public JSONObject creerJsonDemandeur(DemandeurEmploi demandeurEmploi) throws JSONException {
		JSONObject utilisateurAgepi = new JSONObject();
        utilisateurAgepi.put("dateDepot", LocalDate.now().withDayOfMonth(1));
        utilisateurAgepi.put("origine", "W");
        utilisateurAgepi.put("idRciChiffre", demandeurEmploi.getIdPoleEmploi());
        utilisateurAgepi.put("contexte", "Reprise");
        utilisateurAgepi.put("dateActionReclassement", LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()));
        utilisateurAgepi.put("LieuFormationOuEmploi", "France");
        utilisateurAgepi.put("intensite", demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine());
        utilisateurAgepi.put("typeIntensite", "Hebdomadaire");
        utilisateurAgepi.put("dureePeriodeEmploiOuFormation", demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois());
        utilisateurAgepi.put("natureContratTravail", demandeurEmploi.getFuturTravail().getTypeContrat());
        
        int nombreEnfants = 0;
        int nombreEnfantsMoinsDixAns = 0;
        for(Personne personneACharge:demandeurEmploi.getSituationFamiliale().getPersonnesACharge()) {
        	LocalDate today = LocalDate.now();
        	int agePersonneACharge = Period.between(personneACharge.getInformationsPersonnelles().getDateNaissance(), today).getYears();
        	if(agePersonneACharge < 10) {
        		++nombreEnfants;
        		++nombreEnfantsMoinsDixAns;
        	}else if(agePersonneACharge < 18) {
        		++nombreEnfants;
        	}
        }
        utilisateurAgepi.put("nombreEnfantsv", nombreEnfants);
        utilisateurAgepi.put("nombreEnfantsMoins10Ans", nombreEnfantsMoinsDixAns);
        utilisateurAgepi.put("eleveSeulEnfants", demandeurEmploi.getSituationFamiliale().getIsEnCouple());
        return utilisateurAgepi;
	}

}
