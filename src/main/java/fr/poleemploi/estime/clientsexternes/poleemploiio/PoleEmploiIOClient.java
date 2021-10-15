package fr.poleemploi.estime.clientsexternes.poleemploiio;

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

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DateNaissancePEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.UnauthorizedMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.exceptions.UnauthorizedException;

@Component
public class PoleEmploiIOClient {
    
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
    private PoleEmploiIOUtile emploiStoreUtile;
    
    private String uriAgepi = "https://api.emploi-store.fr/partenaire/peconnect-simulateurs-aides/v1/demande-agepi/simuler";
    private String uriAideMobilite = "https://api.emploi-store.fr/partenaire/peconnect-simulateurs-aides/v1/demande-aidemobilite/simuler";
    
    @Autowired
    private RestTemplate restTemplate;

    
    private static final Logger LOGGER = LoggerFactory.getLogger(PoleEmploiIOClient.class);
    
    
    public PeConnectAuthorizationPEIO callAccessTokenEndPoint(String code, String redirectURI, String nonce) {
        HttpEntity<MultiValueMap<String, String>> requeteHTTP = emploiStoreUtile.getAccesTokenRequeteHTTP(code, redirectURI);
        try {
            ResponseEntity<PeConnectAuthorizationPEIO> reponse = restTemplate.postForEntity(accessTokenURI, requeteHTTP , PeConnectAuthorizationPEIO.class);
            if(reponse.getStatusCode().equals(HttpStatus.OK)) {
                PeConnectAuthorizationPEIO informationsAccessTokenESD = reponse.getBody();
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
    
   public AgepiPEIOOut callAgepiEndPoint(AgepiPEIOIn agepiIn) { 
	   HttpEntity<MultiValueMap<String, Object>> requeteHTTP = emploiStoreUtile.getAgepiRequeteHTTP(agepiIn);
	   try {
		   ResponseEntity<AgepiPEIOOut> reponse = restTemplate.postForEntity(this.uriAgepi, requeteHTTP , AgepiPEIOOut.class);
		   return reponse.getBody();
	   } catch (HttpClientErrorException e) {
		   LOGGER.info(String.format(LoggerMessages.DETAIL_REQUETE_HTTP.getMessage(), e.getMessage(), requeteHTTP.toString()));
           throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
	   }
   }
   
   public AideMobilitePEIOOut callAideMobiliteEndPoint(AideMobilitePEIOIn aideMobiliteIn) {
	   HttpEntity<MultiValueMap<String, Object>> requeteHTTP = emploiStoreUtile.getAideMobiliteRequeteHTTP(aideMobiliteIn);
	   try {
		   ResponseEntity<AideMobilitePEIOOut> reponse = restTemplate.postForEntity(this.uriAideMobilite, requeteHTTP , AideMobilitePEIOOut.class);
		   return reponse.getBody();
	   } catch (HttpClientErrorException e) {
		   LOGGER.info(String.format(LoggerMessages.DETAIL_REQUETE_HTTP.getMessage(), e.getMessage(), requeteHTTP.toString()));
           throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage());
	   }
   }
    
   public Optional<UserInfoPEIO> callUserInfoEndPoint(String bearerToken) {
        HttpEntity<String> requeteHTTP = emploiStoreUtile.getRequeteHTTP(bearerToken);
        ResponseEntity<UserInfoPEIO> reponse = this.restTemplate.exchange(userInfoURI, HttpMethod.GET, requeteHTTP, UserInfoPEIO.class);
        if(reponse.getStatusCode().equals(HttpStatus.OK)) {
            return Optional.of(reponse.getBody());            
        } else {
            String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), userInfoURI);
            LOGGER.error(messageError);
        }
        return Optional.empty();
    }
    
    public DetailIndemnisationPEIO callDetailIndemnisationEndPoint(String bearerToken) {
        HttpEntity<String> requeteHTTP = emploiStoreUtile.getRequeteHTTP(bearerToken);
        ResponseEntity<DetailIndemnisationPEIO> reponse = this.restTemplate.exchange(apiDetailIndemnisationURI, HttpMethod.GET, requeteHTTP, DetailIndemnisationPEIO.class);
        if(reponse.getStatusCode().equals(HttpStatus.OK)) {
            return reponse.getBody();
        } else {
            String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), apiDetailIndemnisationURI);
            LOGGER.error(messageError);
            throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage()); 
        }
    }
    
    public Optional<CoordonneesPEIO> callCoordonneesAPI(String bearerToken) {
        try {
            HttpEntity<String> requeteHTTP = emploiStoreUtile.getRequeteHTTP(bearerToken);
            ResponseEntity<CoordonneesPEIO> reponse = this.restTemplate.exchange(apiCoordonneesURI, HttpMethod.GET, requeteHTTP, CoordonneesPEIO.class);
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
    
    public Optional<DateNaissancePEIO> callDateNaissanceEndPoint(String bearerToken) {
        try {
            HttpEntity<String> requeteHTTP = emploiStoreUtile.getRequeteHTTP(bearerToken);
            ResponseEntity<DateNaissancePEIO> reponse = this.restTemplate.exchange(apiDateNaissanceURI, HttpMethod.GET, requeteHTTP, DateNaissancePEIO.class);
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
