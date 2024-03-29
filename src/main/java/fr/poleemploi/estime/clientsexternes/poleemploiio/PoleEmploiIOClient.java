package fr.poleemploi.estime.clientsexternes.poleemploiio;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AccessTokenPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.EtatCivilPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.utile.AccessTokenUtile;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.UnauthorizedMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.exceptions.TooManyRequestException;
import fr.poleemploi.estime.services.exceptions.UnauthorizedException;
import fr.poleemploi.estime.services.ressources.PeConnectAuthorization;

@Component
public class PoleEmploiIOClient {

    @Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.token-uri}")
    private String accessTokenURI;

    @Value("${poleemploiio-uri}")
    private String poleemploiioURI;

    @Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.user-info-uri}")
    private String userInfoURI;

    @Autowired
    private AccessTokenUtile accessTokenUtile;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(PoleEmploiIOClient.class);

    //Nombre max de tentatives d'appel
    private static final int MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR = 3;

    //Temps d'attente avant de retenter une requête HTTP
    private static final int RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR = 3000;

    public PeConnectAuthorization getPeConnectAuthorizationByCode(String code, String redirectURI, String nonce) {
	HttpEntity<MultiValueMap<String, String>> httpEntity = accessTokenUtile.createAccessTokenByCodeHttpEntity(code, redirectURI);
	AccessTokenPEIOOut accessTokenPEIOOut = callAccessTokenEndPoint(httpEntity);
	if (accessTokenPEIOOut != null && accessTokenPEIOOut.getNonce().compareTo(nonce) != 0) {
	    LOGGER.error(UnauthorizedMessages.ACCES_NON_AUTORISE_NONCE_INCORRECT.getMessage());
	    throw new UnauthorizedException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
	}
	return accessTokenUtile.mapAccessInformationsTokenToPeConnectAuthorization(accessTokenPEIOOut);
    }

    public PeConnectAuthorization getPeConnectAuthorizationByRefreshToken(String refreshToken) {
	HttpEntity<MultiValueMap<String, String>> httpEntity = accessTokenUtile.createAccessTokenByRefreshTokenHttpEntity(refreshToken);
	return accessTokenUtile.mapAccessInformationsTokenToPeConnectAuthorization(callAccessTokenEndPoint(httpEntity));
    }

    public UserInfoPEIOOut getUserInfo(String bearerToken) {
	try {
	    HttpEntity<String> httpEntity = createAuthorizationHttpEntity(bearerToken);
	    return this.restTemplate.exchange(userInfoURI, HttpMethod.GET, httpEntity, UserInfoPEIOOut.class).getBody();
	} catch (Exception exception) {
	    LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), exception.getMessage(), userInfoURI));
	    throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
	}
    }

    @Retryable(value = {
	    TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
    public DetailIndemnisationPEIOOut getDetailIndemnisation(String bearerToken) {
	String apiDetailIndemnisationURI = poleemploiioURI + "peconnect-detailindemnisations/v1/indemnisation";
	try {
	    HttpEntity<String> httpEntity = createAuthorizationHttpEntity(bearerToken);
	    return this.restTemplate.exchange(apiDetailIndemnisationURI, HttpMethod.GET, httpEntity, DetailIndemnisationPEIOOut.class).getBody();
	} catch (Exception exception) {
	    if (isTooManyRequestsHttpClientError(exception)) {
		throw new TooManyRequestException(exception.getMessage());
	    } else {
		LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), exception.getMessage(), apiDetailIndemnisationURI));
		throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
	    }
	}
    }

    @Retryable(value = {
	    TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
    public Optional<CoordonneesPEIOOut> getCoordonnees(String bearerToken) {
	String apiCoordonneesURI = poleemploiioURI + "peconnect-coordonnees/v1/coordonnees";
	try {
	    HttpEntity<String> httpEntity = createAuthorizationHttpEntity(bearerToken);
	    return Optional.of(this.restTemplate.exchange(apiCoordonneesURI, HttpMethod.GET, httpEntity, CoordonneesPEIOOut.class).getBody());
	} catch (HttpClientErrorException httpClientErrorException) {
	    if (isTooManyRequestsHttpClientError(httpClientErrorException)) {
		throw new TooManyRequestException(httpClientErrorException.getMessage());
	    }
	}
	return Optional.empty();
    }

    @Retryable(value = {
	    TooManyRequestException.class }, maxAttempts = MAX_ATTEMPTS_AFTER_TOO_MANY_REQUEST_HTTP_ERROR, backoff = @Backoff(delay = RETRY_DELAY_AFTER_TOO_MANY_REQUEST_HTTP_ERROR))
    public Optional<EtatCivilPEIOOut> getEtatCivil(String bearerToken) {
	try {
	    String apiEtatCivilURI = poleemploiioURI + "peconnect-datenaissance/v1/etat-civil";
	    HttpEntity<String> httpEntity = createAuthorizationHttpEntity(bearerToken);
	    return Optional.of(this.restTemplate.exchange(apiEtatCivilURI, HttpMethod.GET, httpEntity, EtatCivilPEIOOut.class).getBody());
	} catch (HttpClientErrorException httpClientErrorException) {
	    if (isTooManyRequestsHttpClientError(httpClientErrorException)) {
		throw new TooManyRequestException(httpClientErrorException.getMessage());
	    }
	}
	return Optional.empty();
    }

    private AccessTokenPEIOOut callAccessTokenEndPoint(HttpEntity<MultiValueMap<String, String>> httpEntity) {
	try {
	    return restTemplate.postForEntity(accessTokenURI, httpEntity, AccessTokenPEIOOut.class).getBody();
	} catch (HttpClientErrorException exception) {
	    LOGGER.error(String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), exception.getMessage(), accessTokenURI));
	    throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
	}
    }

    private boolean isTooManyRequestsHttpClientError(Exception exception) {
	return exception instanceof HttpClientErrorException && ((HttpClientErrorException) exception).getRawStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value();
    }

    private HttpEntity<String> createAuthorizationHttpEntity(String bearerToken) {
	HttpHeaders headers = new HttpHeaders();
	headers.add("Authorization", bearerToken);
	return new HttpEntity<>(headers);
    }
}
