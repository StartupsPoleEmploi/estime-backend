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
import fr.poleemploi.estime.services.ressources.PrestationsFamiliales;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurASSEnCouple extends CommunTests {
    
    private static final int NUMERA_MOIS_SIMULE_PPA = 5;

    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /*****************************************  en couple  ***************************************************************/

    @Test
    void calculerPrimeActiviteEnCoupleTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 0 enfant, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€, 
        //futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);        
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 85€ (résultat simulateur CAF : 81€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(85);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 0 enfant, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€, 
        //futur contrat CDI avec salaire net 1900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(2428);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);  
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité avec salaires 07/2020 = 1900, 08/2020 = 1900, 09/2020 = 1900 
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");        
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 0€ (résultat simulateur CAF : 0€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 1 enfant de 6ans, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€, 
        //futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 
            
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 140€ (résultat simulateur CAF : 136€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(140);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 
        //2 enfants à charge de 6ans et 8ans, af 132€, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        //futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);    
        
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(132);
        prestationsFamiliales.setAllocationSoutienFamilial(0);
        prestationsFamiliales.setComplementFamilial(0);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales);  
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCAF(prestationsCAF);
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 152€ (résultat simulateur CAF : 147€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(152);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 
        //2 enfants à charge de 6ans et 8ans, af 132€, apl 20€, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        //futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);    
        
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(132);
        prestationsFamiliales.setAllocationSoutienFamilial(0);
        prestationsFamiliales.setComplementFamilial(0);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales); 
        prestationsCAF.setAllocationsLogementMensuellesNetFoyer(utileTests.creerAllocationsLogementMensuellesNetFoyer(20));
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCAF(prestationsCAF);
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 11/2020 est de 145€ (résultat simulateur CAF : 141€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(145);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 
        //3 enfants à charge de 4ans, 6ans et 8ans, af 303€, cf 259€,
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        //futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);    
      
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(303);
        prestationsFamiliales.setAllocationSoutienFamilial(0);
        prestationsFamiliales.setComplementFamilial(259);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales); 
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCAF(prestationsCAF);
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 
    
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //TODO montant : écart de 34€ avec CAF
        //Alors le montant de la prime d'activité pour le 11/2020 est de 111€ (résultat simulateur CAF : 77€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(111);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 
        //3 enfants à charge de 4ans, 6ans et 8ans, af 303€, cf 259€, apl 140€, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        //futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);    
        
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(303);
        prestationsFamiliales.setAllocationSoutienFamilial(0);
        prestationsFamiliales.setComplementFamilial(259);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales); 
        prestationsCAF.setAllocationsLogementMensuellesNetFoyer(utileTests.creerAllocationsLogementMensuellesNetFoyer(140));
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCAF(prestationsCAF);
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres(    );
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);        

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //TODO montant : écart de 34€ avec CAF
        //Alors le montant de la prime d'activité pour le 11/2020 est de 64€ (résultat simulateur CAF : 30€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(64);
    }
    
    @Test
    void calculerPrimeActiviteEnCoupleTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 
        //4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af 472€, cf 259€, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        //futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);    
        
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(472);
        prestationsFamiliales.setAllocationSoutienFamilial(0);
        prestationsFamiliales.setComplementFamilial(259);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales); 
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCAF(prestationsCAF);
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 

        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //TODO montant : écart de 34€ avec CAF
        //Alors le montant de la prime d'activité pour le 11/2020 est de 128€ (résultat simulateur CAF : 94€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(128);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest9() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 
        //4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af 472€, cf 259€, apl 230€, 
        //ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        //futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);    
        
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(472);
        prestationsFamiliales.setAllocationSoutienFamilial(0);
        prestationsFamiliales.setComplementFamilial(259);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales); 
        prestationsCAF.setAllocationsLogementMensuellesNetFoyer(utileTests.creerAllocationsLogementMensuellesNetFoyer(230));
        demandeurEmploi.getRessourcesFinancieres().setPrestationsCAF(prestationsCAF);
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);        

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //TODO montant : écart de 34€ avec CAF
        //Alors le montant de la prime d'activité pour le 11/2020 est de 74€ (résultat simulateur CAF : 40€ )
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(74);
    }
}