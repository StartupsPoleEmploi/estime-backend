package testsintegration.simulation.temporalite.rsa;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.Optional;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AideMobiliteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.PeConnectAuthorization;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
import utile.tests.Utile;

public class Commun {

	@Autowired
	protected Utile utile;

	@SpyBean
	private PoleEmploiIOClient poleEmploiIOClient;

	@SpyBean
	protected DateUtile dateUtile;
	
	@SpyBean
	private SuiviUtilisateurUtile suiviUtilisateurUtile;
	
	@SpyBean
	private AgepiUtile agepiUtile;
	
	@SpyBean
	private AideMobiliteUtile aideMobUtile;

	@SpyBean
	private AreUtile areUtile;

	protected void initMocks(String dateSimulation, DemandeurEmploi demandeurEmploi, boolean decisionAgepi, boolean decisionAideMobilite, boolean decisionAre) throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
		//mock création date de demande de simulation
		doReturn(utile.getDate(dateSimulation)).when(dateUtile).getDateJour();

		//mock retour appel détail indemnisation de l'ESD 
		DetailIndemnisationPEIO detailIndemnisationPEIO = utile.creerDetailIndemnisationPEIO(TypePopulation.RSA.getLibelle());
		doReturn(detailIndemnisationPEIO).when(poleEmploiIOClient).callDetailIndemnisationEndPoint(Mockito.any(String.class));

		// mock la creation du PeConnectAuth du demandeur d'emploi
		PeConnectAuthorization peConnectAuthorization = new PeConnectAuthorization();
		peConnectAuthorization.setAccessToken("");
		peConnectAuthorization.setExpireIn(new Long(222));
		peConnectAuthorization.setExpiryTime(new Date());
		peConnectAuthorization.setIdToken("");
		peConnectAuthorization.setRefreshToken("");
		peConnectAuthorization.setScope("");
		peConnectAuthorization.setTokenType("");
		demandeurEmploi.setPeConnectAuthorization(peConnectAuthorization);
		
		demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

		// mock tracer parcours utilisateur
		doNothing().when(suiviUtilisateurUtile).tracerParcoursUtilisateurCreationSimulation(demandeurEmploi.getIdPoleEmploi(),
				ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(), demandeurEmploi.getBeneficiaireAides(), demandeurEmploi.getInformationsPersonnelles());

		//mock retour appel api aide mobilite
		if(decisionAgepi) {
			Optional<Aide> aideAgepi = Optional.of(utile.creerAidePourMock("AGEPI"));
			doReturn(aideAgepi).when(agepiUtile).simulerAide(Mockito.any(DemandeurEmploi.class));		
		}

		//mock retour appel api aide mobilite
		if(decisionAideMobilite) {
			Optional<Aide> aideMobilite = Optional.of(utile.creerAidePourMock("AM"));
			doReturn(aideMobilite).when(aideMobUtile).simulerAide(Mockito.any(DemandeurEmploi.class));
		}

		//mock retour appel api are
		if(decisionAre) {
			Optional<Aide> arePEIOOut = Optional.of(utile.creerAidePourMock("ARE"));
			doReturn(arePEIOOut).when(areUtile).simulerAide(Mockito.any(DemandeurEmploi.class));
		}
	}

	protected Logement initLogement() {
		Logement logement = new Logement();
		StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
		statutOccupationLogement.setLocataireNonMeuble(true);
		logement.setStatutOccupationLogement(statutOccupationLogement);
		logement.setMontantCharges(50f);
		logement.setMontantLoyer(500f);
		logement.setCodeInsee("44109");
		logement.setDeMayotte(false);
		return logement;
	}
}
