package testsunitaires.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_BASE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_IMPOSABLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.github.tsohr.JSONObject;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPeriode;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurPeriodeTestsASS extends Commun {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    /********************
     * TESTS SUR creerPeriodesAidee
     ************************************************/

    @Test
    void creerPeriodeAideeASSTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-3-mois-ass-salaire-simulation-mois-2.json");

	String codeAideASS = AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
	LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
	int numeroMoisSimule = 2;

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideeTests();

	SimulationAides simulationAides = new SimulationAides();
	List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
	simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 0));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 0));
	simulationAides.setSimulationsMensuelles(simulationsMensuelles);

	JSONObject periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesAide(demandeurEmploi, simulationAides, codeAideASS, dateDebutSimulation, numeroMoisSimule);

	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeASSTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-2-mois-ass-salaire-simulation-mois-6.json");

	String codeAideASS = AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
	LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
	int numeroMoisSimule = 6;

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideeTests();

	SimulationAides simulationAides = new SimulationAides();
	List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
	simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 0));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-12-2020"), codeAideASS, 0));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-01-2021"), codeAideASS, 0));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-02-2021"), codeAideASS, 0));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-03-2021"), codeAideASS, 0));
	simulationAides.setSimulationsMensuelles(simulationsMensuelles);
	simulationAides.setSimulationsMensuelles(simulationsMensuelles);

	JSONObject periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesAide(demandeurEmploi, simulationAides, codeAideASS, dateDebutSimulation, numeroMoisSimule);

	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeASSTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-2-mois-ass-salaire-simulation-mois-3.json");

	String codeAideASS = AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
	LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
	int numeroMoisSimule = 3;

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideeTests();

	SimulationAides simulationAides = new SimulationAides();
	List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
	simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 0));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-12-2020"), codeAideASS, 0));
	simulationAides.setSimulationsMensuelles(simulationsMensuelles);

	JSONObject periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesAide(demandeurEmploi, simulationAides, codeAideASS, dateDebutSimulation, numeroMoisSimule);

	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeASSTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-1-mois-ass-salaire-simulation-mois-4.json");

	String codeAideASS = AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
	LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
	int numeroMoisSimule = 4;

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideeTests();

	SimulationAides simulationAides = new SimulationAides();
	List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
	simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 507));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-12-2020"), codeAideASS, 0));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-01-2021"), codeAideASS, 0));
	simulationAides.setSimulationsMensuelles(simulationsMensuelles);
	simulationAides.setSimulationsMensuelles(simulationsMensuelles);

	JSONObject periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesAide(demandeurEmploi, simulationAides, codeAideASS, dateDebutSimulation, numeroMoisSimule);

	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeASSTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-0-mois-ass-salaire-simulation-mois-5.json");

	String codeAideASS = AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
	LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
	int numeroMoisSimule = 5;

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideeTests();

	SimulationAides simulationAides = new SimulationAides();
	List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
	simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 507));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-12-2020"), codeAideASS, 524));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-01-2021"), codeAideASS, 0));
	simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-02-2021"), codeAideASS, 0));
	simulationAides.setSimulationsMensuelles(simulationsMensuelles);
	simulationAides.setSimulationsMensuelles(simulationsMensuelles);

	JSONObject periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesAide(demandeurEmploi, simulationAides, codeAideASS, dateDebutSimulation, numeroMoisSimule);

	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    /*****************
     * TESTS SUR creerPeriodesSalaireDemandeur
     ********************************************************/

    /**
     * CONTEXTE DES TESTS 
     * 
     * date demande simulation : 01-06-2020
     * 
     * avant simulation
     * M-2 05/2020       
     * M-1 06/2020 
     * 
     * période de la simulation sur 6 mois
     * M1 07/2020       
     * M2 08/2020   
     * M3 09/2020    
     * M4 10/2020
     * M5 11/2020
     * M6 12/2020
     *      
     *  
     ********************************************************************************/

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = utileTests.getDate("01-07-2020");
    }

    /**
     * Demandeur ASS sans cumul ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest1() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-sans-cumul-ass-salaire-simulation-mois-5.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-sans-cumul-ass-salaire-simulation-mois-5.json");

	int numeroMoisSimulation = 5;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 1 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M4
     * 
     */
    @Test
    void mapPeriodeSalaireTest2() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-1mois-ass-salaire-simulation-mois-4.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-1mois-ass-salaire-simulation-mois-4.json");

	int numeroMoisSimulation = 4;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);

	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = utileTests.creerPeriodeTravailleeAvantSimulation(850, 1101, 1);

	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 2 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M3
     * 
     */
    @Test
    void mapPeriodeSalaireTest3() throws Exception {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-2mois-ass-salaire-simulation-mois-3.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-2mois-ass-salaire-simulation-mois-3.json");

	int numeroMoisSimulation = 3;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);

	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = utileTests.creerPeriodeTravailleeAvantSimulation(850, 1101, 2);

	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 2 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M6
     * 
     */
    @Test
    void mapPeriodeSalaireTest4() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-2mois-ass-salaire-simulation-mois-6.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-2mois-ass-salaire-simulation-mois-6.json");

	int numeroMoisSimulation = 6;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);

	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = utileTests.creerPeriodeTravailleeAvantSimulation(850, 1101, 2);

	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 3 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest5() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-3mois-ass-salaire-simulation-mois-2.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-3mois-ass-salaire-simulation-mois-2.json");

	int numeroMoisSimulation = 2;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);

	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = utileTests.creerPeriodeTravailleeAvantSimulation(850, 1101, 3);

	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 3 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest6() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-3mois-ass-salaire-simulation-mois-5.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-3mois-ass-salaire-simulation-mois-5.json");

	int numeroMoisSimulation = 5;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);

	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = utileTests.creerPeriodeTravailleeAvantSimulation(850, 1101, 3);

	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /***************** METHODES UTILES POUR TESTS *******************************/

    private DemandeurEmploi creerDemandeurEmploiPeriodeAideeTests() throws ParseException {

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.toString());
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1228);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(950);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(15);
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

	return demandeurEmploi;
    }

    private SimulationMensuelle creerSimulationMensuelle(LocalDate dateDebutSimulation, String codeAideASS, float montantASS) {
	SimulationMensuelle simulationMensuelleMois = new SimulationMensuelle();
	simulationMensuelleMois.setDatePremierJourMoisSimule(dateDebutSimulation);
	HashMap<String, Aide> prestationsEligiblesPourMois1 = new HashMap<>();
	if (montantASS > 0) {
	    Aide allocationASSMois1 = getAideeASS(montantASS);
	    prestationsEligiblesPourMois1.put(codeAideASS, allocationASSMois1);
	}
	simulationMensuelleMois.setMesAides(prestationsEligiblesPourMois1);
	return simulationMensuelleMois;
    }

}
