package testsunitaires.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_BASE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_IMPOSABLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

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

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPeriode;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurPeriodesTestsRSA extends Commun {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    /***************** TESTS SUR  creerPeriodesSalaireDemandeur ********************************************************/

    /** CONTEXTE DES TESTS
     * ******** date demande simulation : 01-06-2020  ********************************
     * 
     * -- avant simulation -------------période de la simulation sur 6 mois ------------
     *    M-1        M            M1         M2       M3         M4       M5        M6
     *  05/2020   06/2020      07/2020   08/2020   09/2020   10/2020   11/2020   12/2020
     * @throws ParseException 
     ********************************************************************************/

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = utileTests.getDate("01-07-2020");
    }

    /**
     * Demandeur RSA, déclaration en M, calcul RSA en M3
     * a travaillé avant simulation en juillet et juin
     * 
     */
    @Test
    void mapPeriodeSalaireTest1() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-imposable-declarationM0-travaille-2-mois-simulation-mois-3.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-base-declarationM0-travaille-2-mois-simulation-mois-3.json");

	int numeroMoisSimulation = 3;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(0);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(380, 508, 2));

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur RSA, déclaration en M1, calcul RSA en M1
     * a travaillé avant simulation en juillet et juin
     * 
     */
    @Test
    void mapPeriodeSalaireTest2() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-imposable-declarationM1-travaille-2-mois-simulation-mois-1.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-base-declarationM1-travaille-2-mois-simulation-mois-1.json");

	int numeroMoisSimulation = 1;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(1);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(380, 508, 2));

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur RSA, déclaration en M1, calcul RSA en M4
     * a travaillé avant simulation en juillet et juin
     * 
     */
    @Test
    void mapPeriodeSalaireTest3() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-imposable-declarationM1-travaille-2-mois-simulation-mois-4.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-base-declarationM1-travaille-2-mois-simulation-mois-4.json");

	int numeroMoisSimulation = 4;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(1);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(380, 508, 2));

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur RSA, déclaration en M2, calcul RSA en M2
     * a travaillé avant simulation en juillet et juin
     * 
     */
    @Test
    void mapPeriodeSalaireTest4() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-imposable-declarationM2-travaille-2-mois-simulation-mois-2.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-base-declarationM2-travaille-2-mois-simulation-mois-2.json");

	int numeroMoisSimulation = 2;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(2);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(380, 508, 2));

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur RSA, déclaration en M2, calcul RSA en M5
     * a travaillé avant simulation en juillet et juin
     * 
     */
    @Test
    void mapPeriodeSalaireTest5() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-imposable-declarationM2-travaille-2-mois-simulation-mois-5.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-base-declarationM2-travaille-2-mois-simulation-mois-5.json");

	int numeroMoisSimulation = 5;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(2);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(380, 508, 2));

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur RSA, déclaration en M3
     * a travaillé avant simulation en juillet et juin
     * 
     */
    @Test
    void mapPeriodeSalaireTest6() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-imposable-declarationM3-travaille-2-mois-simulation-mois-3.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-base-declarationM3-travaille-2-mois-simulation-mois-3.json");

	int numeroMoisSimulation = 3;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(3);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(380, 508, 2));

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur RSA, déclaration en M3
     * a travaillé avant simulation en juillet et juin
     * 
     */
    @Test
    void mapPeriodeSalaireTest7() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-imposable-declarationM3-travaille-2-mois-simulation-mois-6.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsRSA/salaire/salaire-base-declarationM3-travaille-2-mois-simulation-mois-6.json");

	int numeroMoisSimulation = 6;

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(3);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(380, 508, 2));

	JSONObject demandeurJSON = new JSONObject();
	openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);

	assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

}
