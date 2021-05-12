package fr.poleemploi.estime.logique;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.emploistoredev.EmploiStoreDevClient;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.PeConnectAuthorizationESD;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.UserInfoESD;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.utile.AccesTokenUtile;
import fr.poleemploi.estime.commun.utile.IndividuUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeConnectUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.SimulateurAidesSociales;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;

@Component
public class IndividuLogique {

    @Autowired
    private AccesTokenUtile accesTokenUtile;

    @Autowired
    private DemandeurEmploiUtile demandeurEmploiUtile;

    @Autowired
    private EmploiStoreDevClient emploiStoreDevClient;

    @Autowired
    private IndividuUtile individuUtile;

    @Autowired
    private PeConnectUtile peConnectUtile;

    @Autowired
    private SimulateurAidesSociales simulateurAidesSociales;

    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    @Autowired
    private SuiviUtilisateurUtile suiviUtilisateurUtile;


    private static final Logger LOGGER = LoggerFactory.getLogger(IndividuLogique.class);

    public Individu authentifier(String code, String redirectURI, String nonce) {

        Individu individu = new Individu();

        PeConnectAuthorizationESD peConnectAuthorizationESD = emploiStoreDevClient.callAccessTokenEndPoint(code, redirectURI, nonce); 
        String bearerToken = accesTokenUtile.getBearerToken(peConnectAuthorizationESD.getAccessToken());

        DetailIndemnisationESD detailIndemnisationESD = emploiStoreDevClient.callDetailIndemnisationEndPoint(bearerToken);
        Optional<UserInfoESD> userInfoOption = emploiStoreDevClient.callUserInfoEndPoint(bearerToken);

        if(userInfoOption.isPresent()) {
            UserInfoESD userInfoESD = userInfoOption.get();
            if(stagingEnvironnementUtile.isStagingEnvironnement()) {  
                stagingEnvironnementUtile.gererAccesAvecBouchon(individu, userInfoESD);
            } else {            
                individu.setIdPoleEmploi(userInfoESD.getSub());
                individu.setPopulationAutorisee(individuUtile.isPopulationAutorisee(detailIndemnisationESD));
                individuUtile.addInformationsDetailIndemnisationPoleEmploi(individu, detailIndemnisationESD);                 
            } 

            //@TODO JLA : remettre individu.isPopulationAutorisee() à la place de true après expérimentation
            suiviUtilisateurUtile.tracerParcoursUtilisateur(
                    userInfoESD.getSub(), 
                    userInfoESD.getFamilyName(), 
                    userInfoESD.getGivenName(), 
                    userInfoESD.getEmail(), 
                    suiviUtilisateurUtile.getParcoursAccesService(individu), 
                    individu.getBeneficiaireAidesSociales(), 
                    true);            

            individu.setPeConnectAuthorization(peConnectUtile.mapInformationsAccessTokenPeConnect(peConnectAuthorizationESD));

        } else {
            LOGGER.error(LoggerMessages.USER_INFO_KO.getMessage());
            throw new InternalServerException(InternalServerMessages.IDENTIFICATION_IMPOSSIBLE.getMessage());
        }        

        return individu;
    }

    public DemandeurEmploi creerDemandeurEmploi(Individu individu) {

        String accessToken = individu.getPeConnectAuthorization().getAccessToken();
        String bearerToken = accesTokenUtile.getBearerToken(accessToken);

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setIdPoleEmploi(individu.getIdPoleEmploi());
        demandeurEmploiUtile.addCodeDepartement(demandeurEmploi, bearerToken);
        demandeurEmploiUtile.addDateNaissance(demandeurEmploi, bearerToken);

        demandeurEmploi.setBeneficiaireAidesSociales(individu.getBeneficiaireAidesSociales());
        demandeurEmploi.setRessourcesFinancieres(individu.getRessourcesFinancieres());         

        suiviUtilisateurUtile.tracerParcoursUtilisateur(
                demandeurEmploi.getIdPoleEmploi(), 
                null, 
                null, 
                null, 
                ParcoursUtilisateur.SIMULATION_COMMENCEE.getParcours(), 
                individu.getBeneficiaireAidesSociales(), 
                false);     

        return demandeurEmploi;
    } 

    public SimulationAidesSociales simulerMesAidesSociales(DemandeurEmploi demandeurEmploi) { 
        SimulationAidesSociales simulationAidesSociales = simulateurAidesSociales.simuler(demandeurEmploi);
        
        suiviUtilisateurUtile.tracerParcoursUtilisateur(
                demandeurEmploi.getIdPoleEmploi(), 
                null, 
                null, 
                null, 
                ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(), 
                demandeurEmploi.getBeneficiaireAidesSociales(), 
                false);   

        return simulationAidesSociales;
    }

    public void supprimerSuiviParcoursUtilisateur(String idPoleEmploi) {
        suiviUtilisateurUtile.supprimerTracesParcoursUtilisateur(idPoleEmploi);        
    }
}
