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
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsCPAM;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurASSCelibatairePensionInvalidite extends CommunTests {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;
    
    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private UtileTests testUtile;
    
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
        //futur contrat CDI avec salaire net 800 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCPAM(allocationsCPAM);

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("25-01-2021");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 06/2021 est de 32 euros (résultat simulateur CAF : TODO ldetre retrouver valeur CAF)
        assertThat(montantPrimeActivite).isEqualTo(32);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 1 enfant à charge de 1ans 
        //ASS de 16.89€ journalière
        //pension d'invalidité 200€ par mois, 
        //futur contrat CDI avec salaire net 800 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(1));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCPAM(allocationsCPAM);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("25-01-2021");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 06/2021 est de 74 euros (résultat simulateur CAF : 368€)
        assertThat(montantPrimeActivite).isEqualTo(74);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 2 enfants à charge de 1 ans et 1 ans, 
        //ASS de 16.89€ journalière
        //af = 1100 euros, apl = 150 euros, pension invalidité 200 euros
        //futur contrat CDI avec salaire net 800 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(1));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(1));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCPAM(allocationsCPAM);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1100);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(150));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);       
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("25-01-2021");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 06/2021 est de 86 euros (résultat simulateur CAF : pas le droit à cette prestation)
        assertThat(montantPrimeActivite).isEqualTo(86);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 0 enfant à charge, 
        //ASS de 16.89€ journalière
        //pension d'invalidité 200€ par mois, asi 200€ par mois
        //futur contrat CDI avec salaire net 800 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        allocationsCPAM.setAllocationSupplementaireInvalidite(200f);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCPAM(allocationsCPAM);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1100);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(150));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);       

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("25-01-2021");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 06/2021 est de 0 euros (résultat simulateur CAF : TODO ldetre retrouver valeur CAF)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
}
