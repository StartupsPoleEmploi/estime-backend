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
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SalaireAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurPeriodeTestsASS extends CommunTests {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    /********************
     * TESTS SUR creerPeriodesAideSociale
     ************************************************/

    @Test
    void creerPeriodeAideSocialeASSTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException,
            FileNotFoundException, URISyntaxException {

        String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-3-mois-ass-salaire-simulation-mois-2.json");

        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
        int numeroMoisSimule = 2;

        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi,
                simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);

        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideSocialeASSTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException,
            FileNotFoundException, URISyntaxException {

        String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-2-mois-ass-salaire-simulation-mois-6.json");

        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
        int numeroMoisSimule = 6;

        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-12-2020"), codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-01-2021"), codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-02-2021"), codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-03-2021"), codeAideASS, 0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi,
                simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);

        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideSocialeASSTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException,
            FileNotFoundException, URISyntaxException {

        String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-2-mois-ass-salaire-simulation-mois-3.json");

        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
        int numeroMoisSimule = 3;

        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-12-2020"), codeAideASS, 0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi,
                simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);

        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideSocialeASSTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException,
            FileNotFoundException, URISyntaxException {

        String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-1-mois-ass-salaire-simulation-mois-4.json");

        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
        int numeroMoisSimule = 4;

        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 507));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-12-2020"), codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-01-2021"), codeAideASS, 0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi,
                simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);

        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideSocialeASSTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException,
            FileNotFoundException, URISyntaxException {

        String openFiscaPayloadExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/periode-ass-cumul-0-mois-ass-salaire-simulation-mois-5.json");

        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = utileTests.getDate("01-10-2020");
        int numeroMoisSimule = 5;

        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-11-2020"), codeAideASS, 507));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-12-2020"), codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-01-2021"), codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(utileTests.getDate("01-02-2021"), codeAideASS, 0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi,
                simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);

        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    /*****************
     * TESTS SUR creerPeriodesSalaireDemandeur
     ********************************************************/

    /**
     * CONTEXTE DES TESTS ******** date demande simulation : 01-06-2020
     * ********************************
     * 
     * -- avant simulation -------------période de la simulation sur 6 mois
     * ------------ M-1 M M1 M2 M3 M4 M5 M6 05/2020 06/2020 07/2020 08/2020 09/2020
     * 10/2020 11/2020 12/2020
     * 
     * @throws ParseException
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
    void mapPeriodeSalaireTest1() throws JSONException, JsonParseException, JsonMappingException, IOException,
            URISyntaxException, ParseException {

        String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-sans-cumul-ass-salaire-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-sans-cumul-ass-salaire-simulation-mois-5.json");

        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(),
                isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);

        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation,
                numeroMoisSimulation);

        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 1 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M4
     * 
     */
    @Test
    void mapPeriodeSalaireTest2() throws JSONException, JsonParseException, JsonMappingException, IOException,
            URISyntaxException, ParseException {

        String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-1mois-ass-salaire-simulation-mois-4.json");
        String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-1mois-ass-salaire-simulation-mois-4.json");

        int numeroMoisSimulation = 4;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(),
                isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(1);

        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation,
                numeroMoisSimulation);

        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 2 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M3
     * 
     */
    @Test
    void mapPeriodeSalaireTest3() throws JSONException, JsonParseException, JsonMappingException, IOException,
            URISyntaxException, ParseException {

        String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-2mois-ass-salaire-simulation-mois-3.json");
        String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-2mois-ass-salaire-simulation-mois-3.json");

        int numeroMoisSimulation = 3;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(),
                isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(2);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(1200);
        salaireMoisDemande.setMontantBrut(1544);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(1200);
        salaireMoisMoins1Mois.setMontantBrut(1544);
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation
                .setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0);
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation
                .setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation,
                numeroMoisSimulation);

        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 2 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M6
     * 
     */
    @Test
    void mapPeriodeSalaireTest4() throws JSONException, JsonParseException, JsonMappingException, IOException,
            URISyntaxException, ParseException {

        String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-2mois-ass-salaire-simulation-mois-6.json");
        String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-2mois-ass-salaire-simulation-mois-6.json");

        int numeroMoisSimulation = 6;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(),
                isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(2);

        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation,
                numeroMoisSimulation);

        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 3 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest5() throws JSONException, JsonParseException, JsonMappingException, IOException,
            URISyntaxException, ParseException {

        String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-3mois-ass-salaire-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-3mois-ass-salaire-simulation-mois-2.json");

        int numeroMoisSimulation = 2;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(),
                isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(1200);
        salaireMoisDemande.setMontantBrut(1544);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(1150);
        salaireMoisMoins1Mois.setMontantBrut(1480);
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation
                .setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0);
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation
                .setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation,
                numeroMoisSimulation);

        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }

    /**
     * Demandeur ASS cumul 3 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest6() throws JSONException, JsonParseException, JsonMappingException, IOException,
            URISyntaxException, ParseException {

        String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-imposable-cumul-3mois-ass-salaire-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
                "testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsASS/salaire/salaire-base-cumul-3mois-ass-salaire-simulation-mois-5.json");

        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(),
                isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1291);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1000);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(1200);
        salaireMoisDemande.setMontantBrut(1544);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(0);
        salaireMoisMoins1Mois.setMontantBrut(0);
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation
                .setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0);
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation
                .setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation,
                numeroMoisSimulation);

        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }

    /***************** METHODES UTILES POUR TESTS *******************************/

    private DemandeurEmploi creerDemandeurEmploiPeriodeAideSocialeTests() throws ParseException {

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(),
                isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.toString());
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1228);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(950);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(15);
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        return demandeurEmploi;
    }

    private SimulationMensuelle creerSimulationMensuelle(LocalDate dateDebutSimulation, String codeAideASS,
            float montantASS) {
        SimulationMensuelle simulationMensuelleMois = new SimulationMensuelle();
        simulationMensuelleMois.setDatePremierJourMoisSimule(dateDebutSimulation);
        HashMap<String, AideSociale> aidesEligiblesPourMois1 = new HashMap<>();
        if (montantASS > 0) {
            AideSociale aideSocialeASSMois1 = getAideSocialeASS(montantASS);
            aidesEligiblesPourMois1.put(codeAideASS, aideSocialeASSMois1);
        }
        simulationMensuelleMois.setMesAides(aidesEligiblesPourMois1);
        return simulationMensuelleMois;
    }

}
