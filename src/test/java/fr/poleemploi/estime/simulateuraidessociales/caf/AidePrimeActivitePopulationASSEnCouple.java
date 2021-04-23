package fr.poleemploi.estime.simulateuraidessociales.caf;

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
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.test.utile.TestUtile;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class AidePrimeActivitePopulationASSEnCouple {
    
    private static final int NUMERA_MOIS_SIMULE_PPA = 5;

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
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
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 82 euros (résultat simulateur CAF : 81 euros)
        assertThat(montantPrimeActivite).isEqualTo(82);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 0 enfant, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros, 
        //futur contrat CDI avec salaire net 1900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1900);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");        

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
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 6);         
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 137 euros (résultat simulateur CAF : 136 euros)
        assertThat(montantPrimeActivite).isEqualTo(137);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //2 enfants à charge de 6ans et 8ans, af = 132, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 6);   
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 148 euros (résultat simulateur CAF : 147 euros)
        assertThat(montantPrimeActivite).isEqualTo(148);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //2 enfants à charge de 6ans et 8ans, af = 132, apl = 20 euros, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 6);   
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 142 euros (résultat simulateur CAF : 141 euros)
        assertThat(montantPrimeActivite).isEqualTo(142);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //3 enfants à charge de 4ans, 6ans et 8ans, af = 473 euros,
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 4);  
        testUtile.createPersonne(personnesACharge, 6);   
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 108 euros (résultat simulateur CAF : 107 euros)
        assertThat(montantPrimeActivite).isEqualTo(108);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //3 enfants à charge de 4ans, 6ans et 8ans, af = 473 euros, apl = 140 euros, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 4);  
        testUtile.createPersonne(personnesACharge, 6);   
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 61 euros (résultat simulateur CAF : 61 euros)
        assertThat(montantPrimeActivite).isEqualTo(61);
    }
    
    @Test
    void calculerPrimeActiviteEnCoupleTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af = 729 euros, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 4);  
        testUtile.createPersonne(personnesACharge, 6);   
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 12); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 125 euros (résultat simulateur CAF : 96 euros - 112 euros sans CF)
        assertThat(montantPrimeActivite).isEqualTo(125);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest9() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200 euros, 
        //4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af = 729 euros, apl = 230 euros, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6 euros | M3(08/2020) = 523,6 euros | M4(09/2020) = 0 euros,
        //futur contrat CDI avec salaire net 900 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 4);  
        testUtile.createPersonne(personnesACharge, 6);   
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 12); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaireNet(1200);
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
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 70 euros (résultat simulateur CAF : 41 )
        assertThat(montantPrimeActivite).isEqualTo(70);
    }
}