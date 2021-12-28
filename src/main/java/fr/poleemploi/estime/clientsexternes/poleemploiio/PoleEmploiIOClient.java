package fr.poleemploi.estime.clientsexternes.poleemploiio;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.EtatCivilPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi.AgepiPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.aidemobilite.AideMobilitePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.aidemobilite.AideMobilitePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.aidemobilite.CoordonneesPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.utile.PoleEmploiIOUtile;
import fr.poleemploi.estime.clientsexternes.poleemploiio.utile.SimulateurAidesAgepiUtile;
import fr.poleemploi.estime.clientsexternes.poleemploiio.utile.SimulateurAidesAideMobiliteUtile;
import fr.poleemploi.estime.clientsexternes.poleemploiio.utile.SimulateurRepriseActiviteUtile;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.UnauthorizedMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.exceptions.TooManyRequestException;
import fr.poleemploi.estime.services.exceptions.UnauthorizedException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;


@Component
public class PoleEmploiIOClient {

	@Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.token-uri}")
	private String accessTokenURI;

	@Value("${poleemploiio-uri}")
	private String poleemploiioURI;

	@Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.user-info-uri}")
	private String userInfoURI;

	@Autowired
	private PoleEmploiIOUtile poleEmploiIOUtile;

	@Autowired
	private SimulateurAidesAgepiUtile simulateurAidesAgepiUtile;

	@Autowired
	private SimulateurAidesAideMobiliteUtile simulateurAidesAideMobiliteUtile;

	@Autowired
	private SimulateurRepriseActiviteUtile simulateurRepriseActiviteUtile;


	@Autowired
	private RestTemplate restTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(PoleEmploiIOClient.class);

	//Nombre max de tentatives d'appel
	private static final int MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR = 3;

	//Temps d'attente avant de retenter une requête HTTP
	private static final int RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR = 3000; 


	public PeConnectAuthorizationPEIO getPeConnectAuthorizationByCode(String code, String redirectURI, String nonce) {
		HttpEntity<MultiValueMap<String, String>> httpEntity = poleEmploiIOUtile.createAccessTokenByCodeHttpEntity(code, redirectURI);
		PeConnectAuthorizationPEIO peConnectAuthorizationPEIO = callAccessTokenEndPoint(httpEntity);
		if (peConnectAuthorizationPEIO.getNonce().compareTo(nonce) != 0) {
			LOGGER.error(UnauthorizedMessages.ACCES_NON_AUTORISE_NONCE_INCORRECT.getMessage());
			throw new UnauthorizedException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
		} else {
			return peConnectAuthorizationPEIO;              
		}
	}   

	public PeConnectAuthorizationPEIO getPeConnectAuthorizationByRefreshToken(String refreshToken) {
		HttpEntity<MultiValueMap<String, String>> requeteHTTP = poleEmploiIOUtile.createAccessTokenByRefreshTokenHttpEntity(refreshToken);
		return callAccessTokenEndPoint(requeteHTTP);
	}

	public UserInfoPEIO getUserInfo(String bearerToken) {
		try {
			HttpEntity<String> requeteHTTP = poleEmploiIOUtile.createAuthorizationHttpEntity(bearerToken);
			ResponseEntity<UserInfoPEIO> reponse = this.restTemplate.exchange(userInfoURI, HttpMethod.GET, requeteHTTP, UserInfoPEIO.class);
			return reponse.getBody();
		} catch (Exception e) {
			LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), userInfoURI));
			throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
		}
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public DetailIndemnisationPEIO getDetailIndemnisation(String bearerToken) {
		String apiDetailIndemnisationURI = poleemploiioURI + "peconnect-detailindemnisations/v1/indemnisation";
		try {           
			HttpEntity<String> requeteHTTP = poleEmploiIOUtile.createAuthorizationHttpEntity(bearerToken);
			return this.restTemplate.exchange(apiDetailIndemnisationURI, HttpMethod.GET, requeteHTTP, DetailIndemnisationPEIO.class).getBody();
		} catch (Exception exception) {
			if(isTooManyRequestsHttpClientError(exception)) {
				throw new TooManyRequestException(exception.getMessage());
			} else {        
				LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), exception.getMessage(), apiDetailIndemnisationURI));
				throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
			}
		}
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public float getMontantAgepiSimulateurAides(DemandeurEmploi demandeurEmploi) {
		String apiSimulateurAidesAgepiURI = poleemploiioURI + "peconnect-simulateurs-aides/v1/demande-agepi/simuler";
		try {
			//TODO JLA implémenter accessTokenValid
			HttpEntity<AgepiPEIOIn> httpEntity = simulateurAidesAgepiUtile.createHttpEntityAgepiSimulateurAides(demandeurEmploi);
			AgepiPEIOOut agepiPEIOOut = restTemplate.postForEntity(apiSimulateurAidesAgepiURI, httpEntity, AgepiPEIOOut.class).getBody();
			if(agepiPEIOOut != null && agepiPEIOOut.getDecisionAgepiAPI() != null) {
				return agepiPEIOOut.getDecisionAgepiAPI().getMontant();
			} else {
				return 0;
			}
		} catch (Exception exception) {
			if(isTooManyRequestsHttpClientError(exception)) {
				throw new TooManyRequestException(exception.getMessage());
			} else {        
				LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), exception.getMessage(), apiSimulateurAidesAgepiURI));
				throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
			}
		}
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public float getMontantAideMobiliteSimulateurAides(DemandeurEmploi demandeurEmploi) {
		String apiSimulateurAidesAideMobiliteURI = poleemploiioURI + "peconnect-simulateurs-aides/v1/demande-aidemobilite/simuler";
		try {
			//TODO JLA implémenter accessTokenValid
			HttpEntity<AideMobilitePEIOIn> httpEntity = simulateurAidesAideMobiliteUtile.createHttpEntityAgepiSimulateurAides(demandeurEmploi);
			AideMobilitePEIOOut aideMobilitePEIOOut = restTemplate.postForEntity(apiSimulateurAidesAideMobiliteURI, httpEntity, AideMobilitePEIOOut.class).getBody();
			if(aideMobilitePEIOOut != null && aideMobilitePEIOOut.getDecisionAideMobiliteAPI() != null) {
				return aideMobilitePEIOOut.getDecisionAideMobiliteAPI().getMontant();
			} else {
				return 0;
			}
		} catch (Exception exception) {
			LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), exception.getMessage(), apiSimulateurAidesAideMobiliteURI));
			throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
		}
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public Optional<CoordonneesPEIO> getCoordonnees(String bearerToken) {
		String apiCoordonneesURI = poleemploiioURI + "peconnect-coordonnees/v1/coordonnees";
		try {
			HttpEntity<String> httpEntity = poleEmploiIOUtile.createAuthorizationHttpEntity(bearerToken);
			return Optional.of(this.restTemplate.exchange(apiCoordonneesURI, HttpMethod.GET, httpEntity, CoordonneesPEIO.class).getBody());
		} catch (HttpClientErrorException httpClientErrorException) {
			if(isTooManyRequestsHttpClientError(httpClientErrorException)) {
				throw new TooManyRequestException(httpClientErrorException.getMessage());
			} 
		}
		return Optional.empty();
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public Optional<EtatCivilPEIO> getEtatCivil(String bearerToken) {
		try {
			String apiEtatCivilURI = poleemploiioURI + "peconnect-datenaissance/v1/etat-civil";
			HttpEntity<String> httpEntity = poleEmploiIOUtile.createAuthorizationHttpEntity(bearerToken);
			return Optional.of(this.restTemplate.exchange(apiEtatCivilURI, HttpMethod.GET, httpEntity, EtatCivilPEIO.class).getBody());
		} catch (HttpClientErrorException httpClientErrorException) {
			if(isTooManyRequestsHttpClientError(httpClientErrorException)) {
				throw new TooManyRequestException(httpClientErrorException.getMessage());
			} 
		}
		return Optional.empty();
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public Optional<ArePEIOOut> getAreSimulateurRepriseActivite(DemandeurEmploi demandeurEmploi) {        
		String apiSimulateurRepriseActiviteURI = poleemploiioURI + "peconnect-simuler-reprise-activite/v1/simulation-droits/reprise-activite";        
		try {
			HttpEntity<String> requeteHTTP = simulateurRepriseActiviteUtile.createHttpEntityAre(demandeurEmploi);
			return Optional.of(restTemplate.postForEntity(apiSimulateurRepriseActiviteURI, requeteHTTP, ArePEIOOut.class).getBody());
		} catch (Exception exception) {
			if(isTooManyRequestsHttpClientError(exception)) {
				throw new TooManyRequestException(exception.getMessage());
			} else {        
				LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), exception.getMessage(), apiSimulateurRepriseActiviteURI));
				throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
			}
		}
	}

	private PeConnectAuthorizationPEIO callAccessTokenEndPoint(HttpEntity<MultiValueMap<String, String>> httpEntity) {
		try {
			return restTemplate.postForEntity(accessTokenURI, httpEntity, PeConnectAuthorizationPEIO.class).getBody();
		} catch (HttpClientErrorException exception) {
			LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), exception.getMessage(), accessTokenURI));
			throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
		}
	}

	private boolean isAccesTokenExpired(DemandeurEmploi demandeurEmploi) {
		Date currentDateMoinsUneMinute = new Date(System.currentTimeMillis() - (60 * 1000));
		return demandeurEmploi.getPeConnectAuthorization().getExpiryTime().before(currentDateMoinsUneMinute);
	}

	private boolean isTooManyRequestsHttpClientError(Exception exception) {
		return exception instanceof HttpClientErrorException && ((HttpClientErrorException) exception).getRawStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value();
	}
}
