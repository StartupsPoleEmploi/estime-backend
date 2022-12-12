package testsintegration.simulation.temporalite.autresAides;

import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

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
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.MicroEntreprise;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utileTests;

    @SpyBean
    private PoleEmploiIOClient poleEmploiIOClient;

    @SpyBean
    protected DateUtile dateUtile;

    protected DemandeurEmploi createDemandeurEmploi() throws ParseException {
	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS, isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(800);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1245);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(false);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(117f);

	return demandeurEmploi;
    }

    protected DemandeurEmploi createDemandeurEmploiMicroEntrepreneur(String typesBenefices) throws ParseException {
	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.NON_BENEFICIAIRE, isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(800);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1245);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(117f);

	demandeurEmploi.getInformationsPersonnelles().setMicroEntrepreneur(true);
	demandeurEmploi.getInformationsPersonnelles().setMicroEntreprise(new MicroEntreprise());
	demandeurEmploi.getInformationsPersonnelles().getMicroEntreprise().setDateRepriseCreationEntreprise(dateUtile.enleverMoisALocalDate(dateUtile.getDateJour(), 10));
	demandeurEmploi.getInformationsPersonnelles().getMicroEntreprise().setTypeBenefices(typesBenefices);
	demandeurEmploi.getInformationsPersonnelles().getMicroEntreprise().setChiffreAffairesN(1200f);
	demandeurEmploi.getInformationsPersonnelles().getMicroEntreprise().setChiffreAffairesNMoins1(3000f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setBeneficesMicroEntrepriseDernierExercice(1200f);
	return demandeurEmploi;

    }

    protected DemandeurEmploi createDemandeurEmploiSalarie() throws ParseException {
	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.NON_BENEFICIAIRE, isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(800);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1245);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(117f);

	demandeurEmploi.getInformationsPersonnelles().setSalarie(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1000, 1291, 1));

	return demandeurEmploi;

    }

    protected void initMocks() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	doReturn(utileTests.getDate("01-01-2022")).when(dateUtile).getDateJour();

	DetailIndemnisationPEIOOut detailIndemnisationESD = utileTests.creerDetailIndemnisationPEIO(TypePopulationEnum.ASS);
	doReturn(detailIndemnisationESD).when(poleEmploiIOClient).getDetailIndemnisation(Mockito.any(String.class));
    }
}
