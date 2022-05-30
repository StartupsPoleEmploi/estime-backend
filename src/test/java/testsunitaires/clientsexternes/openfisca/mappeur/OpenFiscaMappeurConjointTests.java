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
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurConjointTests extends Commun {

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
    void mapDemandeurAvecConjointAAHToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-aah.json");

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
	situationFamiliale.setIsEnCouple(true);
	Personne conjoint = new Personne();
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(true);
	conjoint.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAFConjoint = createAidesCAF();
	aidesCAFConjoint.setAllocationAAH(900f);
	ressourcesFinancieresConjoint.setAidesCAF(aidesCAFConjoint);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	situationFamiliale.setConjoint(conjoint);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointASSToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-ass.json");

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
	situationFamiliale.setIsEnCouple(true);
	Personne conjoint = new Personne();
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireASS(true);
	conjoint.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationMensuelleNet(900f);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	ressourcesFinancieresConjoint.setAidesPoleEmploi(aidesPoleEmploi);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	situationFamiliale.setConjoint(conjoint);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointAREToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-are.json");

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
	situationFamiliale.setIsEnCouple(true);
	Personne conjoint = new Personne();
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireARE(true);
	conjoint.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setAllocationMensuelleNet(900f);
	aidesPoleEmploi.setAllocationARE(allocationARE);
	ressourcesFinancieresConjoint.setAidesPoleEmploi(aidesPoleEmploi);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	situationFamiliale.setConjoint(conjoint);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointPensionInvaliditeToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-pension-invalidite.json");

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
	situationFamiliale.setIsEnCouple(true);
	Personne conjoint = new Personne();
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	ressourcesFinancieresConjoint.setAidesCPAM(aidesCPAM);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	situationFamiliale.setConjoint(conjoint);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointSalaireToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-salaire.json");

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
	situationFamiliale.setIsEnCouple(true);
	Personne conjoint = new Personne();
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	Salaire salaireConjoint = new Salaire();
	salaireConjoint.setMontantNet(1200);
	salaireConjoint.setMontantBrut(1544);
	futurTravail.setSalaire(salaire);
	ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
	ressourcesFinancieresConjoint.setHasTravailleAuCoursDerniersMois(true);
	ressourcesFinancieresConjoint.setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1200, 1544, 13));
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	situationFamiliale.setConjoint(conjoint);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointRevenusLocatifsToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-revenus-locatifs.json");

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
	situationFamiliale.setIsEnCouple(true);
	Personne conjoint = new Personne();

	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setRevenusImmobilier3DerniersMois(3000f);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	situationFamiliale.setConjoint(conjoint);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointPensionRetraiteToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-pension-retraite.json");

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
	situationFamiliale.setIsEnCouple(true);
	Personne conjoint = new Personne();

	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setPensionRetraite(1000f);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	situationFamiliale.setConjoint(conjoint);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }
}
