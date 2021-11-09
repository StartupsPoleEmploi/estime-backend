package fr.poleemploi.estime.logique;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIODevClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoESD;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.utile.AccesTokenUtile;
import fr.poleemploi.estime.commun.utile.DemandeurDemoUtile;
import fr.poleemploi.estime.commun.utile.IndividuUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeConnectUtile;
import fr.poleemploi.estime.logique.simulateur.aides.SimulateurAides;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class IndividuLogique {

    @Autowired
    private AccesTokenUtile accesTokenUtile;

    @Autowired
    private DemandeurDemoUtile demandeurDemoUtile;

    @Autowired
    private DemandeurEmploiUtile demandeurEmploiUtile;

    @Autowired
    private PoleEmploiIODevClient emploiStoreDevClient;

    @Autowired
    private IndividuUtile individuUtile;

    @Autowired
    private PeConnectUtile peConnectUtile;

    @Autowired
    private SimulateurAides simulateurAides;

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
        Optional<CoordonneesESD> coordonneesESDOption = emploiStoreDevClient.callCoordonneesAPI(bearerToken);
        Optional<UserInfoESD> userInfoOption = emploiStoreDevClient.callUserInfoEndPoint(bearerToken);

        if (userInfoOption.isPresent() && coordonneesESDOption.isPresent()) {
            UserInfoESD userInfoESD = userInfoOption.get();
            CoordonneesESD coordonneesESD = coordonneesESDOption.get();
            if (stagingEnvironnementUtile.isStagingEnvironnement()) {
                stagingEnvironnementUtile.gererAccesAvecBouchon(individu, userInfoESD, coordonneesESD);
            } else {
                individu.setIdPoleEmploi(userInfoESD.getSub());
                if (demandeurDemoUtile.isDemandeurDemo(userInfoESD)) {
                    individu.setPopulationAutorisee(true);
                    demandeurDemoUtile.addInformationsDetailIndemnisationPoleEmploi(individu, detailIndemnisationESD, coordonneesESD);
                } else {
                    individu.setPopulationAutorisee(individuUtile.isPopulationAutorisee(detailIndemnisationESD));
                    individuUtile.addInformationsDetailIndemnisationPoleEmploi(individu, detailIndemnisationESD, coordonneesESD);
                }
            }

            // @TODO JLA : remettre individu.isPopulationAutorisee() à la place de true après expérimentation
            suiviUtilisateurUtile.tracerParcoursUtilisateur(userInfoESD, suiviUtilisateurUtile.getParcoursAccesService(individu), individu.getBeneficiaireAides(),
                    individu.getInformationsPersonnelles(), detailIndemnisationESD);

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

        demandeurEmploi.setBeneficiaireAides(individu.getBeneficiaireAides());
        demandeurEmploiUtile.addRessourcesFinancieres(demandeurEmploi, individu);
        demandeurEmploiUtile.addInformationsPersonnelles(demandeurEmploi, individu, bearerToken);

        suiviUtilisateurUtile.tracerParcoursUtilisateur(demandeurEmploi.getIdPoleEmploi(), ParcoursUtilisateur.SIMULATION_COMMENCEE.getParcours(), individu.getBeneficiaireAides(),
                individu.getInformationsPersonnelles());

        return demandeurEmploi;
    }

    public SimulationAides simulerMesAides(DemandeurEmploi demandeurEmploi) {
        SimulationAides simulationAides = simulateurAides.simuler(demandeurEmploi);

        suiviUtilisateurUtile.tracerParcoursUtilisateur(demandeurEmploi.getIdPoleEmploi(), ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(), demandeurEmploi.getBeneficiaireAides(),
                demandeurEmploi.getInformationsPersonnelles());

        return simulationAides;
    }

    public void supprimerSuiviParcoursUtilisateur(String idPoleEmploi) {
        suiviUtilisateurUtile.supprimerTracesParcoursUtilisateur(idPoleEmploi);
    }

}
