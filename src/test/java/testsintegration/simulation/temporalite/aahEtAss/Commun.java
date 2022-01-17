package testsintegration.simulation.temporalite.aahEtAss;

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
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utileTests;

    @SpyBean
    protected DateUtile dateUtile;

    @SpyBean
    private PoleEmploiIOClient poleEmploiIOClient;

    private static int PROCHAINE_DECLARATION_TRIMESTRIELLE = 0;

    protected DemandeurEmploi createDemandeurEmploi() throws ParseException {

	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.AAH_ASS.getLibelle(), isEnCouple, nbEnfant);

	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());

	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));

	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(940);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1200);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);

	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
	demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

	AidesFamiliales aidesFamiliales = new AidesFamiliales();
	aidesFamiliales.setAllocationsFamiliales(0);
	aidesFamiliales.setAllocationSoutienFamilial(117);
	aidesFamiliales.setComplementFamilial(0);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesFamiliales(aidesFamiliales);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationAAH(900f);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);

	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2020"));

	return demandeurEmploi;
    }

    protected void initMocks(DemandeurEmploi demandeurEmploi)
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	doReturn(utileTests.getDate("20-10-2020")).when(dateUtile).getDateJour();

	DetailIndemnisationPEIOOut detailIndemnisationESD = utileTests.creerDetailIndemnisationPEIO(TypePopulationEnum.AAH_ASS.getLibelle());
	doReturn(detailIndemnisationESD).when(poleEmploiIOClient).getDetailIndemnisation(Mockito.any(String.class));

	doReturn(400f).when(poleEmploiIOClient).getMontantAgepiSimulateurAides(Mockito.any(DemandeurEmploi.class));

	doReturn(450f).when(poleEmploiIOClient).getMontantAideMobiliteSimulateurAides(Mockito.any(DemandeurEmploi.class));
    }
}
