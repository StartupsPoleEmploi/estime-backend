package fr.poleemploi.estime.clientsexternes.poleemploiio;

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

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.EtatCivilPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.UnauthorizedMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.exceptions.TooManyRequestException;
import fr.poleemploi.estime.services.exceptions.UnauthorizedException;

@Component
public class PoleEmploiIOClient {

	@Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.token-uri}")
	private String accessTokenURI;

	@Value("${poleemploiio-uri}")
	private String poleemploiioURI;

	@Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.user-info-uri}")
	private String userInfoURI;

	@Autowired
	private PoleEmploiIOUtile emploiStoreDevUtile;

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(PoleEmploiIOClient.class);

	//Nombre max de tentatives d'appel
	private static final int MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR = 3;
	//Temps d'attente avant de retenter une requête HTTP
	private static final int RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR = 3000; 

	public PeConnectAuthorizationPEIO callAccessTokenEndPoint(String code, String redirectURI, String nonce) {
		HttpEntity<MultiValueMap<String, String>> requeteHTTP = emploiStoreDevUtile.getAccesTokenRequeteHTTP(code, redirectURI);
		try {
			ResponseEntity<PeConnectAuthorizationPEIO> reponse = restTemplate.postForEntity(accessTokenURI, requeteHTTP, PeConnectAuthorizationPEIO.class);
			PeConnectAuthorizationPEIO informationsAccessTokenESD = reponse.getBody();
			if (informationsAccessTokenESD.getNonce().compareTo(nonce) != 0) {
				LOGGER.error(UnauthorizedMessages.ACCES_NON_AUTORISE_NONCE_INCORRECT.getMessage());
				throw new UnauthorizedException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
			} else {
				return informationsAccessTokenESD;				
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error(String.format(LoggerMessages.DETAIL_REQUETE_HTTP.getMessage(), e.getMessage(), requeteHTTP.toString()));
			throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
		}
	}

	public UserInfoPEIO callUserInfoEndPoint(String bearerToken) {
		try {
			HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
			ResponseEntity<UserInfoPEIO> reponse = this.restTemplate.exchange(userInfoURI, HttpMethod.GET, requeteHTTP, UserInfoPEIO.class);
			return reponse.getBody();
		} catch (Exception e) {
			String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), userInfoURI);
			LOGGER.error(messageError);
			throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
		}
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public DetailIndemnisationPEIO callDetailIndemnisationEndPoint(String bearerToken) {
		String apiDetailIndemnisationURI = poleemploiioURI + "peconnect-detailindemnisations/v1/indemnisation";
		try {			
			HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
			ResponseEntity<DetailIndemnisationPEIO> reponse = this.restTemplate.exchange(apiDetailIndemnisationURI, HttpMethod.GET, requeteHTTP, DetailIndemnisationPEIO.class);
			return reponse.getBody();
		} catch (Exception exception) {
			if(isTooManyRequestsHttpClientError(exception)) {
				throw new TooManyRequestException(exception.getMessage());
			} else {
				String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), apiDetailIndemnisationURI);
				LOGGER.error(messageError);
				throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
			}
		}
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public Optional<CoordonneesPEIO> callCoordonneesAPI(String bearerToken) {
		String apiCoordonneesURI = poleemploiioURI + "peconnect-coordonnees/v1/coordonnees";
		try {
			HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
			ResponseEntity<CoordonneesPEIO> reponse = this.restTemplate.exchange(apiCoordonneesURI, HttpMethod.GET, requeteHTTP, CoordonneesPEIO.class);
			return Optional.of(reponse.getBody());

		} catch (HttpClientErrorException httpClientErrorException) {
			if(isTooManyRequestsHttpClientError(httpClientErrorException)) {
				throw new TooManyRequestException(httpClientErrorException.getMessage());
			} 
		}
		return Optional.empty();
	}	

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public Optional<EtatCivilPEIO> callEtatCivilEndPoint(String bearerToken) {
		String apiEtatCivilURI = poleemploiioURI + "peconnect-datenaissance/v1/etat-civil";
		ResponseEntity<EtatCivilPEIO> reponse = null;
		try {
			HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
			reponse = this.restTemplate.exchange(apiEtatCivilURI, HttpMethod.GET, requeteHTTP, EtatCivilPEIO.class);
			return Optional.of(reponse.getBody());
		} catch (HttpClientErrorException httpClientErrorException) {
			if(isTooManyRequestsHttpClientError(httpClientErrorException)) {
				throw new TooManyRequestException(httpClientErrorException.getMessage());
			} 
		}
		return Optional.empty();
	}

	private boolean isTooManyRequestsHttpClientError(Exception exception) {
		return exception instanceof HttpClientErrorException && ((HttpClientErrorException) exception).getRawStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value();
	}
}
