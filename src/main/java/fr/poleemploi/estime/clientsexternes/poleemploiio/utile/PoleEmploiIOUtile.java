package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CLIENT_ID;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CLIENT_SECRET;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CODE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.GRANT_TYPE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.REDIRECT_URI;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.REFRESH_TOKEN;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class PoleEmploiIOUtile {


    @Value("${spring.security.oauth2.client.registration.estime.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.estime.client-secret}")
    private String clientSecret;

    public HttpEntity<MultiValueMap<String, String>> createAccessTokenByCodeHttpEntity(String code, String redirectURI) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(CODE, code);
        map.add(REDIRECT_URI, redirectURI);
        map.add(CLIENT_ID, clientId);
        map.add(CLIENT_SECRET, clientSecret);
        map.add(GRANT_TYPE, "authorization_code");
        
        return new HttpEntity<>(map, headers);

    }

    public HttpEntity<MultiValueMap<String, String>> createAccessTokenByRefreshTokenHttpEntity(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(CLIENT_ID, clientId);
        map.add(CLIENT_SECRET, clientSecret);
        map.add(GRANT_TYPE, "refresh_token");
        map.add(REFRESH_TOKEN, refreshToken);

        HttpEntity<MultiValueMap<String, String>> result = new HttpEntity<>(map, headers);

        return result;
    }

    public HttpEntity<String> createAuthorizationHttpEntity(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearerToken);
        return new HttpEntity<>(headers);
    }

   

   
    
    
}
