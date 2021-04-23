package fr.poleemploi.estime.utile.externes.clients.mappeurs.periodes.aah;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPeriode;
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.test.utile.BouchonAideSociale;
import fr.poleemploi.test.utile.TestUtile;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureTestDatabase
class OpenFiscaMappeurPeriodeAideSocialAAHTests {
    
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
    void creerPeriodeAideSocialeAAHTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/periode-aah-non-travaille-6-derniers-mois-numero-mois-2.json");
        
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
        
        JSONObject periodeAideSocialeAAH = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeAAH.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeAAHTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/periode-aah-non-travaille-6-derniers-mois-numero-mois-5.json");
        
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
        
        JSONObject periodeAideSocialeAAH = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeAAH.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeAAHTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/periode-aah-6-mois-travaille-6-derniers-mois-numero-mois-2.json");
        
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
        
        JSONObject periodeAideSocialeAAH = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeAAH.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void creerPeriodeAideSocialeAAHTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur-periode/aah/periode-aah-6-mois-travaille-6-derniers-mois-numero-mois-5.json");
        
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
        
        JSONObject periodeAideSocialeAAH = openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule);
        
        assertThat(periodeAideSocialeAAH.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    
    private DemandeurEmploi creerDemandeurEmploiPeriodeAideSocialeTests(float montantAAH) {
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(montantAAH);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        return demandeurEmploi;
    }
    
    private SimulationMensuelle creerSimulationMensuelle(LocalDate dateDebutSimulation, String codeAideAAH, float montantAAH) {
        SimulationMensuelle simulationMensuelleMois = new SimulationMensuelle();
        simulationMensuelleMois.setDatePremierJourMoisSimule(dateDebutSimulation);
        HashMap<String, AideSociale> aidesEligiblesPourMois1 = new HashMap<>();
        AideSociale aideSocialeAAHMois1 = bouchonAideSociale.getAideSocialeAAH(montantAAH);
        aidesEligiblesPourMois1.put(codeAideAAH, aideSocialeAAHMois1);
        simulationMensuelleMois.setMesAides(aidesEligiblesPourMois1);
        return simulationMensuelleMois;
    }
    

}
