package fr.poleemploi.estime.clientsexternes.poleemploiio;

import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.ALLOCATION_BRUTE_JOURNALIERE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CLIENT_ID;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CLIENT_SECRET;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CODE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CODE_TERRITOIRE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CONTEXTE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.DATE_ACTION_RECLASSEMENT;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.DATE_DEPOT;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.DISTANCE_DOMICILE_ACTION_RECLASSEMENT;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.DUREE_PERIODE_EMPLOI_OU_FORMATION;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.ELEVE_SEUL_ENFANTS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.FRAIS_PRIS_EN_CHARGE_PAR_TIERS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.GAIN_BRUT;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.GRANT_TYPE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.INTENSITE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.LIEU_FORMATION_OU_EMPLOI;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NATURE_CONTRAT_TRAVAIL;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NOMBRE_ALLERS_RETOURS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NOMBRE_ENFANTS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NOMBRE_ENFANTS_MOINS_10_ANS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NOMBRE_NUITEES;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NOMBRE_REPAS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.ORIGINE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.REDIRECT_URI;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.SALAIRE_BRUT_JOURNALIER;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.TYPE_INTENSITE;

import java.util.HashMap;
import java.util.Map;

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
	map.add(CODE, code);
	map.add(REDIRECT_URI, redirectURI);
	map.add(CLIENT_ID, clientId);
	map.add(CLIENT_SECRET, clientSecret);
	map.add(GRANT_TYPE, authorizationGrantType);

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
	map.add(ORIGINE, agepiIn.getOrigine());
	map.add(DATE_DEPOT, agepiIn.getDateDepot());
	map.add(DATE_ACTION_RECLASSEMENT, agepiIn.getDateActionReclassement());
	map.add(CONTEXTE, agepiIn.getContexte());
	map.add(NATURE_CONTRAT_TRAVAIL, agepiIn.getNatureContratTravail());
	map.add(LIEU_FORMATION_OU_EMPLOI, agepiIn.getLieuFormationOuEmploi());
	map.add(TYPE_INTENSITE, agepiIn.getTypeIntensite());
	map.add(INTENSITE, agepiIn.getIntensite());
	map.add(DUREE_PERIODE_EMPLOI_OU_FORMATION, agepiIn.getDureePeriodeEmploiOuFormation());
	map.add(NOMBRE_ENFANTS, agepiIn.getNombreEnfants());
	map.add(NOMBRE_ENFANTS_MOINS_10_ANS, agepiIn.getNombreEnfantsMoins10Ans());
	map.add(ELEVE_SEUL_ENFANTS, agepiIn.getEleveSeulEnfants());

	return new HttpEntity<>(map, headers);
    }

    public HttpEntity<MultiValueMap<String, Object>> getAideMobiliteRequeteHTTP(AideMobilitePEIOIn aideMobiliteIn, String bearerToken) {
	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);
	headers.add("Authorization", bearerToken);

	MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
	map.add(ORIGINE, aideMobiliteIn.getOrigine());
	map.add(DATE_DEPOT, aideMobiliteIn.getDateDepot());
	map.add(DATE_ACTION_RECLASSEMENT, aideMobiliteIn.getDateActionReclassement());
	map.add(CONTEXTE, aideMobiliteIn.getContexte());
	map.add(NATURE_CONTRAT_TRAVAIL, aideMobiliteIn.getNatureContratTravail());
	map.add(DUREE_PERIODE_EMPLOI_OU_FORMATION, aideMobiliteIn.getDureePeriodeEmploiOuFormation());
	map.add(DISTANCE_DOMICILE_ACTION_RECLASSEMENT, aideMobiliteIn.getDistanceDomicileActionReclassement());
	map.add(DUREE_PERIODE_EMPLOI_OU_FORMATION, aideMobiliteIn.getDureePeriodeEmploiOuFormation());
	map.add(NOMBRE_ALLERS_RETOURS, aideMobiliteIn.getNombreAllersRetours());
	map.add(NOMBRE_REPAS, aideMobiliteIn.getNombreRepas());
	map.add(NOMBRE_NUITEES, aideMobiliteIn.getNombreNuitees());
	map.add(LIEU_FORMATION_OU_EMPLOI, aideMobiliteIn.getLieuFormationOuEmploi());
	map.add(FRAIS_PRIS_EN_CHARGE_PAR_TIERS, aideMobiliteIn.isFraisPrisEnChargeParTiers());
	map.add(TYPE_INTENSITE, aideMobiliteIn.getTypeIntensite());
	map.add(INTENSITE, aideMobiliteIn.getIntensite());
	map.add(NOMBRE_ENFANTS, aideMobiliteIn.getNombreEnfants());
	map.add(NOMBRE_ENFANTS_MOINS_10_ANS, aideMobiliteIn.getNombreEnfantsMoins10Ans());
	map.add(ELEVE_SEUL_ENFANTS, aideMobiliteIn.isEleveSeulEnfants());
	map.add(CODE_TERRITOIRE, aideMobiliteIn.getCodeTerritoire());

	return new HttpEntity<>(map, headers);
    }

    public HttpEntity<Map<String, Object>> getAreRequeteHTTP(ArePEIOIn areIn, String bearerToken) {
	HttpHeaders headers = new HttpHeaders();
	headers.add("Authorization", bearerToken);
	headers.setContentType(MediaType.APPLICATION_JSON);
	Map<String, Object> map = new HashMap<String, Object>();
	float allocationBruteJournaliere = areIn.getAllocationBruteJournaliere();
	float gainBrut = areIn.getGainBrut();
	float salaireBrutJournalier = areIn.getSalaireBrutJournalier();

	map.put(ALLOCATION_BRUTE_JOURNALIERE, allocationBruteJournaliere);
	map.put(GAIN_BRUT, gainBrut);
	map.put(SALAIRE_BRUT_JOURNALIER, salaireBrutJournalier);

	return new HttpEntity<>(map, headers);
    }
}
