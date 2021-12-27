package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

@Component
public class AccessTokenUtile {

    private static final String BEARER = "Bearer ";
    
    public String getBearerToken(String accessToken) {
        return BEARER + accessToken;
    }
}
