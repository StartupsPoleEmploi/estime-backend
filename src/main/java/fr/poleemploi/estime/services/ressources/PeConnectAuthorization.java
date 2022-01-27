package fr.poleemploi.estime.services.ressources;

import java.time.LocalDateTime;

public class PeConnectAuthorization {

    private String bearerToken;
    private Long expireIn;
    private String idToken;
    private String refreshToken;
    private String scope;
    private String tokenType;
    private LocalDateTime expireInDate;

    public LocalDateTime getExpireInDate() {
	return expireInDate;
    }
    public void setExpireInDate(LocalDateTime expireInDate) {
	this.expireInDate = expireInDate;
    }
    public String getIdToken() {
	return idToken;
    }
    public void setIdToken(String idToken) {
	this.idToken = idToken;
    }
    public String getRefreshToken() {
	return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
	this.refreshToken = refreshToken;
    }
    public String getTokenType() {
	return tokenType;
    }
    public void setTokenType(String tokenType) {
	this.tokenType = tokenType;
    }
    public Long getExpireIn() {
	return expireIn;
    }
    public void setExpireIn(Long expireIn) {
	this.expireIn = expireIn;
    }
    public String getBearerToken() {
	return bearerToken;
    }
    public void setBearerToken(String bearerToken) {
	this.bearerToken = bearerToken;
    }
    public String getScope() {
	return scope;
    }
    public void setScope(String scope) {
	this.scope = scope;
    }
    @Override
    public String toString() {
	return "PeConnectAuthorization [bearerToken=" + bearerToken + ", expireIn=" + expireIn + ", idToken=" + idToken
		+ ", refreshToken=" + refreshToken + ", scope=" + scope + ", tokenType=" + tokenType + ", expireInDate="
		+ expireInDate + "]";
    }
}
