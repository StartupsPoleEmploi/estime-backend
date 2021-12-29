package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIOOut;
import fr.poleemploi.estime.commun.utile.DemandeurDemoUtile;
import fr.poleemploi.estime.commun.utile.IndividuUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.services.ressources.Individu;

@Component
public class IndividuLogique {

    @Autowired
    private DemandeurDemoUtile demandeurDemoUtile;    

    @Autowired
    private PoleEmploiIOClient poleEmploiIOClient;

    @Autowired
    private IndividuUtile individuUtile;

    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    @Autowired
    private SuiviUtilisateurUtile suiviUtilisateurUtile;

    public Individu authentifier(String code, String redirectURI, String nonce) {
        Individu individu = new Individu();

        individu.setPeConnectAuthorization(poleEmploiIOClient.getPeConnectAuthorizationByCode(code, redirectURI, nonce));

        DetailIndemnisationPEIOOut detailIndemnisationESD = poleEmploiIOClient.getDetailIndemnisation(individu.getPeConnectAuthorization().getBearerToken());
        UserInfoPEIOOut userInfoESD = poleEmploiIOClient.getUserInfo(individu.getPeConnectAuthorization().getBearerToken());

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

        return individu;
    }

}
