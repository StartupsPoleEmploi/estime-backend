package fr.poleemploi.estime.configuration.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

public class JwtTokenValidator implements OAuth2TokenValidator<Jwt> {
   
    private String clientId;
    
    private static final String ERROR_CODE = "invalid_token";
    private static final String ERROR_DESCRIPTION = "Incorrect token %s claim value";
    private static final String AZP_CLAIM = "azp";
    
    JwtTokenValidator(String clientId) {
        this.clientId = clientId;
    }
    
    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
         if(isAzpClaimNotEqualsClientId(token)) {
             return generateOAuth2Error(AZP_CLAIM);
         }
         if(isAudClaimNotEqualsClientId(token)) {
             return generateOAuth2Error(JwtClaimNames.AUD);
         }
        return OAuth2TokenValidatorResult.success();
    }
    
    private boolean isAudClaimNotEqualsClientId(Jwt token) {
        return !token.getAudience().contains(clientId);
    }
    
    private boolean isAzpClaimNotEqualsClientId(Jwt token) {
        return !token.getClaim(AZP_CLAIM).toString().contains(clientId);
    }
    
    private OAuth2TokenValidatorResult generateOAuth2Error(String claimName) {
        OAuth2Error error = new OAuth2Error(ERROR_CODE, String.format(ERROR_DESCRIPTION, claimName) , null);
        return OAuth2TokenValidatorResult.failure(error);
    }
}
