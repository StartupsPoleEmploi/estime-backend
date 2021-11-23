package testsintegration.simulation.poleemploiio;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

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
import utile.tests.UtileTests;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class InterfacageAPITest {

    @Autowired
    private PoleEmploiIOClient poleEmploiIOClient;

    @Autowired
    protected UtileTests utileTests;


    private final String accessToken = "Bearer AdDRTTX4saBwb5Lihs1tGQMBMU0";

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void testInterfacageApiAGEPIValide() {

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

	AgepiPEIOOut agepiOut = poleEmploiIOClient.callAgepiEndPoint(agepiPEIOIn, this.accessToken).get();
	float montant = agepiOut.getDecisionAGEPIAPI().getMontant();
	assertThat(montant).isPositive();
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

	AgepiPEIOOut agepiOut = poleEmploiIOClient.callAgepiEndPoint(agepiPEIOIn, this.accessToken).get();
	float montant = agepiOut.getDecisionAGEPIAPI().getMontant();
	assertThat(montant).isZero();
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

	AideMobilitePEIOOut aideMobiliteOut = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn, this.accessToken).get();
	float montant = aideMobiliteOut.getDecisionAideMobiliteAPI().getMontant();
	assertThat(montant).isPositive();
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

	AideMobilitePEIOOut aideMobiliteOut = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn, this.accessToken).get();
	float montant = aideMobiliteOut.getDecisionAideMobiliteAPI().getMontant();
	assertThat(montant).isZero();
    }

    @Test
    void testInterfacageApiAreValide() {
	ArePEIOIn areIn = new ArePEIOIn();
	areIn.setAllocationBruteJournaliere(45f);
	areIn.setGainBrut(27f);
	areIn.setSalaireBrutJournalier(375f);

	ArePEIOOut areOut = poleEmploiIOClient.callAreEndPoint(areIn, this.accessToken).get();
	float montant = areOut.getAllocationMensuelle();
	assertThat(montant).isPositive();
    }

    @Test
    void testInterfacageApiAreInValide() {
	ArePEIOIn areIn = new ArePEIOIn();
	areIn.setAllocationBruteJournaliere(0f);
	areIn.setGainBrut(555555f);
	areIn.setSalaireBrutJournalier(7777f);

	ArePEIOOut areOut = poleEmploiIOClient.callAreEndPoint(areIn, this.accessToken).get();
	float montant = areOut.getAllocationMensuelle();
	assertThat(montant).isZero();
    }

    protected DemandeurEmploi createDemandeurEmploi(int prochaineDeclarationTrimestrielle) throws ParseException {

	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);

	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");

	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));

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

    //	@SuppressWarnings("removal")
    //	@Test
    //	void testAideAreValide() {
    //		IndividuLogique individuLogique = new IndividuLogique();
    //		DemandeurEmploi demandeurEmploi = createDemandeurEmploi(0);
    //        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
    //        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(2);
    //        demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1101, 850, 1038, 800, 0, 0));
    //
    //		PeConnectAuthorization peConnectAuthorization = new PeConnectAuthorization();
    //		RessourcesFinancieres ressourceFinancieres = new RessourcesFinancieres();
    //		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
    //		AllocationARE allocationAre = new AllocationARE();
    //		Salaire salaire = new Salaire();
    //		SimulateurAides simulateurAides = new SimulateurAides();
    //		SimulateurAidesPoleEmploi simulateurAidesPoleEmploi;
    //		
    //		salaire.setMontantBrut(365);
    //		allocationAre.setMontantJournalierBrut(new Float(27));
    //		allocationAre.setSalaireJournalierReferenceBrut(new Float(45));
    //		aidesPoleEmploi.setAllocationARE(allocationAre);
    //		ressourceFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
    //		ressourceFinancieres.setSalaire(salaire);
    //		
    //		peConnectAuthorization.setAccessToken(this.accessToken);
    //		demandeurEmploi.setPeConnectAuthorization(peConnectAuthorization);
    //		demandeurEmploi.setRessourcesFinancieres(ressourceFinancieres);
    //
    //		SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);
    //		
    ////		AreUtile are = new AreUtile();
    ////		Map<String, Aide>  aidesPourCeMois = new HashMap<String, Aide>();
    ////		if(are.simulerAide(demandeurEmploi).isPresent()) {
    ////        	aidesPourCeMois.put(Aides.ALLOCATION_RETOUR_EMPLOI.getCode(), are.simulerAide(demandeurEmploi).get());
    ////        }
    ////		Aide areAide = are.simulerAide(demandeurEmploi).get();
    ////		
    //		assertThat(simulationAides).isNotNull();
    //	}
}
