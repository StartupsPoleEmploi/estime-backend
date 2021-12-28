package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AccessTokenPEIOIn;
import fr.poleemploi.estime.commun.utile.StringUtile;

@Component
public class AccessTokenUtile {

	private static final String GRANT_TYPE_CODE = "authorization_code";
	private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
	private static final String BEARER = "Bearer ";

	@Value("${spring.security.oauth2.client.registration.estime.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.estime.client-secret}")
	private String clientSecret;


	public String getBearerToken(String accessToken) {
		return BEARER + accessToken;
	}

	public HttpEntity<AccessTokenPEIOIn> createAccessTokenByCodeHttpEntity(String code, String redirectURI) {
		AccessTokenPEIOIn accessTokenPEIOIn = createAccessTokenPEIOIn(GRANT_TYPE_CODE, code, redirectURI);		
		return createHttpEntity(accessTokenPEIOIn);
	}

	public HttpEntity<AccessTokenPEIOIn> createAccessTokenByRefreshTokenHttpEntity(String refreshToken) {
		AccessTokenPEIOIn accessTokenPEIOIn = createAccessTokenPEIOIn(GRANT_TYPE_REFRESH_TOKEN, refreshToken, StringUtile.EMPTY);
		return createHttpEntity(accessTokenPEIOIn);
	}

	private HttpEntity<AccessTokenPEIOIn> createHttpEntity(AccessTokenPEIOIn accessTokenPEIOIn) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return new HttpEntity<AccessTokenPEIOIn>(accessTokenPEIOIn, headers);
	}

	private AccessTokenPEIOIn createAccessTokenPEIOIn(String grantType, String codeOrRefreshToken, String redirectURI) {
		AccessTokenPEIOIn accessTokenPEIOIn = new AccessTokenPEIOIn();
		accessTokenPEIOIn.setClientId(clientId);
		accessTokenPEIOIn.setClientSecret(clientSecret);
		accessTokenPEIOIn.setGrantType(grantType);

		switch (grantType) {
		case GRANT_TYPE_REFRESH_TOKEN:
			accessTokenPEIOIn.setRefreshToken(codeOrRefreshToken);
			break;

		default:
			accessTokenPEIOIn.setCode(codeOrRefreshToken);
			accessTokenPEIOIn.setRedirectURI(redirectURI);
			break;
		}

		return accessTokenPEIOIn;
	}
}
