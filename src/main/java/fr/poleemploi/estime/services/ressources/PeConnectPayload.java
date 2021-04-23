package fr.poleemploi.estime.services.ressources;

public class PeConnectPayload {
    
    private String clientId;
    private String code;
    private String nonce;
    private String redirectURI;
    private String state;
    
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getNonce() {
        return nonce;
    }
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
    public String getRedirectURI() {
        return redirectURI;
    }
    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    
    @Override
    public String toString() {
        return "OIDCAuthorizeData [clientId=" + clientId + ", code=" + code + ", nonce=" + nonce + ", redirectURI="
                + redirectURI + ", state=" + state + "]";
    }
}
