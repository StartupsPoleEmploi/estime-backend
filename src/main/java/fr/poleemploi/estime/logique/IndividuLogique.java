package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.utile.AccesTokenUtile;
import fr.poleemploi.estime.commun.utile.DemandeurDemoUtile;
import fr.poleemploi.estime.commun.utile.IndividuUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeConnectUtile;
import fr.poleemploi.estime.logique.simulateur.aides.SimulateurAides;
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
    private PoleEmploiIOClient emploiStoreDevClient;

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

    public Individu authentifier(String code, String redirectURI, String nonce) {
	Individu individu = new Individu();

	PeConnectAuthorizationPEIO peConnectAuthorizationESD = emploiStoreDevClient.callAccessTokenEndPoint(code, redirectURI, nonce);
	String bearerToken = accesTokenUtile.getBearerToken(peConnectAuthorizationESD.getAccessToken());

	DetailIndemnisationPEIO detailIndemnisationESD = emploiStoreDevClient.callDetailIndemnisationEndPoint(bearerToken);
	UserInfoPEIO userInfoESD = emploiStoreDevClient.callUserInfoEndPoint(bearerToken);

	if (stagingEnvironnementUtile.isStagingEnvironnement() && stagingEnvironnementUtile.isUtilisateurFictif(userInfoESD)) {
	    stagingEnvironnementUtile.gererAccesAvecBouchon(individu, userInfoESD);
	} else {
	    individu.setIdPoleEmploi(userInfoESD.getSub());
	    if (demandeurDemoUtile.isDemandeurDemo(userInfoESD)) {
		individu.setPopulationAutorisee(true);
		demandeurDemoUtile.addInformationsDetailIndemnisationPoleEmploi(individu, detailIndemnisationESD);
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

    public DemandeurEmploi creerDemandeurEmploi(Individu individu) {

	String accessToken = individu.getPeConnectAuthorization().getAccessToken();
	String bearerToken = accesTokenUtile.getBearerToken(accessToken);

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	demandeurEmploi.setIdPoleEmploi(individu.getIdPoleEmploi());
	demandeurEmploiUtile.addInformationsPersonnelles(demandeurEmploi, individu);
	if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
	    demandeurEmploiUtile.addCodeDepartement(demandeurEmploi, bearerToken);
	    demandeurEmploiUtile.addDateNaissance(demandeurEmploi, bearerToken);
	}
	demandeurEmploi.setBeneficiaireAides(individu.getBeneficiaireAides());
	demandeurEmploiUtile.addRessourcesFinancieres(demandeurEmploi, individu);

	if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
	    suiviUtilisateurUtile.tracerParcoursUtilisateurCreationSimulation(demandeurEmploi.getIdPoleEmploi(), ParcoursUtilisateur.SIMULATION_COMMENCEE.getParcours(),
		    individu.getBeneficiaireAides(), demandeurEmploi.getInformationsPersonnelles());
	}

	return demandeurEmploi;
    }

    public SimulationAides simulerMesAides(DemandeurEmploi demandeurEmploi) {
	SimulationAides simulationAides = simulateurAides.simuler(demandeurEmploi);

	if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
	    suiviUtilisateurUtile.tracerParcoursUtilisateurCreationSimulation(demandeurEmploi.getIdPoleEmploi(), ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(),
		    demandeurEmploi.getBeneficiaireAides(), demandeurEmploi.getInformationsPersonnelles());
	}

	return simulationAides;
    }

    public void supprimerSuiviParcoursUtilisateur(String idPoleEmploi) {
	suiviUtilisateurUtile.supprimerTracesParcoursUtilisateur(idPoleEmploi);
    }
}
