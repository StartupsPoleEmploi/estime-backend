package fr.poleemploi.estime.clientsexternes.poleemploiio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOIn;

@Component
public class PoleEmploiIOUtile {
    
    @Value("${spring.security.oauth2.client.registration.estime.authorization-grant-type}")
    private String authorizationGrantType;
    
    @Value("${spring.security.oauth2.client.registration.estime.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.estime.client-secret}")
    private String clientSecret;
    
    public HttpEntity<MultiValueMap<String, String>> getAccesTokenRequeteHTTP(String code, String redirectURI) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("redirect_uri", redirectURI);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", authorizationGrantType);
        
        return new HttpEntity<>(map, headers);
    }
    
    public HttpEntity<String> getRequeteHTTP(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearerToken);
        return new HttpEntity<>(headers);
    }

	public HttpEntity<MultiValueMap<String, Object>> getAgepiRequeteHTTP(AgepiPEIOIn agepiIn, String bearerToken) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", bearerToken);
        
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("origine", agepiIn.getOrigine());
        map.add("dateDepot", agepiIn.getDateDepot());
        map.add("dateActionReclassement", agepiIn.getDateActionReclassement());
        map.add("contexte", agepiIn.getContexte());
        map.add("natureContratTravail", agepiIn.getNatureContratTravail());
        map.add("lieuFormationOuEmploi", agepiIn.getLieuFormationOuEmploi());
        map.add("typeIntensite", agepiIn.getTypeIntensite());
        map.add("intensite", agepiIn.getIntensite());
        map.add("dureePeriodeEmploiOuFormation", agepiIn.getDureePeriodeEmploiOuFormation());
        map.add("nombreEnfants", agepiIn.getNombreEnfants());
        map.add("nombreEnfantsMoins10Ans", agepiIn.getNombreEnfantsMoins10Ans());
        map.add("eleveSeulEnfants", agepiIn.getEleveSeulEnfants());
        
        
        return new HttpEntity<>(map, headers);
	}

	public HttpEntity<MultiValueMap<String, Object>> getAideMobiliteRequeteHTTP(AideMobilitePEIOIn aideMobiliteIn, String bearerToken) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", bearerToken);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("origine", aideMobiliteIn.getOrigine());
        map.add("dateDepot", aideMobiliteIn.getDateDepot());
        map.add("dateActionReclassement", aideMobiliteIn.getDateActionReclassement());
        map.add("contexte", aideMobiliteIn.getContexte());
        map.add("natureContratTravail", aideMobiliteIn.getNatureContratTravail());
        map.add("lieuActionReclassement", aideMobiliteIn.getLieuActionReclassement());
        map.add("codePostalActionReclassement", aideMobiliteIn.getCodePostalActionReclassement());
        map.add("communeActionReclassement", aideMobiliteIn.getCommuneActionReclassement());
        map.add("dureePeriodeEmploiOuFormation", aideMobiliteIn.getDureePeriodeEmploiOuFormation());
        map.add("distanceDomicileActionReclassement", aideMobiliteIn.getDistanceDomicileActionReclassement());
        map.add("dureePeriodeEmploiOuFormation", aideMobiliteIn.getDureePeriodeEmploiOuFormation());
        map.add("financementPEFormation", aideMobiliteIn.isFinancementPEFormation());
        map.add("nombreAllersRetours", aideMobiliteIn.getNombreAllersRetours());
        map.add("nombreRepas", aideMobiliteIn.getNombreRepas());
        map.add("nombreNuitees", aideMobiliteIn.getNombreNuitees());
                
        return new HttpEntity<>(map, headers);
	}

	public HttpEntity<MultiValueMap<String, Object>> getAreRequeteHTTP(ArePEIOIn areIn, String bearerToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", bearerToken);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("salaireBrutJournalier", areIn.getAllocationBruteJournaliere());
        map.add("allocationBruteJournaliere", areIn.getGainBrut());
        map.add("gainBrut", areIn.getSalaireBrutJournalier());

        return new HttpEntity<>(map, headers);
	}
}
