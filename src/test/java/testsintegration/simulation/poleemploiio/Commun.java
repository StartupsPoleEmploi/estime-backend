package testsintegration.simulation.poleemploiio;

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
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utile;

    @SpyBean
    protected DateUtile dateUtile;

    @SpyBean
    private SuiviUtilisateurUtile suiviUtilisateurUtile;

    @SpyBean
    private PoleEmploiIOClient poleEmploiIOClient;

    protected DemandeurEmploi createDemandeurEmploi(boolean isEnCouple, int nbEnfant) throws ParseException {

	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));

	return demandeurEmploi;
    }

    protected DemandeurEmploi createDemandeurEmploiAgepi() throws ParseException {

	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);

	return demandeurEmploi;
    }

    protected DemandeurEmploi createDemandeurEmploiAideMobilite() throws ParseException {

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);

	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(100f);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);

	return demandeurEmploi;
    }

    protected void initMocks(DemandeurEmploi demandeurEmploi, boolean decisionAgepi, boolean decisionAideMobilite, boolean decisionAre)
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
	// mock tracer parcours utilisateur
	doNothing().when(suiviUtilisateurUtile).tracerParcoursUtilisateurCreationSimulation(demandeurEmploi.getIdPoleEmploi(),
		ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(), demandeurEmploi.getBeneficiaireAides(), demandeurEmploi.getInformationsPersonnelles());

	// mock création date de demande de simulation
	doReturn(utile.getDate("20-10-2020")).when(dateUtile).getDateJour();

	//mock retour appel détail indemnisation de l'ESD 
	DetailIndemnisationPEIO detailIndemnisationPEIO = utile.creerDetailIndemnisationPEIO(TypePopulation.AAH_ASS.getLibelle());
	doReturn(detailIndemnisationPEIO).when(poleEmploiIOClient).callDetailIndemnisationEndPoint(Mockito.any(String.class));

	//mock retour appel api aide mobilite
	Optional<AgepiPEIOOut> agepiPEIOOut = Optional.of(utile.creerAgepiPEIOOut(decisionAgepi));
	doReturn(agepiPEIOOut).when(poleEmploiIOClient).callAgepiEndPoint(Mockito.any(AgepiPEIOIn.class), Mockito.any(String.class));

	//mock retour appel api aide mobilite
	Optional<AideMobilitePEIOOut> aideMobilitePEIOOut = Optional.of(utile.creerAideMobilitePEIOOut(decisionAideMobilite));
	doReturn(aideMobilitePEIOOut).when(poleEmploiIOClient).callAideMobiliteEndPoint(Mockito.any(AideMobilitePEIOIn.class), Mockito.any(String.class));//mock retour appel api aide mobilite

	//mock retour appel api are
	Optional<ArePEIOOut> arePEIOOut = Optional.of(utile.creerArePEIOOut(decisionAre));
	doReturn(arePEIOOut).when(poleEmploiIOClient).callAreEndPoint(Mockito.any(ArePEIOIn.class), Mockito.any(String.class));
    }

    protected void initMocksAPI() {
	initMocksAPI(false, false, false);
    }

    protected void initMocksAPI(boolean decisionAgepi, boolean decisionAideMobilite, boolean decisionAre) {

	//mock retour appel api aide mobilite
	Optional<AgepiPEIOOut> agepiPEIOOut = Optional.of(utile.creerAgepiPEIOOut(decisionAgepi));
	doReturn(agepiPEIOOut).when(poleEmploiIOClient).callAgepiEndPoint(Mockito.any(AgepiPEIOIn.class), Mockito.any(String.class));

	//mock retour appel api aide mobilite
	Optional<AideMobilitePEIOOut> aideMobilitePEIOOut = Optional.of(utile.creerAideMobilitePEIOOut(decisionAideMobilite));
	doReturn(aideMobilitePEIOOut).when(poleEmploiIOClient).callAideMobiliteEndPoint(Mockito.any(AideMobilitePEIOIn.class), Mockito.any(String.class));//mock retour appel api aide mobilite

	//mock retour appel api are
	Optional<ArePEIOOut> arePEIOOut = Optional.of(utile.creerArePEIOOut(decisionAre));
	doReturn(arePEIOOut).when(poleEmploiIOClient).callAreEndPoint(Mockito.any(ArePEIOIn.class), Mockito.any(String.class));
    }
}
