package fr.poleemploi.estime.utile.externes.clients.mappeurs.periodes.aah;

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
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
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
        
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-non-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-non-travaille-avant-simulation-mois-2.json");
        
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
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-non-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-non-travaille-avant-simulation-mois-5.json");
                
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
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-6mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-6mois-travaille-avant-simulation-mois-2.json");
        
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-6mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-6mois-travaille-avant-simulation-mois-5.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-5mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-5mois-travaille-avant-simulation-mois-2.json");
                
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
        Salaire salairesAvant = new Salaire();
        salairesAvant.setMontantNet(850);
        salairesAvant.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesAvant);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-5mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-5mois-travaille-avant-simulation-mois-5.json");
                
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
        Salaire salairesAvant = new Salaire();
        salairesAvant.setMontantNet(850);
        salairesAvant.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesAvant);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-4mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-4mois-travaille-avant-simulation-mois-2.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-4mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-4mois-travaille-avant-simulation-mois-5.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-3mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-3mois-travaille-avant-simulation-mois-5.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-3mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-3mois-travaille-avant-simulation-mois-2.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-2mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-2mois-travaille-avant-simulation-mois-5.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-2mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-2mois-travaille-avant-simulation-mois-2.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-1mois-travaille-avant-simulation-mois-5.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-1mois-travaille-avant-simulation-mois-5.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(800);
        salairesMoisMoins1Mois.setMontantBrut(1038);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
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
        
        String openFiscaPayloadSalaireImposableExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-imposable-1mois-travaille-avant-simulation-mois-2.json");
        String openFiscaPayloadSalaireBaseExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/salaire/salaire-base-1mois-travaille-avant-simulation-mois-2.json");
                
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
        Salaire salairesMoisDemande = new Salaire();
        salairesMoisDemande.setMontantNet(850);
        salairesMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salairesMoisDemande);
        Salaire salairesMoisMoins1Mois = new Salaire();
        salairesMoisMoins1Mois.setMontantNet(0);
        salairesMoisMoins1Mois.setMontantBrut(0);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salairesMoisMoins1Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1200);
        salaire.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        JSONObject demandeurJSON = new JSONObject();
        openFiscaMappeurPeriode.creerPeriodesSalaireDemandeurJSON(demandeurJSON, demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(demandeurJSON.get(SALAIRE_BASE).toString()).isEqualTo(openFiscaPayloadSalaireBaseExpected);
        assertThat(demandeurJSON.get(SALAIRE_IMPOSABLE).toString()).isEqualTo(openFiscaPayloadSalaireImposableExpected);
    }   
}
