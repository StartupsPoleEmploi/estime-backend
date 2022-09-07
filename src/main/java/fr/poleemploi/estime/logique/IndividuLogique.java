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
import fr.poleemploi.estime.services.ressources.PeConnectPayload;

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

    public Individu authentifier(PeConnectPayload peConnectPayload, String trafficSource) {
	Individu individu = new Individu();

	if (peConnectPayload != null) {
	    return setIndividuAvecConnexion(individu, peConnectPayload, trafficSource);
	}
	return setIndividuSansConnexion();
    }

    public Individu setIndividuSansConnexion() {
	return individuUtile.creerIndividuNonConnecte();
    }

    public Individu setIndividuAvecConnexion(Individu individu, PeConnectPayload peConnectPayload, String trafficSource) {

	individu.setPeConnectAuthorization(
		poleEmploiIOClient.getPeConnectAuthorizationByCode(peConnectPayload.getCode(), peConnectPayload.getRedirectURI(), peConnectPayload.getNonce()));

	DetailIndemnisationPEIOOut detailIndemnisationESD = poleEmploiIOClient.getDetailIndemnisation(individu.getPeConnectAuthorization().getBearerToken());
	UserInfoPEIOOut userInfoPEIO = poleEmploiIOClient.getUserInfo(individu.getPeConnectAuthorization().getBearerToken());

	if (stagingEnvironnementUtile.isStagingEnvironnement() && stagingEnvironnementUtile.isUtilisateurFictif(userInfoPEIO)) {
	    stagingEnvironnementUtile.gererAccesAvecBouchon(individu, userInfoPEIO);
	} else {
	    individu.setIdPoleEmploi(userInfoPEIO.getSub());
	    if (demandeurDemoUtile.isDemandeurDemo(userInfoPEIO)) {
		individu.setPopulationAutorisee(true);
		demandeurDemoUtile.addInformationsDetailIndemnisationPoleEmploi(individu);
		demandeurDemoUtile.addInformationsPersonnelles(individu, userInfoPEIO);
	    } else {
		individu.setPopulationAutorisee(individuUtile.isPopulationAutorisee(detailIndemnisationESD));
		individuUtile.addInformationsDetailIndemnisationPoleEmploi(individu, detailIndemnisationESD);
		individuUtile.addInformationsPersonnelles(individu, userInfoPEIO);
	    }
	}

	if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
	    suiviUtilisateurUtile.tracerParcoursUtilisateurAuthentification(userInfoPEIO, suiviUtilisateurUtile.getParcoursAccesService(individu), individu.getBeneficiaireAides(),
		    detailIndemnisationESD, trafficSource);
	}

	return individu;
    }

}
