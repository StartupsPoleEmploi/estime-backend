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
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import utile.tests.UtileDemandeurTests;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class OpenFiscaMappeurPeriodeTestsAAH extends CommunTests {
    
    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;
    
    @Autowired
    private UtileTests testUtile;
    
    @Autowired
    private UtileDemandeurTests utileDemandeurTests;
    
    private LocalDate dateDebutSimulation;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @BeforeEach
    void initBeforeTest() throws ParseException {        
        dateDebutSimulation = testUtile.getDate("01-07-2020");
    }
    
    
    /******************** TESTS SUR creerPeriodesAideSociale ************************************************/
   
    @Test
    void creerPeriodeAideSocialeAAHTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/periode-aah-non-travaille-6-derniers-mois-numero-mois-2.json");
        
        String codeAideAAH = AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 2;
        
        float montantAAH = 900;
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests(montantAAH);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideAAH, montantAAH));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideAAH, montantAAH));       
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        
        JSONObject periodeAideSocialeAAH = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeAAH.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeAAHTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/periode-aah-non-travaille-6-derniers-mois-numero-mois-5.json");
        
        String codeAideAAH = AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 5;
    
        float montantAAH = 900;
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests(montantAAH);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();        
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideAAH, montantAAH));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideAAH, montantAAH));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-12-2020"), codeAideAAH, montantAAH));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-01-2021"), codeAideAAH, montantAAH));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-02-2021"), codeAideAAH, montantAAH));        
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        
        JSONObject periodeAideSocialeAAH = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeAAH.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeAAHTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/periode-aah-6-mois-travaille-6-derniers-mois-numero-mois-2.json");
        
        String codeAideAAH = AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 2;
        
        float montantAAH = 900;
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests(montantAAH);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();
        float montantAAHReduit = 180;
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideAAH, montantAAHReduit));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideAAH, montantAAHReduit));       
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        
        JSONObject periodeAideSocialeAAH = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeAAH.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeAAHTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/periode-aah-6-mois-travaille-6-derniers-mois-numero-mois-5.json");
        
        String codeAideAAH = AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 5;
    
        float montantAAH = 900;
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests(montantAAH);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();        
        float montantAAHReduit = 180;
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideAAH, montantAAHReduit));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideAAH, montantAAHReduit));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-12-2020"), codeAideAAH, montantAAHReduit));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-01-2021"), codeAideAAH, montantAAHReduit));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-02-2021"), codeAideAAH, montantAAHReduit));        
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        
        JSONObject periodeAideSocialeAAH = openFiscaMappeurPeriode.creerPeriodesAideSociale(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeAAH.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    
    /************** *****************/
    
    /** CONTEXTE DES TESTS
     * ******** date demande simulation : 01-06-2020  ********************************
     * 
     * -- avant simulation -------------période de la simulation sur 6 mois ------------
     *    M-1        M            M1         M2       M3         M4       M5        M6
     *  05/2020   06/2020      07/2020   08/2020   09/2020   10/2020   11/2020   12/2020
     * @throws ParseException 
     ********************************************************************************/

  
    /**
     * Demandeur AAH non travaille dans les 6 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest1() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-non-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-non-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);       
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
 
    /**
     * Demandeur AAH non travaille dans les 6 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest2() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-non-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-non-travaille-avant-simulation-mois-5.json");
                
        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);   
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 6 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest3() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-6mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-6mois-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(6);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 6 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest4() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-6mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-6mois-travaille-avant-simulation-mois-5.json");
                
        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(6);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 5 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest5() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-5mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-5mois-travaille-avant-simulation-mois-2.json");
                
        int numeroMoisSimulation = 2;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(5);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);        
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 5 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest6() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-5mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-5mois-travaille-avant-simulation-mois-5.json");
                
        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(5);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);               
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 4 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest7() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-4mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-4mois-travaille-avant-simulation-mois-2.json");
                
        int numeroMoisSimulation = 2;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(4);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);  
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 4 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest8() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-4mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-4mois-travaille-avant-simulation-mois-5.json");
                
        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(4);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);  
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 3 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest9() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-3mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-3mois-travaille-avant-simulation-mois-5.json");
                
        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(3);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);          
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 3 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest10() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-3mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-3mois-travaille-avant-simulation-mois-2.json");
                
        int numeroMoisSimulation = 2;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(3);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);          
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 2 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest11() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-2mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-2mois-travaille-avant-simulation-mois-5.json");
                
        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(2);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);          
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    
    /**
     * Demandeur AAH a travaillé 2 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest12() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-2mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-2mois-travaille-avant-simulation-mois-2.json");
                
        int numeroMoisSimulation = 2;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(2);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);          
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 1 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest13() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-1mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-1mois-travaille-avant-simulation-mois-5.json");
                
        int numeroMoisSimulation = 5;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(1);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);        
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 1 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest14() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-imposable-1mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodeTestsAAH/salaire/salaire-base-1mois-travaille-avant-simulation-mois-2.json");
                
        int numeroMoisSimulation = 2;

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(900f);    
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(1);   
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(0);
        salairesMoisMoins1Mois.setMontantBrut(0);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);  
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeur(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }   
    
    
    /***************** METHODES UTILES POUR TESTS  ********/
    
    
    private DemandeurEmploi creerDemandeurEmploiPeriodeAideSocialeTests(float montantAAH) throws ParseException {
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetAAH(montantAAH);        
        return demandeurEmploi;
    }
    
    private SimulationMensuelle creerSimulationMensuelle(LocalDate dateDebutSimulation, String codeAideAAH, float montantAAH) {
        SimulationMensuelle simulationMensuelleMois = new SimulationMensuelle();
        simulationMensuelleMois.setDatePremierJourMoisSimule(dateDebutSimulation);
        HashMap<String, AideSociale> aidesEligiblesPourMois1 = new HashMap<>();
        AideSociale aideSocialeAAHMois1 = getAideSocialeAAH(montantAAH);
        aidesEligiblesPourMois1.put(codeAideAAH, aideSocialeAAHMois1);
        simulationMensuelleMois.setMesAides(aidesEligiblesPourMois1);
        return simulationMensuelleMois;
    }
    

}
