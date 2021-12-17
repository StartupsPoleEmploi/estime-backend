package fr.poleemploi.estime.clientsexternes.poleemploiio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class PoleEmploiIOUtile {
    
    @Value("${spring.security.oauth2.client.registration.estime.authorization-grant-type}")
    private String authorizationGrantType;
    
    @Value("${spring.security.oauth2.client.registration.estime.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.estime.client-secret}")
    private String clientSecret;
    
    public HttpEntity<MultiValueMap<String, String>> getAccesTokenRequeteHTTP(String code, String redirectURI) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("redirect_uri", redirectURI);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", authorizationGrantType);
        
        return new HttpEntity<>(map, headers);
    }
    
    public HttpEntity<String> getRequeteHTTP(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearerToken);
        return new HttpEntity<>(headers);
    }
}
