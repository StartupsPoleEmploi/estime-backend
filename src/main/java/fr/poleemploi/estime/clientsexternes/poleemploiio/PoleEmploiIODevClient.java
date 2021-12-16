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

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DateNaissanceESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoESD;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.UnauthorizedMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.exceptions.TooManyRequestException;
import fr.poleemploi.estime.services.exceptions.UnauthorizedException;

@Component
public class PoleEmploiIODevClient {

	@Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.token-uri}")
	private String accessTokenURI;

	@Value("${emploi-store-dev.coordonnees-api-uri}")
	private String apiCoordonneesURI;

	@Value("${emploi-store-dev.date-naissance-api-uri}")
	private String apiDateNaissanceURI;

	@Value("${emploi-store-dev.detail-indemnisation-api-uri}")
	private String apiDetailIndemnisationURI;

	@Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.user-info-uri}")
	private String userInfoURI;

	@Autowired
	private PoleEmploiIODevUtile emploiStoreDevUtile;

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger LOGGER = LoggerFactory.getLogger(PoleEmploiIODevClient.class);
	
	//Nombre max de tentatives d'appel
	private static final int MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR = 3;
	//Temps d'attente avant de retenter une requête HTTP
	private static final int RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR = 3000; 

	public PeConnectAuthorizationESD callAccessTokenEndPoint(String code, String redirectURI, String nonce) {
		HttpEntity<MultiValueMap<String, String>> requeteHTTP = emploiStoreDevUtile.getAccesTokenRequeteHTTP(code, redirectURI);
		try {
			ResponseEntity<PeConnectAuthorizationESD> reponse = restTemplate.postForEntity(accessTokenURI, requeteHTTP, PeConnectAuthorizationESD.class);
			if (reponse.getStatusCode().equals(HttpStatus.OK)) {
				PeConnectAuthorizationESD informationsAccessTokenESD = reponse.getBody();
				if (informationsAccessTokenESD.getNonce().compareTo(nonce) != 0) {
					LOGGER.error(UnauthorizedMessages.ACCES_NON_AUTORISE_NONCE_INCORRECT.getMessage());
					throw new UnauthorizedException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
				}
				return informationsAccessTokenESD;
			} else {
				String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), accessTokenURI);
				LOGGER.error(messageError);
				throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
			}
		} catch (HttpClientErrorException e) {
			LOGGER.error(String.format(LoggerMessages.DETAIL_REQUETE_HTTP.getMessage(), e.getMessage(), requeteHTTP.toString()));
			throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
		}
	}

	public Optional<UserInfoESD> callUserInfoEndPoint(String bearerToken) {
		HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
		ResponseEntity<UserInfoESD> reponse = this.restTemplate.exchange(userInfoURI, HttpMethod.GET, requeteHTTP, UserInfoESD.class);
		if (reponse.getStatusCode().equals(HttpStatus.OK)) {
			return Optional.of(reponse.getBody());
		} else {
			String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), userInfoURI);
			LOGGER.error(messageError);
		}
		return Optional.empty();
	}

	public DetailIndemnisationESD callDetailIndemnisationEndPoint(String bearerToken) {
		HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
		ResponseEntity<DetailIndemnisationESD> reponse = this.restTemplate.exchange(apiDetailIndemnisationURI, HttpMethod.GET, requeteHTTP, DetailIndemnisationESD.class);
		if (reponse.getStatusCode().equals(HttpStatus.OK)) {
			return reponse.getBody();
		} else {
			String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), apiDetailIndemnisationURI);
			LOGGER.error(messageError);
			throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
		}
	}

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public Optional<CoordonneesESD> callCoordonneesAPI(String bearerToken) {
		try {
			HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
			ResponseEntity<CoordonneesESD> reponse = this.restTemplate.exchange(apiCoordonneesURI, HttpMethod.GET, requeteHTTP, CoordonneesESD.class);
			return Optional.of(reponse.getBody());

		} catch (HttpClientErrorException e) {
			if(isTooManyRequestsHttpClientError(e)) {
				throw new TooManyRequestException(e.getMessage());
			} 
		}
		return Optional.empty();
	}	

	@Retryable(value = { TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
	public Optional<DateNaissanceESD> callDateNaissanceEndPoint(String bearerToken) {
		ResponseEntity<DateNaissanceESD> reponse = null;
		try {
			HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
			reponse = this.restTemplate.exchange(apiDateNaissanceURI, HttpMethod.GET, requeteHTTP, DateNaissanceESD.class);
			return Optional.of(reponse.getBody());
		} catch (HttpClientErrorException e) {
			if(isTooManyRequestsHttpClientError(e)) {
				throw new TooManyRequestException(e.getMessage());
			} 
		}
		return Optional.empty();
	}
	
	private boolean isTooManyRequestsHttpClientError(Exception e) {
		return e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getRawStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value();
	}
}
