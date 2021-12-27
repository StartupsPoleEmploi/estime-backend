package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

@Component
public class BearerTokenUtile {

    private static final String BEARER = "Bearer ";
    
    public String getBearerToken(String accessToken) {
        return BEARER + accessToken;
    }
}
