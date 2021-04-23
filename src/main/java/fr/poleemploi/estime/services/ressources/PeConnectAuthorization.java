package fr.poleemploi.estime.services.ressources;

public class PeConnectAuthorization {

    private String accessToken;
    private Long expireIn;
    private String idToken;
    private String refreshToken;
    private String scope;
    private String tokenType;
    
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
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    @Override
    public String toString() {
        return "DonneesAccessToken [accessToken=" + accessToken + ", expireIn=" + expireIn + ", idToken=" + idToken
                + ", refreshToken=" + refreshToken + ", scope=" + scope + ", tokenType=" + tokenType + "]";
    }
}
