package testsintegration.simulation.temporalite.ass;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Optional;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AideMobiliteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
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

	protected DemandeurEmploi createDemandeurEmploi() throws ParseException {
		boolean isEnCouple = false;
		int nbEnfant = 1;
		DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
		demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
		demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
		demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
		demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
		demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
		demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1245);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1600);
		demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
		demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
		demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
		demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
		demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utile.getDate("14-04-2020"));
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(117f);

		return demandeurEmploi;
	}

	protected void initMocks(DemandeurEmploi demandeurEmploi, boolean decisionAgepi, boolean decisionAideMobilite, boolean decisionAre) throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
		// mock création date de demande de simulation
		doReturn(utile.getDate("20-10-2020")).when(dateUtile).getDateJour();

		//mock retour appel détail indemnisation de l'ESD 
		DetailIndemnisationPEIO detailIndemnisationPEIO = utile.creerDetailIndemnisationPEIO(TypePopulation.ASS.getLibelle());
		doReturn(detailIndemnisationPEIO).when(poleEmploiIOClient).callDetailIndemnisationEndPoint(Mockito.any(String.class));


		// mock tracer parcours utilisateur
		doNothing().when(suiviUtilisateurUtile).tracerParcoursUtilisateurCreationSimulation(demandeurEmploi.getIdPoleEmploi(),
				ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(), demandeurEmploi.getBeneficiaireAides(), demandeurEmploi.getInformationsPersonnelles());

		// mock création date de demande de simulation
		doReturn(utile.getDate("20-10-2020")).when(dateUtile).getDateJour();

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
}
