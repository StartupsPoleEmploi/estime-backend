package fr.poleemploi.estime.utile.externes.clients.mappeurs.periodes.ass;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.github.tsohr.JSONException;
import com.github.tsohr.JSONObject;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPeriode;
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.test.utile.BouchonAideSociale;
import fr.poleemploi.test.utile.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class OpenFiscaMappeurPeriodeAideSocialASSTests {
    
    @Autowired
    private BouchonAideSociale bouchonAideSociale;
    
    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;
    
    @Autowired
    TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
   
    @Test
    void creerPeriodeAideSocialeASSTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/periode-ass-cumul-3-mois-ass-salaire-simulation-mois-2.json");
        
        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 2;
        
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideASS, 0));       
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);                 
        
        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeASSTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/periode-ass-cumul-2-mois-ass-salaire-simulation-mois-6.json");
        
        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 6;

        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideASS, 0));   
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-12-2020"), codeAideASS, 0)); 
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-01-2021"), codeAideASS, 0)); 
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-02-2021"), codeAideASS, 0)); 
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-03-2021"), codeAideASS, 0)); 
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        
        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeASSTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/periode-ass-cumul-2-mois-ass-salaire-simulation-mois-3.json");
        
        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 3;
    
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();        
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideASS, 0));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-12-2020"), codeAideASS, 0));     
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        
        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeASSTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/periode-ass-cumul-1-mois-ass-salaire-simulation-mois-4.json");
        
        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 4;
        
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideASS, 507));       
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-12-2020"), codeAideASS, 0));    
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-01-2021"), codeAideASS, 0));    
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        
        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    @Test
    void creerPeriodeAideSocialeASSTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/ass/periode-ass-cumul-0-mois-ass-salaire-simulation-mois-5.json");
        
        String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
        LocalDate dateDebutSimulation = testUtile.getDate("01-10-2020");
        int numeroMoisSimule = 5;
        
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiPeriodeAideSocialeTests();
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle>  simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(creerSimulationMensuelle(dateDebutSimulation, codeAideASS, 524));
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-11-2020"), codeAideASS, 507));       
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-12-2020"), codeAideASS, 524));    
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-01-2021"), codeAideASS, 0));   
        simulationsMensuelles.add(creerSimulationMensuelle(testUtile.getDate("01-02-2021"), codeAideASS, 0));   
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);               
        
        JSONObject periodeAideSocialeASS = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeASS.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    
    
    private DemandeurEmploi creerDemandeurEmploiPeriodeAideSocialeTests() {
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setDistanceKmDomicileTravail(15);
        futurTravail.setNombreHeuresTravailleesSemaine(35);
        futurTravail.setNombreTrajetsDomicileTravail(20);
        Salaire salaire = new Salaire();
        salaire.setMontantNet(950);
        salaire.setMontantBrut(1228);
        futurTravail.setSalaire(salaire);
        futurTravail.setTypeContrat(TypesContratTravail.CDI.toString());
        demandeurEmploi.setFuturTravail(futurTravail);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        return demandeurEmploi;
    }
    
    private SimulationMensuelle creerSimulationMensuelle(LocalDate dateDebutSimulation, String codeAideASS, float montantASS) {
        SimulationMensuelle simulationMensuelleMois = new SimulationMensuelle();
        simulationMensuelleMois.setDatePremierJourMoisSimule(dateDebutSimulation);
        HashMap<String, AideSociale> aidesEligiblesPourMois1 = new HashMap<>();
        if(montantASS > 0) {
            AideSociale aideSocialeASSMois1 = bouchonAideSociale.getAideSocialeASS(montantASS);
            aidesEligiblesPourMois1.put(codeAideASS, aideSocialeASSMois1);            
        }
        simulationMensuelleMois.setMesAides(aidesEligiblesPourMois1);
        return simulationMensuelleMois;
    }
    

}
