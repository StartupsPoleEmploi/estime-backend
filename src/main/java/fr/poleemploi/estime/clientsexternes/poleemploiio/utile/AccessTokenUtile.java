package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AccessTokenPEIOOut;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.StringUtile;
import fr.poleemploi.estime.services.ressources.PeConnectAuthorization;

@Component
public class AccessTokenUtile {

	private static final String GRANT_TYPE_CODE = "authorization_code";
	private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
	private static final String BEARER = "Bearer ";

	@Value("${spring.security.oauth2.client.registration.estime.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.estime.client-secret}")
	private String clientSecret;

	@Autowired
	private DateUtile dateUtile;


	public HttpEntity<MultiValueMap<String, String>>  createAccessTokenByCodeHttpEntity(String code, String redirectURI) {
		MultiValueMap<String, String> accessTokenPEIOIn = createAccessTokenPEIOIn(GRANT_TYPE_CODE, code, redirectURI);		
		return createHttpEntity(accessTokenPEIOIn);
	}

	public HttpEntity<MultiValueMap<String, String>>  createAccessTokenByRefreshTokenHttpEntity(String refreshToken) {
		MultiValueMap<String, String> accessTokenPEIOIn = createAccessTokenPEIOIn(GRANT_TYPE_REFRESH_TOKEN, refreshToken, StringUtile.EMPTY);
		return createHttpEntity(accessTokenPEIOIn);
	}

	public String getBearerToken(String accessToken) {
		return BEARER + accessToken;
	}

	public LocalDateTime getDateExpirationAccessToken(long expireIn) {
		//on estime que le token n'est plus valide 1min. avant sa fin de validité
		long expireInMoins1Minute = expireIn - 60;
		return dateUtile.getDateTimeJour().plusSeconds(expireInMoins1Minute);    	
	}

	public boolean isAccessTokenExpired(LocalDateTime expireInDate) {
		return dateUtile.getDateTimeJour().isAfter(expireInDate);
	}

	public PeConnectAuthorization mapAccessInformationsTokenToPeConnectAuthorization(AccessTokenPEIOOut accessTokenPEIOOut) {
		PeConnectAuthorization peConnectAuthorization = new PeConnectAuthorization();
		peConnectAuthorization.setBearerToken(getBearerToken(accessTokenPEIOOut.getAccessToken()));
		peConnectAuthorization.setExpireIn(accessTokenPEIOOut.getExpiresIn());
		peConnectAuthorization.setIdToken(accessTokenPEIOOut.getIdToken());
		peConnectAuthorization.setRefreshToken(accessTokenPEIOOut.getRefreshToken());
		peConnectAuthorization.setScope(accessTokenPEIOOut.getScope());
		peConnectAuthorization.setTokenType(accessTokenPEIOOut.getTokenType());
		peConnectAuthorization.setExpireInDate(getDateExpirationAccessToken(accessTokenPEIOOut.getExpiresIn()));
		return peConnectAuthorization;
	}

	private HttpEntity<MultiValueMap<String, String>> createHttpEntity(MultiValueMap<String, String> accessTokenPEIOIn) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return new HttpEntity<>(accessTokenPEIOIn, headers);
	}

	private MultiValueMap<String, String> createAccessTokenPEIOIn(String grantType, String codeOrRefreshToken, String redirectURI) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("client_id", clientId);
		map.add("client_secret", clientSecret);
		map.add("grant_type", grantType);

		switch (grantType) {
		case GRANT_TYPE_REFRESH_TOKEN:
			map.add(GRANT_TYPE_REFRESH_TOKEN, codeOrRefreshToken);
			break;

		default:
			map.add("code", codeOrRefreshToken);
			map.add("redirect_uri", redirectURI);
			break;
		}

		return map;
	}	
}
