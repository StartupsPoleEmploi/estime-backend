package testsintegration.simulation.temporalite.aideLogement;

import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AideMobiliteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.PeConnectAuthorization;
import fr.poleemploi.estime.services.ressources.Personne;
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
	private AgepiUtile agepiUtile;
	
	@SpyBean
	private AideMobiliteUtile aideMobUtile;

	@SpyBean
	private AreUtile areUtile;

	protected void initMocks(String dateSimulation, DemandeurEmploi demandeurEmploi, boolean decisionAgepi, boolean decisionAideMobilite, boolean decisionAre) throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
		// mock création date de demande de simulation
		doReturn(utile.getDate(dateSimulation)).when(dateUtile).getDateJour();
		
		// mock la creation du PeConnectAuth du demandeur d'emploi
		PeConnectAuthorization peConnectAuthorization = new PeConnectAuthorization();
		peConnectAuthorization.setBearerToken("");
		peConnectAuthorization.setExpireIn(Long.valueOf(222));
		peConnectAuthorization.setExpiryTime(new Date());
		peConnectAuthorization.setIdToken("");
		peConnectAuthorization.setRefreshToken("");
		peConnectAuthorization.setScope("");
		peConnectAuthorization.setTokenType("");
		demandeurEmploi.setPeConnectAuthorization(peConnectAuthorization);

		//mock retour appel détail indemnisation de l'ESD 
		DetailIndemnisationPEIO detailIndemnisationPEIO = utile.creerDetailIndemnisationPEIO(TypePopulationEnum.RSA.getLibelle());
		doReturn(detailIndemnisationPEIO).when(poleEmploiIOClient).getDetailIndemnisation(Mockito.any(String.class));

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
	}

	protected Logement initLogement(String codeInsee, boolean isLogementConventionne) {
		Logement logement = new Logement();
		StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
		statutOccupationLogement.setLocataireNonMeuble(true);
		logement.setStatutOccupationLogement(statutOccupationLogement);
		logement.setMontantCharges(50f);
		logement.setMontantLoyer(500f);
		logement.setCodeInsee(codeInsee);
		logement.setConventionne(isLogementConventionne);
		logement.setDeMayotte(false);
		return logement;
	}

	protected DemandeurEmploi creerDemandeurEmploiAPL(int prochaineDeclarationTrimestrielle) throws ParseException {
		boolean isEnCouple = true;
		int nbEnfant = 2;
		DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);

		demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
		demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
		demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");

		demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
		demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(7));

		demandeurEmploi.getInformationsPersonnelles().setLogement(initLogement("44109", true));
		demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
		demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(940);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1200);
		demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(20);
		demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);

		demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
		demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

		AidesFamiliales aidesFamiliales = new AidesFamiliales();
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesFamiliales(aidesFamiliales);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(prochaineDeclarationTrimestrielle);

		AidesLogement aidesLogement = new AidesLogement();
		AllocationsLogement aidePersonnaliseeLogement = new AllocationsLogement();
		aidePersonnaliseeLogement.setMoisN(300f);
		aidePersonnaliseeLogement.setMoisNMoins1(300f);
		aidePersonnaliseeLogement.setMoisNMoins2(300f);
		aidePersonnaliseeLogement.setMoisNMoins3(300f);
		aidesLogement.setAidePersonnaliseeLogement(aidePersonnaliseeLogement);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesLogement(aidesLogement);

		return demandeurEmploi;
	}

	protected DemandeurEmploi creerDemandeurEmploiALF(int prochaineDeclarationTrimestrielle) throws ParseException {
		boolean isEnCouple = true;
		int nbEnfant = 2;
		DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);

		demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
		demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
		demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");

		demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
		demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(7));

		demandeurEmploi.getInformationsPersonnelles().setLogement(initLogement("44109", false));
		demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
		demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(940);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1200);
		demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(20);
		demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);

		demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
		demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

		AidesFamiliales aidesFamiliales = new AidesFamiliales();
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesFamiliales(aidesFamiliales);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(prochaineDeclarationTrimestrielle);

		AidesLogement aidesLogement = new AidesLogement();
		AllocationsLogement allocationLogementFamiliale = new AllocationsLogement();
		allocationLogementFamiliale.setMoisN(300f);
		allocationLogementFamiliale.setMoisNMoins1(300f);
		allocationLogementFamiliale.setMoisNMoins2(300f);
		allocationLogementFamiliale.setMoisNMoins3(300f);
		aidesLogement.setAllocationLogementFamiliale(allocationLogementFamiliale);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesLogement(aidesLogement);

		return demandeurEmploi;
	}

	protected DemandeurEmploi creerDemandeurEmploiALS(int prochaineDeclarationTrimestrielle) throws ParseException {
		boolean isEnCouple = false;
		int nbEnfant = 0;
		DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);

		demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
		demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
		demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");

		demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
		List<Personne> personnesACharge = new ArrayList<Personne>();
		demandeurEmploi.getSituationFamiliale().setPersonnesACharge(personnesACharge);

		demandeurEmploi.getInformationsPersonnelles().setLogement(initLogement("44109", false));
		demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
		demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(940);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1200);
		demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(20);
		demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);

		demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
		demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

		AidesFamiliales aidesFamiliales = new AidesFamiliales();
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesFamiliales(aidesFamiliales);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(prochaineDeclarationTrimestrielle);

		AidesLogement aidesLogement = new AidesLogement();
		AllocationsLogement allocationLogementSociale = new AllocationsLogement();
		allocationLogementSociale.setMoisN(300f);
		allocationLogementSociale.setMoisNMoins1(300f);
		allocationLogementSociale.setMoisNMoins2(300f);
		allocationLogementSociale.setMoisNMoins3(300f);
		aidesLogement.setAllocationLogementSociale(allocationLogementSociale);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesLogement(aidesLogement);

		return demandeurEmploi;
	}

}
