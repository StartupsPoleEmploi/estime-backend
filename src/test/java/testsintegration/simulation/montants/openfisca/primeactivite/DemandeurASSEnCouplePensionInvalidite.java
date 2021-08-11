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
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurASSEnCouplePensionInvalidite extends CommunTests {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;
    
    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

    @Test
    void calculerPrimeActivitePensionInvaliditeTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, pas d'enfants, conjoint 200€ pension invalidite, 
        //ASS de 16.89€ journalière
        //futur contrat CDI avec salaire net 800€/mois
        boolean isEnCouple = true;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        AidesCPAM aidesCPAMConjoint = new AidesCPAM();
        aidesCPAMConjoint.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAidesCPAM(aidesCPAMConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 
                
        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //TODO montant : écart de 30€ avec CAF 
        //Alors le montant de la prime d'activité pour le 01/2021 est de 0€ (résultat simulateur CAF : 30€ )
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, 1 enfant de 4 ans, conjoint 200€ pension invalidite, 
        //ASS de 16.89€ journalière
        //futur contrat CDI avec salaire net 800€/mois
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        
        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(0);
        aidesFamiliales.setAllocationSoutienFamilial(0);
        aidesFamiliales.setComplementFamilial(0);
        aidesCAF.setAidesFamiliales(aidesFamiliales); 
        aidesCAF.setAllocationsLogement(utileTests.creerallocationsLogement(150));
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);  
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        AidesCPAM aidesCPAMConjoint = new AidesCPAM();
        aidesCPAMConjoint.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAidesCPAM(aidesCPAMConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //TODO montant : écart de 127€ avec CAF
        //Alors le montant de la prime d'activité pour le 01/2021 est de 0€ (résultat simulateur CAF : 127€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, conjoint salaire 1000€ et 200€ pension invalidite, 
        //ASS de 16.89€ journalière
        //futur contrat CDI 35h avec salaire net 800€/mois brut 1038€/mois
        boolean isEnCouple = true;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("08-01-1979"));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1000);
        salaireConjoint.setMontantBrut(1291);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        AidesCPAM aidesCPAMConjoint = new AidesCPAM();
        aidesCPAMConjoint.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAidesCPAM(aidesCPAMConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);        

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //Alors le montant de la prime d'activité pour le 01/2021 est de 0€ (résultat simulateur CAF : 26€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }
    
    @Test
    void calculerPrimeActivitePensionInvaliditeTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        //Si DE France Métropolitaine, en couple, 1 enfant de 4 ans, conjoint salaire 1000€ et 200€ pension invalidite, apl 150€
        // ASS de 16.89€ journalière
        //futur contrat CDI avec salaire net 800€/mois
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("08-01-1979"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(800);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1038);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);        
        
        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(0);
        aidesFamiliales.setAllocationSoutienFamilial(0);
        aidesFamiliales.setComplementFamilial(0);
        aidesCAF.setAidesFamiliales(aidesFamiliales); 
        aidesCAF.setAllocationsLogement(utileTests.creerallocationsLogement(150));
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);  
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1000);
        salaireConjoint.setMontantBrut(1291);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        AidesCPAM aidesCPAMConjoint = new AidesCPAM();
        aidesCPAMConjoint.setPensionInvalidite(200f);
        ressourcesFinancieresConjoint.setAidesCPAM(aidesCPAMConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint); 
        
        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);       

        //Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");  
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        //TODO montant : écart de 32€ avec CAF
        //Alors le montant de la prime d'activité pour le 01/2021 est de 0€ (résultat simulateur CAF : 32€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }
}
