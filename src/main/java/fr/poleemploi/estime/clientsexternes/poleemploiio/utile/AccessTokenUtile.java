package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

	public HttpEntity<MultiValueMap<String, String>>  createAccessTokenByCodeHttpEntity(String code, String redirectURI) {
		MultiValueMap<String, String> accessTokenPEIOIn = createAccessTokenPEIOIn(GRANT_TYPE_CODE, code, redirectURI);		
		return createHttpEntity(accessTokenPEIOIn);
	}

	public HttpEntity<MultiValueMap<String, String>>  createAccessTokenByRefreshTokenHttpEntity(String refreshToken) {
		MultiValueMap<String, String> accessTokenPEIOIn = createAccessTokenPEIOIn(GRANT_TYPE_REFRESH_TOKEN, refreshToken, StringUtile.EMPTY);
		return createHttpEntity(accessTokenPEIOIn);
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
			map.add("refresh_token", codeOrRefreshToken);
			break;

		default:
			map.add("code", codeOrRefreshToken);
			map.add("redirect_uri", redirectURI);
			break;
		}

		return map;
	}
}
