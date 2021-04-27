package fr.poleemploi.estime.utile.externes.clients.mappeurs.periodes.ass;

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
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.test.utile.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class OpenFiscaMappeurPeriodeSalaireASSTests {
    
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
     * Demandeur ASS sans cumul ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest1() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/salaire/sans-cumul-ass-salaire-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(false);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1000);
        demandeurEmploi.setFuturTravail(futurTravail);
                
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur ASS cumul 1 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M4
     * 
     */
    @Test
    void mapPeriodeSalaireTest2() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/salaire/cumul-1mois-ass-salaire-simulation-mois-4.json");
        
        int numeroMoisSimulation = 4;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(1);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);

        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1000);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur ASS cumul 2 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M3
     * 
     */
    @Test
    void mapPeriodeSalaireTest3() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/salaire/cumul-2mois-ass-salaire-simulation-mois-3.json");
        
        int numeroMoisSimulation = 3;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(1200);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1000);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur ASS cumul 2 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M6
     * 
     */
    @Test
    void mapPeriodeSalaireTest4() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/salaire/cumul-2mois-ass-salaire-simulation-mois-6.json");
        
        int numeroMoisSimulation = 6;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);

        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1000);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur ASS cumul 3 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M2
     * 
     */
    @Test
    void mapPeriodeSalaireTest5() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/salaire/cumul-3mois-ass-salaire-simulation-mois-2.json");
        
        int numeroMoisSimulation = 2;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(3);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(1200);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(1150);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1000);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    /**
     * Demandeur ASS cumul 3 mois ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest6() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/salaire/cumul-3mois-ass-salaire-simulation-mois-5.json");
        
        int numeroMoisSimulation = 5;

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(3);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(1200);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1000);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        
        JSONObject periodeSalaire = openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimulation);
        
        assertThat(periodeSalaire.toString()).isEqualTo(openFiscaPayloadExpected);
    }
   
}
