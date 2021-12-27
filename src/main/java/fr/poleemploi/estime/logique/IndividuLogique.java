package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.commun.utile.AccessTokenUtile;
import fr.poleemploi.estime.commun.utile.DemandeurDemoUtile;
import fr.poleemploi.estime.commun.utile.IndividuUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeConnectUtile;
import fr.poleemploi.estime.services.ressources.Individu;

@Component
public class IndividuLogique {

    @Autowired
    private AccessTokenUtile bearerTokenUtile;

    @Autowired
    private DemandeurDemoUtile demandeurDemoUtile;

    @Autowired
    private PoleEmploiIOClient poleEmploiIOClient;

    @Autowired
    private IndividuUtile individuUtile;

    @Autowired
    private PeConnectUtile peConnectUtile;

    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    @Autowired
    private SuiviUtilisateurUtile suiviUtilisateurUtile;
    

    public Individu authentifier(String code, String redirectURI, String nonce) {
        Individu individu = new Individu();

        PeConnectAuthorizationPEIO peConnectAuthorizationESD = poleEmploiIOClient.getPeConnectAuthorizationByCode(code, redirectURI, nonce);
        String bearerToken = bearerTokenUtile.getBearerToken(peConnectAuthorizationESD.getAccessToken());

        DetailIndemnisationPEIO detailIndemnisationESD = poleEmploiIOClient.getDetailIndemnisation(bearerToken);
        UserInfoPEIO userInfoESD = poleEmploiIOClient.getUserInfo(bearerToken);

        if (stagingEnvironnementUtile.isStagingEnvironnement() && stagingEnvironnementUtile.isUtilisateurFictif(userInfoESD)) {
            stagingEnvironnementUtile.gererAccesAvecBouchon(individu, userInfoESD);
        } else {
            individu.setIdPoleEmploi(userInfoESD.getSub());
            if (demandeurDemoUtile.isDemandeurDemo(userInfoESD)) {
                individu.setPopulationAutorisee(true);
                demandeurDemoUtile.addInformationsDetailIndemnisationPoleEmploi(individu);
            } else {
                individu.setPopulationAutorisee(individuUtile.isPopulationAutorisee(detailIndemnisationESD));
                individuUtile.addInformationsDetailIndemnisationPoleEmploi(individu, detailIndemnisationESD);
            }
        }

        if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
            // @TODO JLA : remettre individu.isPopulationAutorisee() à la place de true après expérimentation
            suiviUtilisateurUtile.tracerParcoursUtilisateurAuthentification(userInfoESD, suiviUtilisateurUtile.getParcoursAccesService(individu), individu.getBeneficiaireAides(),
                    detailIndemnisationESD);
        }

        individu.setPeConnectAuthorization(peConnectUtile.mapInformationsAccessTokenPeConnect(peConnectAuthorizationESD));

        return individu;
    }
}
