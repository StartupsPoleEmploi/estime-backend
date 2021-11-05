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
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;
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

    @Value("${emploi-store-io.coordonnees-api-uri}")
    private String apiCoordonneesURI;

    @Value("${emploi-store-io.date-naissance-api-uri}")
    private String apiDateNaissanceURI;

    @Value("${emploi-store-io.detail-indemnisation-api-uri}")
    private String apiDetailIndemnisationURI;
    
    @Value("${spring.security.oauth2.client.provider.oauth-pole-emploi.user-info-uri}")
    private String userInfoURI;
    
    @Autowired
    private PoleEmploiIOUtile emploiStoreUtile;
    
    @Value("${emploi-store-io.agepi-api-uri}")
    private String apiAgepiURI;
    
    @Value("${emploi-store-io.aide-mobilite-api-uri}")
    private String apiAideMobiliteURI;
    
    @Value("${emploi-store-io.are-api-uri}")
    private String apiAreURI;
    
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
    
   public Optional<ArePEIOOut> callAreEndPoint(ArePEIOIn areIn, String bearerToken) {
	   HttpEntity<MultiValueMap<String, Object>> requeteHTTP = emploiStoreUtile.getAreRequeteHTTP(areIn, bearerToken);
	   try {
		   ResponseEntity<ArePEIOOut> reponse = restTemplate.postForEntity(apiAreURI, requeteHTTP , ArePEIOOut.class);
		   return Optional.of(reponse.getBody());
	   } catch (Exception e) {
           String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), apiAreURI);
           LOGGER.error(messageError);
       }
	   return Optional.empty();
   }
    
   public Optional<AgepiPEIOOut> callAgepiEndPoint(AgepiPEIOIn agepiIn, String bearerToken) { 
	   HttpEntity<MultiValueMap<String, Object>> requeteHTTP = emploiStoreUtile.getAgepiRequeteHTTP(agepiIn, bearerToken);
	   try {
		   ResponseEntity<AgepiPEIOOut> reponse = restTemplate.postForEntity(apiAgepiURI, requeteHTTP , AgepiPEIOOut.class);
		   return Optional.of(reponse.getBody());
	   } catch (Exception e) {
           String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), apiAgepiURI);
           LOGGER.error(messageError);
       }
	   return Optional.empty();
   }
   
   public Optional<AideMobilitePEIOOut> callAideMobiliteEndPoint(AideMobilitePEIOIn aideMobiliteIn, String bearerToken) {
	   HttpEntity<MultiValueMap<String, Object>> requeteHTTP = emploiStoreUtile.getAideMobiliteRequeteHTTP(aideMobiliteIn, bearerToken);
	   try {
		   ResponseEntity<AideMobilitePEIOOut> reponse = restTemplate.postForEntity(apiAideMobiliteURI, requeteHTTP , AideMobilitePEIOOut.class);
		   return Optional.of(reponse.getBody());
	   } catch (Exception e) {
           String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), apiAideMobiliteURI);
           LOGGER.error(messageError);
       }
	   return Optional.empty();
   }
    
   public Optional<UserInfoPEIO> callUserInfoEndPoint(String bearerToken) {
	   try { 
		   HttpEntity<String> requeteHTTP = emploiStoreUtile.getRequeteHTTP(bearerToken);
		   ResponseEntity<UserInfoPEIO> reponse = this.restTemplate.exchange(userInfoURI, HttpMethod.GET, requeteHTTP, UserInfoPEIO.class);
		   if(reponse.getStatusCode().equals(HttpStatus.OK)) {
			   return Optional.of(reponse.getBody());            
		   } else {
			   String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), userInfoURI);
			   LOGGER.error(messageError);
		   }
	   } catch (Exception e) {
		   String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), apiCoordonneesURI);
		   LOGGER.error(messageError);
	   }
	   return Optional.empty();
   }

   public Optional<DetailIndemnisationPEIO> callDetailIndemnisationEndPoint(String bearerToken) {
	   try {
		   HttpEntity<String> requeteHTTP = emploiStoreUtile.getRequeteHTTP(bearerToken);
		   ResponseEntity<DetailIndemnisationPEIO> reponse = this.restTemplate.exchange(apiDetailIndemnisationURI, HttpMethod.GET, requeteHTTP, DetailIndemnisationPEIO.class);
		   if(reponse.getStatusCode().equals(HttpStatus.OK)) {
			   return Optional.of(reponse.getBody());
		   } else {
			   String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), reponse.getStatusCode(), apiDetailIndemnisationURI);
			   LOGGER.error(messageError);
			   throw new InternalServerException(InternalServerMessages.ACCES_APPLICATION_IMPOSSIBLE.getMessage()); 
		   }
	   } catch (Exception e) {
		   String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), apiCoordonneesURI);
		   LOGGER.error(messageError);
	   }
	   return Optional.empty();
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
