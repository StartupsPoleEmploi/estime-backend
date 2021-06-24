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
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurASSCelibataire extends CommunTests {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;
    
    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

    @Test
    void calculerPrimeActiviteTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 0 enfant, 
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //futur contrat CDI, salaire net 900€
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        //Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 93 euros (résultat simulateur CAF : 93 euros)
        assertThat(montantPrimeActivite).isEqualTo(95);
    }

    @Test
    void calculerPrimeActiviteTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 0 enfant, 
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 1900 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(2428);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");        
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 0 euros (résultat simulateur CAF : pas droit à cette préstation)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }

    @Test
    void calculerPrimeActiviteTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 1 enfant à charge de 6ans, af = 110 euros, 
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros,
        //futur contrat CDI, salaire net 900€
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(110);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        //Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020"); 
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 156 euros (résultat simulateur CAF : 148 euros)
        assertThat(montantPrimeActivite).isEqualTo(156);
    }

    @Test
    void calculerPrimeActiviteTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 1 enfant à charge de 6ans, af = 110 euros, apl = 380, 
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //Futur contrat, CDI, salaire 900€ net
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(110);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(300));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);
       
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020"); 
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 112 euros (résultat simulateur CAF : 104 euros)
        assertThat(montantPrimeActivite).isEqualTo(112);
    }

    @Test
    void calculerPrimeActiviteTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 2 enfants à charge de 6ans et 8ans, af = 362 euros, 
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 900 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(362);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020"); 
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 136 euros (résultat simulateur CAF : 119 euros)
        assertThat(montantPrimeActivite).isEqualTo(136);
    }

    @Test
    void calculerPrimeActiviteTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 2 enfants à charge de 6ans et 8ans, af = 362 euros, apl = 380 euros, 
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 900 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(362);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(380));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 81 euros (résultat simulateur CAF : 65 euros)
        assertThat(montantPrimeActivite).isEqualTo(81);
    }

    @Test
    void calculerPrimeActiviteTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 3 enfants à charge de 4ans, 6ans et 8ans, af = 899 euros
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 900 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(899);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);
                
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 65 euros (résultat simulateur CAF : 15 euros - 100 euros sans compter CF)
        assertThat(montantPrimeActivite).isEqualTo(65);
    }

    @Test
    void calculerPrimeActiviteTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 3 enfants à charge de 4ans, 6ans et 8ans, af = 899 euros, apl = 450 euros, 
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 900 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(899);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(450));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 0 euros (résultat simulateur CAF : pas droit à cette prestation)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActiviteTest9() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af = 1189 euros
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 900 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1189);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);        
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 51 euros (résultat simulateur CAF : pas droit à cette prestation euros)
        assertThat(montantPrimeActivite).isEqualTo(51);
    }

    @Test
    void calculerPrimeActiviteTest10() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, 
        //af = 1189 euros, apl = 520 euros, 
        //ass M1(07/2020)= 506,7 euros M2(08/2020) = 523,6 euros | M3(09/2020) = 523,6 euros | M4(10/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 900 euros/mois
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi =  utileDemandeurTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);        
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1189);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(utileDemandeurTests.creerAllocationsLogementMensuellesNetFoyer(520));
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);         
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 0 euros (résultat simulateur CAF :  pas droit à cette prestation)
        assertThat(montantPrimeActivite).isEqualTo(0);
    } 
}