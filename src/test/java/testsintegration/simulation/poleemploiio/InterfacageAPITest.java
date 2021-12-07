package testsintegration.simulation.poleemploiio;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class InterfacageAPITest extends Commun {

	@Autowired
	private PoleEmploiIOClient poleEmploiIOClient;

	@Configuration
	@ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
	public static class SpringConfig {

	}

	@Test
	void testInterfacageApiAGEPIValide() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

		//        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
		//        // enfant à charge de 9ans, af = 90€
		//        // Montant net journalier ASS = 16,89€
		//        // AAH = 900€
		//        // 0 mois travaillé avant simulation
		//        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
		//        DemandeurEmploi demandeurEmploi = createDemandeurEmploiAgepi();
		//        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);
		//
		//        // Lorsque je simule mes prestations le 20/10/2020
		//        initMocks(demandeurEmploi);

		AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
		agepiPEIOIn.setContexte("Reprise");
		agepiPEIOIn.setDateActionReclassement("2021-11-22");
		agepiPEIOIn.setDateDepot("2021-11-22");
		agepiPEIOIn.setDureePeriodeEmploiOuFormation(90);
		agepiPEIOIn.setEleveSeulEnfants(true);
		agepiPEIOIn.setIntensite((int) Math.round(50));
		agepiPEIOIn.setLieuFormationOuEmploi("France");
		agepiPEIOIn.setNatureContratTravail("CDI");
		agepiPEIOIn.setNombreEnfants(3);
		agepiPEIOIn.setNombreEnfantsMoins10Ans(2);
		agepiPEIOIn.setOrigine("c");
		agepiPEIOIn.setTypeIntensite("Mensuelle");

		initMocksAPI(true, false, false);
		Optional<AgepiPEIOOut> agepiOutOptional = poleEmploiIOClient.callAgepiEndPoint(agepiPEIOIn, "");
		if (agepiOutOptional.isPresent()) {
			AgepiPEIOOut agepiOut = agepiOutOptional.get();
			float montant = agepiOut.getDecisionAgepiAPI().getMontant();
			assertThat(montant).isPositive();
		}
	}

	@Test
	void testInterfacageApiAGEPIInvalide() {
		AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
		agepiPEIOIn.setContexte("Reprise");
		agepiPEIOIn.setDateActionReclassement("2021-11-04");
		agepiPEIOIn.setDateDepot("2021-11-04");
		agepiPEIOIn.setDureePeriodeEmploiOuFormation(90);
		agepiPEIOIn.setEleveSeulEnfants(false);
		agepiPEIOIn.setIntensite((int) Math.round(50));
		agepiPEIOIn.setLieuFormationOuEmploi("France");
		agepiPEIOIn.setNatureContratTravail("CDI");
		agepiPEIOIn.setNombreEnfants(0);
		agepiPEIOIn.setNombreEnfantsMoins10Ans(0);
		agepiPEIOIn.setOrigine("c");
		agepiPEIOIn.setTypeIntensite("Mensuelle");

		initMocksAPI();
		Optional<AgepiPEIOOut> agepiOutOptional = poleEmploiIOClient.callAgepiEndPoint(agepiPEIOIn, "");
		if (agepiOutOptional.isPresent()) {
			AgepiPEIOOut agepiOut = agepiOutOptional.get();
			float montant = agepiOut.getDecisionAgepiAPI().getMontant();
			assertThat(montant).isZero();
		}
	}

	@Test
	void testInterfacageApiAideMobValide() {
		AideMobilitePEIOIn aideMobilitePEIOIn = new AideMobilitePEIOIn();
		aideMobilitePEIOIn.setContexte("Reprise");
		aideMobilitePEIOIn.setDateActionReclassement("2021-11-09");
		aideMobilitePEIOIn.setDateDepot("2021-11-09");
		aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
		aideMobilitePEIOIn.setNatureContratTravail("CDI");
		aideMobilitePEIOIn.setOrigine("c");
		aideMobilitePEIOIn.setDistanceDomicileActionReclassement(65000);
		aideMobilitePEIOIn.setNombreAllersRetours(15);
		aideMobilitePEIOIn.setNombreRepas(0);
		aideMobilitePEIOIn.setNombreNuitees(0);
		aideMobilitePEIOIn.setCodeTerritoire("001");
		aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
		aideMobilitePEIOIn.setEleveSeulEnfants(true);
		aideMobilitePEIOIn.setFraisPrisEnChargeParTiers(false);
		aideMobilitePEIOIn.setIntensite(50);
		aideMobilitePEIOIn.setTypeIntensite("mensuelle");
		aideMobilitePEIOIn.setNombreEnfantsMoins10Ans(2);
		aideMobilitePEIOIn.setNombreEnfants(3);
		aideMobilitePEIOIn.setLieuFormationOuEmploi("France");

		initMocksAPI(false, true, false);
		Optional<AideMobilitePEIOOut> aideMobilitePEIOOutOptional = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn, "");
		if (aideMobilitePEIOOutOptional.isPresent()) {
			AideMobilitePEIOOut agepiOut = aideMobilitePEIOOutOptional.get();
			float montant = agepiOut.getDecisionAideMobiliteAPI().getMontant();
			assertThat(montant).isPositive();
		}
	}

	@Test
	void testInterfacageApiAideMobInvalide() {
		AideMobilitePEIOIn aideMobilitePEIOIn = new AideMobilitePEIOIn();
		aideMobilitePEIOIn.setContexte("Reprise");
		aideMobilitePEIOIn.setDateActionReclassement("2021-11-09");
		aideMobilitePEIOIn.setDateDepot("2021-11-09");
		aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
		aideMobilitePEIOIn.setNatureContratTravail("CDI");
		aideMobilitePEIOIn.setOrigine("c");
		aideMobilitePEIOIn.setDistanceDomicileActionReclassement(25000);
		aideMobilitePEIOIn.setNombreAllersRetours(15);
		aideMobilitePEIOIn.setNombreRepas(0);
		aideMobilitePEIOIn.setNombreNuitees(0);
		aideMobilitePEIOIn.setCodeTerritoire("001");
		aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
		aideMobilitePEIOIn.setEleveSeulEnfants(true);
		aideMobilitePEIOIn.setFraisPrisEnChargeParTiers(false);
		aideMobilitePEIOIn.setIntensite(50);
		aideMobilitePEIOIn.setTypeIntensite("mensuelle");
		aideMobilitePEIOIn.setNombreEnfantsMoins10Ans(2);
		aideMobilitePEIOIn.setNombreEnfants(3);
		aideMobilitePEIOIn.setLieuFormationOuEmploi("France");

		initMocksAPI();
		Optional<AideMobilitePEIOOut> aideMobilitePEIOOutOptional = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn, "");
		if (aideMobilitePEIOOutOptional.isPresent()) {
			AideMobilitePEIOOut agepiOut = aideMobilitePEIOOutOptional.get();
			float montant = agepiOut.getDecisionAideMobiliteAPI().getMontant();
			assertThat(montant).isZero();
		}
	}

	@Test
	void testInterfacageApiAreValide() {
		ArePEIOIn areIn = new ArePEIOIn();
		areIn.setAllocationBruteJournaliere(45f);
		areIn.setGainBrut(27f);
		areIn.setSalaireBrutJournalier(375f);

		initMocksAPI(false, false, true);
		Optional<ArePEIOOut> areOutOptional = poleEmploiIOClient.callAreEndPoint(areIn, "");
		if (areOutOptional.isPresent()) {
			ArePEIOOut areOut = areOutOptional.get();
			float montant = areOut.getAllocationMensuelle();
			assertThat(montant).isPositive();
		}
	}

	@Test
	void testInterfacageApiAreInvalide() {
		ArePEIOIn areIn = new ArePEIOIn();
		areIn.setAllocationBruteJournaliere(0f);
		areIn.setGainBrut(555555f);
		areIn.setSalaireBrutJournalier(7777f);

		initMocksAPI();
		Optional<ArePEIOOut> areOutOptional = poleEmploiIOClient.callAreEndPoint(areIn, "");
		if (areOutOptional.isPresent()) {
			ArePEIOOut areOut = areOutOptional.get();
			float montant = areOut.getAllocationMensuelle();
			assertThat(montant).isZero();
		}
	}

	protected DemandeurEmploi createDemandeurEmploi(int prochaineDeclarationTrimestrielle) throws ParseException {

		boolean isEnCouple = false;
		int nbEnfant = 1;
		DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);

		demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
		demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
		demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");

		demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));

		demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
		demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(940);
		demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1200);
		demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
		demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);

		AidesFamiliales aidesFamiliales = new AidesFamiliales();
		aidesFamiliales.setAllocationsFamiliales(0);
		aidesFamiliales.setAllocationSoutienFamilial(117);
		aidesFamiliales.setComplementFamilial(0);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesFamiliales(aidesFamiliales);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationAAH(900f);
		demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(prochaineDeclarationTrimestrielle);

		return demandeurEmploi;
	}
}
