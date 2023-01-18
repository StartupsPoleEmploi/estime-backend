package testsintegration.simulation.temporalite.aahEtAre;

import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIOOut;
import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.StatutOccupationLogementEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Logement;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utile;

    @SpyBean
    protected DateUtile dateUtile;

    @SpyBean
    private PoleEmploiIOClient poleEmploiIOClient;

    private static int PROCHAINE_DECLARATION_TRIMESTRIELLE = 0;

    protected DemandeurEmploi createDemandeurEmploi(boolean isEnCouple, int nbEnfant)
	    throws ParseException, JSONException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.AAH_ARE, isEnCouple, nbEnfant);

	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());

	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(800);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1000);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);

	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setNombreJoursRestants(100);
	allocationARE.setAllocationJournaliereBrute(28f);
	allocationARE.setSalaireJournalierReferenceBrut(40f);
	allocationARE.setHasDegressiviteAre(false);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().setAllocationARE(allocationARE);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(false);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setNombreMoisTravaillesDerniersMois(0);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAllocationAAH(400f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	return demandeurEmploi;
    }

    protected void initMocks() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	doReturn(utile.getDate("01-01-2022")).when(dateUtile).getDateJour();

	DetailIndemnisationPEIOOut detailIndemnisationESD = utile.creerDetailIndemnisationPEIO(TypePopulationEnum.AAH_ARE);
	doReturn(detailIndemnisationESD).when(poleEmploiIOClient).getDetailIndemnisation(Mockito.any(String.class));
    }

    protected Logement initLogement() {
	Logement logement = new Logement();
	logement.setStatutOccupationLogement(StatutOccupationLogementEnum.LOCATAIRE_NON_MEUBLE.getLibelle());
	logement.setMontantLoyer(500f);
	logement.setCoordonnees(createCoordonnees());
	return logement;
    }

    protected Coordonnees createCoordonnees() {
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44200");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	return coordonnees;
    }
}
