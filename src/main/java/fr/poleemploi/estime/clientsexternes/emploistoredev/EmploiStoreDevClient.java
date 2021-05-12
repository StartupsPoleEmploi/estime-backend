package fr.poleemploi.estime.clientsexternes.emploistoredev;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.CoordonneesESD;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DateNaissanceESD;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.PeConnectAuthorizationESD;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.UserInfoESD;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.UnauthorizedMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.exceptions.UnauthorizedException;

@Component
public class EmploiStoreDevClient {
    
    @Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.token-uri}")
    private String accessTokenURI;

    @Value("${emploi-store-dev.coordonnees-api-uri}")
    private String apiCoordonneesURI;

    @Value("${emploi-store-dev.date-naissance-api-uri}")
    private String apiDateNaissanceURI;

    @Value("${emploi-store-dev.detail-indemnisation-api-uri}")
    private String apiDetailIndemnisationURI;
    
    @Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.user-info-uri}")
    private String userInfoURI;
    
    @Autowired
    private EmploiStoreDevUtile emploiStoreDevUtile;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EmploiStoreDevClient.class);
    
    
    public PeConnectAuthorizationESD callAccessTokenEndPoint(String code, String redirectURI, String nonce) {
        try {
            HttpEntity<MultiValueMap<String, String>> requeteHTTP = emploiStoreDevUtile.getAccesTokenRequeteHTTP(code, redirectURI);
            //TODO JLA pour test
            LOGGER.info(String.format(LoggerMessages.DETAIL_REQUETE_HTTP.getMessage(), "test", requeteHTTP.toString()));
            ResponseEntity<PeConnectAuthorizationESD> reponse = restTemplate.postForEntity(accessTokenURI, requeteHTTP , PeConnectAuthorizationESD.class);
            if(reponse.getStatusCode().equals(HttpStatus.OK)) {
                PeConnectAuthorizationESD informationsAccessTokenESD = reponse.getBody();
                if(informationsAccessTokenESD.getNonce().compareTo(nonce) != 0) {
                    LOGGER.info(UnauthorizedMessages.ACCES_NON_AUTORISE_NONCE_INCORRECT.getMessage());
                    throw new UnauthorizedException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
                }
                return informationsAccessTokenESD;
            } else {
                String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), accessTokenURI);
                LOGGER.error(messageError);
                throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage()); 
            }
        } catch (HttpClientErrorException e) {
            LOGGER.info(String.format(LoggerMessages.DETAIL_REQUETE_HTTP.getMessage(), e.getMessage(), requeteHTTP.toString()));
            throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
        }        
    }
    
   public Optional<UserInfoESD> callUserInfoEndPoint(String bearerToken) {
        HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
        ResponseEntity<UserInfoESD> reponse = this.restTemplate.exchange(userInfoURI, HttpMethod.GET, requeteHTTP, UserInfoESD.class);
        if(reponse.getStatusCode().equals(HttpStatus.OK)) {
            return Optional.of(reponse.getBody());            
        } else {
            String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), userInfoURI);
            LOGGER.error(messageError);
        }
        return Optional.empty();
    }
    
    public DetailIndemnisationESD callDetailIndemnisationEndPoint(String bearerToken) {
        HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
        ResponseEntity<DetailIndemnisationESD> reponse = this.restTemplate.exchange(apiDetailIndemnisationURI, HttpMethod.GET, requeteHTTP, DetailIndemnisationESD.class);
        if(reponse.getStatusCode().equals(HttpStatus.OK)) {
            return reponse.getBody();
        } else {
            String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), apiDetailIndemnisationURI);
            LOGGER.error(messageError);
            throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage()); 
        }
    }
    
    public Optional<CoordonneesESD> callCoordonneesAPI(String bearerToken) {
        try {
            HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
            ResponseEntity<CoordonneesESD> reponse = this.restTemplate.exchange(apiCoordonneesURI, HttpMethod.GET, requeteHTTP, CoordonneesESD.class);
            if(reponse.getStatusCode().equals(HttpStatus.OK)) {
                return Optional.of(reponse.getBody());            
            } else {
                String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), apiCoordonneesURI);
                LOGGER.error(messageError);
            }
        } catch (Exception e) {
            String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), apiCoordonneesURI);
            LOGGER.error(messageError);
        }
       
        return Optional.empty();
    }
    
    public Optional<DateNaissanceESD> callDateNaissanceEndPoint(String bearerToken) {
        try {
            HttpEntity<String> requeteHTTP = emploiStoreDevUtile.getRequeteHTTP(bearerToken);
            ResponseEntity<DateNaissanceESD> reponse = this.restTemplate.exchange(apiDateNaissanceURI, HttpMethod.GET, requeteHTTP, DateNaissanceESD.class);
            if(reponse.getStatusCode().equals(HttpStatus.OK)) {
                return Optional.of(reponse.getBody());            
            } else {
                String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), apiDateNaissanceURI);
                LOGGER.error(messageError);
            }
        } catch (Exception e) {
            String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), apiDateNaissanceURI);
            LOGGER.error(messageError);
        }
        
        return Optional.empty();
    }
}
