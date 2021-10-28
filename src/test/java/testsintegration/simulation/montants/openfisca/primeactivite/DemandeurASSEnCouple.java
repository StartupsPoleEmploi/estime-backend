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
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurASSEnCouple extends CommunTests {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    /***************************************** en couple ***************************************************************/

    @Test
    void calculerPrimeActiviteEnCoupleTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 0 enfant,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);


        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 85€ (résultat simulateur CAF : 81€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(85);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 0 enfant,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 1900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(2428);
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité avec salaires 07/2020 = 1900, 08/2020 = 1900, 09/2020 = 1900
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 0€ (résultat simulateur CAF : 0€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 1 enfant de 6ans,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 140€ (résultat simulateur CAF : 136€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(140);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
        // 2 enfants à charge de 6ans et 8ans, af 132€,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(132);
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 152€ (résultat simulateur CAF : 147€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(152);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
        // 2 enfants à charge de 6ans et 8ans, af 132€, apl 20€,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(132);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(20f));
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // Alors le montant de la prime d'activité pour le 11/2020 est de 145€ (résultat simulateur CAF : 141€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(145);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
        // 3 enfants à charge de 4ans, 6ans et 8ans, af 303€, cf 259€,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(303);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // TODO montant : écart de 34€ avec CAF
        // Alors le montant de la prime d'activité pour le 11/2020 est de 111€ (résultat simulateur CAF : 77€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(111);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
        // 3 enfants à charge de 4ans, 6ans et 8ans, af 303€, cf 259€, apl 140€,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(466);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(520f));
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // TODO montant : écart de 34€ avec CAF
        // Alors le montant de la prime d'activité pour le 11/2020 est de 64€ (résultat simulateur CAF : 30€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(64);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
        // 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af 472€, cf 259€,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // TODO montant : écart de 34€ avec CAF
        // Alors le montant de la prime d'activité pour le 11/2020 est de 128€ (résultat simulateur CAF : 94€)
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(128);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest9() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
        // 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af 472€, cf 259€, apl 230€,
        // ass M1(06/2020)= 506,7 M2(07/2020) = 523,6€ | M3(08/2020) = 523,6€ | M4(09/2020) = 0€,
        // futur contrat CDI avec salaire net 900€/mois
        boolean isEnCouple = true;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(230f));
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        SimulationAides simulationAides = createSimulationAides();

        // Lorsque je calcul le montant de la prime d'activité
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("05-07-2020");
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        // TODO montant : écart de 34€ avec CAF
        // Alors le montant de la prime d'activité pour le 11/2020 est de 74€ (résultat simulateur CAF : 40€ )
        assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(74);
    }
}