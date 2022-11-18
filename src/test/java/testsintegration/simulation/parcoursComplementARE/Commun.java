package testsintegration.simulation.parcoursComplementARE;

import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.services.ressources.AllocationARE;
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

    protected DemandeurEmploi createDemandeurEmploi(LocalDate dateDebutSimulation)
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
	initMocks(dateDebutSimulation);
	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploiParcoursComplementARE();

	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setAllocationJournaliereBrute(112.02f);
	allocationARE.setAllocationJournaliereBruteTauxPlein(112.02f);
	allocationARE.setSalaireJournalierReferenceBrut(196.53f);
	allocationARE.setHasDegressiviteAre(false);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().setAllocationARE(allocationARE);

	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(1307f);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1678.95f);

	return demandeurEmploi;
    }

    protected void initMocks(LocalDate dateDebutSimulation) throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
	//mock création date de demande de simulation
	doReturn(dateDebutSimulation).when(dateUtile).getDateJour();
    }

}
