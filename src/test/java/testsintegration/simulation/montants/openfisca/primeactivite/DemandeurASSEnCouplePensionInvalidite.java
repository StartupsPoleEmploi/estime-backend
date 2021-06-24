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
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurASSEnCouplePensionInvalidite extends CommunTests {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;
    
    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private UtileTests testUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

    @Test
    void calculerPrimeActivitePensionInvaliditeTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint 200 pension invalidite, 
        //ASS de 16.89€ journalière
        //futur contrat CDI avec salaire net 800 euros/mois
        boolean isEnCouple = true;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1100);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(150));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);  
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAMConjoint = new AllocationsCPAM();
        allocationsCPAMConjoint.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAllocationsCPAM(allocationsCPAMConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 
                
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 01/2021 est de 0 euros (résultat simulateur CAF : 368€ )
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint 200 pension invalidite, 
        //ASS de 16.89€ journalière
        // 1 enfant de 1 an
        //futur contrat CDI avec salaire net 800 euros/mois
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(1));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1100);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(150));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);  
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAMConjoint = new AllocationsCPAM();
        allocationsCPAMConjoint.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAllocationsCPAM(allocationsCPAMConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 01/2021 est de 0 euros (résultat simulateur CAF : 533€)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1000 euros et 200 pension invalidite, 
        //ASS de 16.89€ journalière
        //futur contrat CDI avec salaire net 800 euros/mois
        boolean isEnCouple = true;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("08-01-1979"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1100);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(150));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);  
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1000);
        salaireConjoint.setMontantBrut(1291);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        AllocationsCPAM allocationsCPAMConjoint = new AllocationsCPAM();
        allocationsCPAMConjoint.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAllocationsCPAM(allocationsCPAMConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);        

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 01/2021 est de 0 euros (résultat simulateur CAF : TODO ldetre : 83€)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1000 euros et 200 pension invalidite,
        // 1 enfant de 1 an,
        //futur contrat CDI avec salaire net 800 euros/mois
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("08-01-1979"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(1));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1100);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(150));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);  
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1000);
        salaireConjoint.setMontantBrut(1291);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        AllocationsCPAM allocationsCPAMConjoint = new AllocationsCPAM();
        allocationsCPAMConjoint.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAllocationsCPAM(allocationsCPAMConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);       

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 01/2021 est de 0 euros (résultat simulateur CAF : 274€)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
}
