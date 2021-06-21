package fr.poleemploi.estime.logique.simulateuraidessociales.caf;

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
import fr.poleemploi.estime.services.ressources.AllocationsCPAM;
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
import fr.poleemploi.test.utile.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AidePrimeActivitePopulationPensionInvalidite {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;
    
    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

    /*****************************************  célibataire  ***************************************************************/

    @Test
    void calculerPrimeActivitePensionInvaliditeTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, célibataire, 0 enfant à charge, 
        //ASS de 16.89€ journalière
        //pension d'invalidité 200€ par mois, 
        //futur contrat CDI avec salaire net 800 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setAllocationsCPAM(allocationsCPAM);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
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

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("25-01-2021");  

        //Lorsque je calcul le montant de la prime d'activité
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
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);


        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 1);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setAllocationsCPAM(allocationsCPAM);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("25-01-2021");  
        
        //Lorsque je calcul le montant de la prime d'activité
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
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);


        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 1);
        testUtile.createPersonne(personnesACharge, 1);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setAllocationsCPAM(allocationsCPAM);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(1100);
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(150);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(150);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(150);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(allocationsLogementMensuellesNetFoyer);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("25-01-2021");  

        //Lorsque je calcul le montant de la prime d'activité
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
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        allocationsCPAM.setAllocationSupplementaireInvalidite(200f);
        ressourcesFinancieres.setAllocationsCPAM(allocationsCPAM);
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
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

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("25-01-2021");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 06/2021 est de 0 euros (résultat simulateur CAF : TODO ldetre retrouver valeur CAF)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
    
    /*****************************************  en couple  ***************************************************************/

    

    @Test
    void calculerPrimeActivitePensionInvaliditeTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint 200 pension invalidite, 
        //ASS de 16.89€ journalière
        //futur contrat CDI avec salaire net 800 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        Personne conjoint = new Personne();
        situationFamiliale.setIsEnCouple(true);
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAllocationsCPAM(allocationsCPAM);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
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
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 1);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAllocationsCPAM(allocationsCPAM);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 01/2021 est de 0 euros (résultat simulateur CAF : 533€)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1000 euros et 200 pension invalidite, 
        //ASS de 16.89€ journalière
        //futur contrat CDI avec salaire net 800 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        Personne conjoint = new Personne();
        situationFamiliale.setIsEnCouple(true);
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1000);
        salaireConjoint.setMontantBrut(1291);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAllocationsCPAM(allocationsCPAM);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);        

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 01/2021 est de 0 euros (résultat simulateur CAF : TODO ldetre : 83€)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1000 euros et 200 pension invalidite,
        // 1 enfant de 1 an,
        //futur contrat CDI avec salaire net 800 euros/mois
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 1);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1000);
        salaireConjoint.setMontantBrut(1291);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAllocationsCPAM(allocationsCPAM);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(16.89f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(testUtile.createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("05-07-2020");  

        //Lorsque je calcul le montant de la prime d'activité
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 01/2021 est de 0 euros (résultat simulateur CAF : 274€)
        assertThat(montantPrimeActivite).isEqualTo(0);
    }
}
