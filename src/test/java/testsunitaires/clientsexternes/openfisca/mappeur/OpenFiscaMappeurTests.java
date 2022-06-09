package testsunitaires.clientsexternes.openfisca.mappeur;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tsohr.JSONException;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeur;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurTests extends Commun {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;

    @Autowired
    private OpenFiscaMappeur openFiscaMappeur;

    @Autowired
    Utile testUtile;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void mapDemandeurAplToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-apl.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	AidesLogement aidesLogement = new AidesLogement();
	AllocationsLogement aidePersonnaliseeLogement = new AllocationsLogement();
	aidePersonnaliseeLogement.setMoisN(385);
	aidePersonnaliseeLogement.setMoisNMoins1(380);
	aidePersonnaliseeLogement.setMoisNMoins2(360);
	aidePersonnaliseeLogement.setMoisNMoins3(357);
	aidesLogement.setAidePersonnaliseeLogement(aidePersonnaliseeLogement);
	aidesCAF.setAidesLogement(aidesLogement);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAlfToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-alf.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	AidesLogement aidesLogement = new AidesLogement();
	AllocationsLogement allocationLogementFamiliale = new AllocationsLogement();
	allocationLogementFamiliale.setMoisN(385);
	allocationLogementFamiliale.setMoisNMoins1(380);
	allocationLogementFamiliale.setMoisNMoins2(360);
	allocationLogementFamiliale.setMoisNMoins3(357);
	aidesLogement.setAllocationLogementFamiliale(allocationLogementFamiliale);
	aidesCAF.setAidesLogement(aidesLogement);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAlsToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-als.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	AidesLogement aidesLogement = new AidesLogement();
	AllocationsLogement allocationLogementSociale = new AllocationsLogement();
	allocationLogementSociale.setMoisN(385);
	allocationLogementSociale.setMoisNMoins1(380);
	allocationLogementSociale.setMoisNMoins2(360);
	allocationLogementSociale.setMoisNMoins3(357);
	aidesLogement.setAllocationLogementSociale(allocationLogementSociale);
	aidesCAF.setAidesLogement(aidesLogement);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurSansFamilleToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-sans-famille.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusLocatifsToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-revenus-locatifs.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(1040);
	salaire.setMontantBrut(1342);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setRevenusImmobilier3DerniersMois(3000f);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusTravailleurIndependantToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-revenus-travailleur-independant.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(1040);
	salaire.setMontantBrut(1342);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setChiffreAffairesIndependantDernierExercice(1000f);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusMicroEntrepriseToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-revenus-micro-entreprise.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(1040);
	salaire.setMontantBrut(1342);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setBeneficesMicroEntrepriseDernierExercice(600f);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPensionInvaliditeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-pension-invalidite.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(800);
	salaire.setMontantBrut(1038);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	ressourcesFinancieres.setAidesCPAM(aidesCPAM);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPensionInvaliditeEtASIToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-pension-invalidite-et-asi.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(800);
	salaire.setMontantBrut(1038);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	aidesCPAM.setAllocationSupplementaireInvalidite(200f);
	ressourcesFinancieres.setAidesCPAM(aidesCPAM);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRSAToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-rsa.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	situationFamiliale.setIsSeulPlusDe18Mois(true);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireRSA(true);
	demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	aidesCAF.setAllocationRSA(400f);
	aidesCAF.setProchaineDeclarationTrimestrielle(3);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAREToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-are.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireARE(true);
	demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	AidesPoleEmploi aidesPE = createAidesPE();
	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setAllocationJournaliereBrute(37f);
	allocationARE.setSalaireJournalierReferenceBrut(48f);
	allocationARE.setNombreJoursRestants(65f);
	aidesPE.setAllocationARE(allocationARE);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPE);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurInterimToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-interim.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.INTERIM.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurIAEToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-iae.json");

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.IAE.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }
}
