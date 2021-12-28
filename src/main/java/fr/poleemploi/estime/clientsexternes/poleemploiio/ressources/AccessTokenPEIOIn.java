package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

public class AccessTokenPEIOIn {
	private String code;
	private String clientId;
	private String clientSecret;
	private String grantType;
	private String redirectURI;
	private String refreshToken;
	
	
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRedirectURI() {
		return redirectURI;
	}
	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getGrantType() {
		return grantType;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	@Override
	public String toString() {
		return "AccessTokenPEIOIn [code=" + code + ", clientId=" + clientId + ", clientSecret=" + clientSecret
				+ ", grantType=" + grantType + ", redirectURI=" + redirectURI + ", refreshToken=" + refreshToken + "]";
	}
}
