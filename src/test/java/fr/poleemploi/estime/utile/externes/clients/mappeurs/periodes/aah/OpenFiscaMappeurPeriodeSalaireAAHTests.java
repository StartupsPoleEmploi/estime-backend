package fr.poleemploi.estime.utile.externes.clients.mappeurs.periodes.aah;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPeriode;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.test.utile.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class OpenFiscaMappeurPeriodeSalaireAAHTests {
    
    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;
    
    @Autowired
    TestUtile testUtile;
    
    private LocalDate dateDebutSimulation;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    
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
        dateDebutSimulation = testUtile.getDate("01-07-2020");
    }
    
    /**
     * Demandeur AAH non travaille dans les 6 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest1() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/non-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(0);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
 
    /**
     * Demandeur AAH non travaille dans les 6 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest2() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/non-travaille-avant-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(0);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 6 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest3() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/6mois-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(6);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 6 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest4() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/6mois-travaille-avant-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(6);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 5 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest5() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/5mois-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(5);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 5 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest6() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/5mois-travaille-avant-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(5);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 4 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest7() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/4mois-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(4);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 4 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest8() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/4mois-travaille-avant-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(4);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 3 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest9() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/3mois-travaille-avant-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 3 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest10() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/3mois-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 2 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest11() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/2mois-travaille-avant-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    
    /**
     * Demandeur AAH a travaillé 2 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest12() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/2mois-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 1 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest13() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/1mois-travaille-avant-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(1);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(800);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur AAH a travaillé 1 mois dans les 6 derniers mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest14() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/1mois-travaille-avant-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(1);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(850);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(0);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1200);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }   
}
