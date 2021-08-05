package testsintegration.simulation.montants.openfisca.primeactivite;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationsCAF;
import fr.poleemploi.estime.services.ressources.PrestationsCPAM;
import fr.poleemploi.estime.services.ressources.PrestationsFamiliales;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurASSCelibatairePensionInvalidite extends CommunTests {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;
    
    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

    /*****************************************  célibataire  ***************************************************************/

    @Test
    void calculerPrimeActivitePensionInvaliditeTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 0 enfant à charge, 
        //ASS de 16.89€ journalière
        //pension d'invalidité 200€ par mois, 
        //futur contrat CDI avec salaire net 800€/mois
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        
        PrestationsCPAM prestationsCPAM = new PrestationsCPAM();
        prestationsCPAM.setPensionInvalidite(200f);
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCPAM(prestationsCPAM);

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("25-01-2021");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 06/2021 est de 32€ (résultat simulateur CAF : 30€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(32);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 1 enfant à charge de 4ans 
        //ASS de 16.89€ journalière
        //pension d'invalidité 200€ par mois, 
        //futur contrat CDI avec salaire net 800€/mois
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);  
        
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(0);
        prestationsFamiliales.setAllocationSoutienFamilial(117);
        prestationsFamiliales.setComplementFamilial(0);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales);  
        prestationsCAF.setAllocationsLogementMensuellesNetFoyer(utileTests.creerAllocationsLogementMensuellesNetFoyer(150));
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCAF(prestationsCAF);
        
        PrestationsCPAM prestationsCPAM = new PrestationsCPAM();
        prestationsCPAM.setPensionInvalidite(200f);
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCPAM(prestationsCPAM);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("25-01-2021");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //TODO montant : écart important avec CAF
        //Alors le montant de la prime d'activité pour le 06/2021 est de 49€ (résultat simulateur CAF : 83€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(49);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 2 enfants à charge de 4 ans et 5 ans, 
        //ASS de 16.89€ journalière
        //asf 233€, af 133€, apl 150€, pension invalidité 200€
        //futur contrat CDI avec salaire net 800€/mois
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        
        PrestationsCPAM prestationsCPAM = new PrestationsCPAM();
        prestationsCPAM.setPensionInvalidite(200f);
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCPAM(prestationsCPAM);
        
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(134);
        prestationsFamiliales.setAllocationSoutienFamilial(233);
        prestationsFamiliales.setComplementFamilial(0);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales);  
        prestationsCAF.setAllocationsLogementMensuellesNetFoyer(utileTests.creerAllocationsLogementMensuellesNetFoyer(150));
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCAF(prestationsCAF);       
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("25-01-2021");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        //TODO montant : écart moyen avec CAF
        //Alors le montant de la prime d'activité pour le 06/2021 est de 23€ (résultat simulateur CAF : 0€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(23);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 0 enfant à charge, 
        //ASS de 16.89€ journalière
        //pension d'invalidité 200€ par mois, asi 200€ par mois
        //futur contrat CDI avec salaire net 800€/mois
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        
        PrestationsCPAM prestationsCPAM = new PrestationsCPAM();
        prestationsCPAM.setPensionInvalidite(200f);
        prestationsCPAM.setAllocationSupplementaireInvalidite(200f);
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCPAM(prestationsCPAM);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("25-01-2021");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        //TODO Montant KO
        //Alors le montant de la prime d'activité pour le 06/2021 est de 0€ (résultat simulateur CAF : 0€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }
}
