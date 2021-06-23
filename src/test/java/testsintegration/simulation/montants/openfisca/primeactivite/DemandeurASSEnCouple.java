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
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.UtileTests;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurASSEnCouple extends CommunTests {
    
    private static final int NUMERA_MOIS_SIMULE_PPA = 5;

    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Autowired
    private UtileTests utileTests;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /*****************************************  en couple  ***************************************************************/

    @Test
    void calculerPrimeActiviteEnCoupleTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 0 enfant, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 85 euros (résultat simulateur CAF : 81 euros)
        assertThat(montantPrimeActivite).isEqualTo(85);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 0 enfant, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 1900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1900);
        salaire.setMontantBrut(2428);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");        

        //Lorsque je calcul le montant de la prime d'activité avec salaires 07/2020 = 1900, 08/2020 = 1900, 09/2020 = 1900 
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 0 euros (résultat simulateur CAF : pas droit à cette prestation)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 1 enfant à charge de 6ans, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, 6);         
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 140 euros (résultat simulateur CAF : 136 euros)
        assertThat(montantPrimeActivite).isEqualTo(140);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //2 enfants à charge de 6ans et 8ans, af = 132, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, 6);   
        utileTests.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(132);
        ressourcesFinancieresDemandeur.setAllocationsCAF(allocationsCAF);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
    
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 152 euros (résultat simulateur CAF : 147 euros)
        assertThat(montantPrimeActivite).isEqualTo(152);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //2 enfants à charge de 6ans et 8ans, af = 132, apl = 20 euros, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, 6);   
        utileTests.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(132);
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(20);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(20);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(20);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(allocationsLogementMensuellesNetFoyer);
        ressourcesFinancieresDemandeur.setAllocationsCAF(allocationsCAF);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
    
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 145 euros (résultat simulateur CAF : 141 euros)
        assertThat(montantPrimeActivite).isEqualTo(145);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //3 enfants à charge de 4ans, 6ans et 8ans, af = 473 euros,
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, 4);  
        utileTests.createPersonne(personnesACharge, 6);   
        utileTests.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(473);
        ressourcesFinancieresDemandeur.setAllocationsCAF(allocationsCAF);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
    
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 111 euros (résultat simulateur CAF : 107 euros)
        assertThat(montantPrimeActivite).isEqualTo(111);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //3 enfants à charge de 4ans, 6ans et 8ans, af = 473 euros, apl = 140 euros, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, 4);  
        utileTests.createPersonne(personnesACharge, 6);   
        utileTests.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(473);
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(140);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(140);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(140);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(allocationsLogementMensuellesNetFoyer);
        ressourcesFinancieresDemandeur.setAllocationsCAF(allocationsCAF);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
    
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 64 euros (résultat simulateur CAF : 61 euros)
        assertThat(montantPrimeActivite).isEqualTo(64);
    }
    
    @Test
    void calculerPrimeActiviteEnCoupleTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af = 729 euros, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, 4);  
        utileTests.createPersonne(personnesACharge, 6);   
        utileTests.createPersonne(personnesACharge, 8); 
        utileTests.createPersonne(personnesACharge, 12); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(729);
        ressourcesFinancieresDemandeur.setAllocationsCAF(allocationsCAF);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
    
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 128 euros (résultat simulateur CAF : 96 euros - 112 euros sans CF)
        assertThat(montantPrimeActivite).isEqualTo(128);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest9() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af = 729 euros, apl = 230 euros, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, 4);  
        utileTests.createPersonne(personnesACharge, 6);   
        utileTests.createPersonne(personnesACharge, 8); 
        utileTests.createPersonne(personnesACharge, 12); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieresDemandeur = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(729);
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(230);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(230);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(230);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(allocationsLogementMensuellesNetFoyer);
        ressourcesFinancieresDemandeur.setAllocationsCAF(allocationsCAF);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieresDemandeur.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieresDemandeur);
    
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 73 euros (résultat simulateur CAF : 41 )
        assertThat(montantPrimeActivite).isEqualTo(73);
    }
}