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
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurASSCelibataire extends CommunTests {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void calculerPrimeActiviteTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 0 enfant,
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI, salaire net 900€
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 93€ (résultat simulateur CAF : 93€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(95);
    }

    @Test
    void calculerPrimeActiviteTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 0 enfant,
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI avec salaire net 1900€/mois
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(2428);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 0€ (résultat simulateur CAF : pas droit à cette préstation)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }

    @Test
    void calculerPrimeActiviteTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 1 enfant à charge de 6ans, asf 110€,
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI, salaire net 900€
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(0);
        aidesFamiliales.setAllocationSoutienFamilial(110);
        aidesFamiliales.setComplementFamilial(0);
        aidesCAF.setAidesFamiliales(aidesFamiliales);
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 158€ (résultat simulateur CAF : 148€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(158);
    }

    @Test
    void calculerPrimeActiviteTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 1 enfant à charge de 6ans, asf 110€, apl 380€,
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // Futur contrat, CDI, salaire 900€ net
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(0);
        aidesFamiliales.setAllocationSoutienFamilial(110);
        aidesFamiliales.setComplementFamilial(0);
        aidesCAF.setAidesFamiliales(aidesFamiliales);
        aidesCAF.setAidesLogement(utileTests.creerAidePersonnaliseeLogement(300f));
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 114€ (résultat simulateur CAF : 104€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(114);
    }

    @Test
    void calculerPrimeActiviteTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 2 enfants à charge de 6ans et 8ans, asf 233€, af 133€,
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(133);
        aidesFamiliales.setAllocationSoutienFamilial(233);
        aidesFamiliales.setComplementFamilial(0);
        aidesCAF.setAidesFamiliales(aidesFamiliales);
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je fais une simulation le 01/07/2020
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 136€ (résultat simulateur CAF : 119€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(136);
    }

    @Test
    void calculerPrimeActiviteTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 2 enfants à charge de 6ans et 8ans, asf 233€, af 133€, apl 380€,
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(133);
        aidesFamiliales.setAllocationSoutienFamilial(233);
        aidesFamiliales.setComplementFamilial(0);
        aidesCAF.setAidesFamiliales(aidesFamiliales);
        aidesCAF.setAidesLogement(utileTests.creerAidePersonnaliseeLogement(380f));
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 81€ (résultat simulateur CAF : 65€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(81);
    }

    @Test
    void calculerPrimeActiviteTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 3 enfants à charge de 4ans, 6ans et 8ans, asf 350€, af 303€, cf 259€
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(303);
        aidesFamiliales.setAllocationSoutienFamilial(350);
        aidesFamiliales.setComplementFamilial(259);
        aidesCAF.setAidesFamiliales(aidesFamiliales);
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // TODO montant : écart de 65€ avec CAF
        // Alors le montant de la prime d'activité pour le 11/2020 est de 65€ (résultat simulateur CAF : 0€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(80);
    }

    @Test
    void calculerPrimeActiviteTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 3 enfants à charge de 4ans, 6ans et 8ans, asf 350€, af 303€, cf 259€, apl 450€,
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(303);
        aidesFamiliales.setAllocationSoutienFamilial(350);
        aidesFamiliales.setComplementFamilial(259);
        aidesCAF.setAidesFamiliales(aidesFamiliales);
        aidesCAF.setAidesLogement(utileTests.creerAidePersonnaliseeLogement(450f));
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 0€ (résultat simulateur CAF : 0€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }

    @Test
    void calculerPrimeActiviteTest9() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, asf 466€, af = 472€, cf 259€
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(472);
        aidesFamiliales.setAllocationSoutienFamilial(466);
        aidesFamiliales.setComplementFamilial(259);
        aidesCAF.setAidesFamiliales(aidesFamiliales);
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // TODO montant : écart de 51€ avec CAF
        // Alors le montant de la prime d'activité pour le 11/2020 est de 51€ (résultat simulateur CAF : 0€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(51);
    }

    @Test
    void calculerPrimeActiviteTest10() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, célibataire, 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans,
        // asf 466€, af 472€, cf 259€, apl 520€,
        // ass M1(07/2020)= 506,7€ M2(08/2020) = 523,6€ | M3(09/2020) = 523,6€ | M4(10/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);

        AidesCAF aidesCAF = new AidesCAF();
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationsFamiliales(472);
        aidesFamiliales.setAllocationSoutienFamilial(466);
        aidesFamiliales.setComplementFamilial(259);
        aidesCAF.setAidesFamiliales(aidesFamiliales);
        aidesCAF.setAidesLogement(utileTests.creerAidePersonnaliseeLogement(520f));
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(aidesCAF);

        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 0€ (résultat simulateur CAF : 0€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }
}