package testsintegration.simulation.montants.openfisca.primeactivite;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

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
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurASSCelibataireTests extends Commun {

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
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI, salaire net 900€
	boolean isEnCouple = false;
	int nbEnfant = 0;

	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);

	Simulation simulation = createSimulation();

	// Lorsque je fais une simulation le 01/07/2022
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 93€ (résultat simulateur CAF : 93€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(92f);
    }

    @Test
    void calculerPrimeActiviteTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 0 enfant,
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI avec salaire net 1900€/mois
	boolean isEnCouple = false;
	int nbEnfant = 0;

	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1900);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(2428);

	Simulation simulation = createSimulation();

	// Lorsque je fais une simulation le 01/07/2022
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 0€ (résultat simulateur CAF : pas droit à cette préstation)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isZero();
    }

    @Test
    void calculerPrimeActiviteTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 1 enfant à charge de 6ans, asf 110€,
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI, salaire net 900€
	boolean isEnCouple = false;
	int nbEnfant = 1;

	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(110);

	Simulation simulation = createSimulation();

	// Lorsque je fais une simulation le 01/07/2022
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 158€ (résultat simulateur CAF : 148€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(148f);
    }

    @Test
    void calculerPrimeActiviteTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 1 enfant à charge de 6ans, asf 110€, apl 380€,
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// Futur contrat, CDI, salaire 900€ net
	boolean isEnCouple = false;
	int nbEnfant = 1;

	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(110);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(300f));
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	Simulation simulation = createSimulation();

	// Lorsque je fais une simulation le 01/07/2022
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 114€ (résultat simulateur CAF : 104€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(103f);
    }

    @Test
    void calculerPrimeActiviteTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 2 enfants à charge de 6ans et 8ans, asf 233€, af 133€,
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = false;
	int nbEnfant = 2;

	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(133);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(233);

	Simulation simulation = createSimulation();

	// Lorsque je fais une simulation le 01/07/2022
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 136€ (résultat simulateur CAF : 119€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(118f);
    }

    @Test
    void calculerPrimeActiviteTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 2 enfants à charge de 6ans et 8ans, asf 233€, af 133€, apl 380€,
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = false;
	int nbEnfant = 2;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(133);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(233);
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	Simulation simulation = createSimulation();

	// Lorsque je calcul le montant de la prime d'activité
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 81€ (résultat simulateur CAF : 65€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(63f);
    }

    @Test
    void calculerPrimeActiviteTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 3 enfants à charge de 4ans, 6ans et 8ans, asf 350€, af 303€, cf 259€
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = false;
	int nbEnfant = 3;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(133);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(233);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);

	Simulation simulation = createSimulation();

	// Lorsque je calcul le montant de la prime d'activité
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// TODO montant : écart de 334€ avec CAF
	// Alors le montant de la prime d'activité pour le 11/2022 est de 406€ (résultat simulateur CAF : 72€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(133f);
    }

    @Test
    void calculerPrimeActiviteTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 3 enfants à charge de 4ans, 6ans et 8ans, asf 350€, af 303€, cf 259€, apl 450€,
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = false;
	int nbEnfant = 3;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(303);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(350);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(450f));
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	Simulation simulation = createSimulation();

	// Lorsque je calcul le montant de la prime d'activité
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// TODO montant : écart de 135€ avec CAF
	// Alors le montant de la prime d'activité pour le 11/2022 est de 135€ (résultat simulateur CAF : 0€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isZero();
    }

    @Test
    void calculerPrimeActiviteTest9() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, asf 466€, af = 472€, cf 259€
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = false;
	int nbEnfant = 4;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(466);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);

	Simulation simulation = createSimulation();

	// Lorsque je calcul le montant de la prime d'activité
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// TODO montant : écart de 124€ avec CAF
	// Alors le montant de la prime d'activité pour le 11/2022 est de 124€ (résultat simulateur CAF : 0€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(16f);
    }

    @Test
    void calculerPrimeActiviteTest10() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans,
	// asf 466€, af 472€, cf 259€, apl 520€,
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = false;
	int nbEnfant = 4;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(466);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(520f));
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	Simulation simulation = createSimulation();

	// Lorsque je calcul le montant de la prime d'activité
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 0€ (résultat simulateur CAF : 0€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isZero();
    }

    @Test
    void calculerPrimeActiviteTest11() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans,
	// asf 466€, af 472€, cf 259€, apl 520€,
	// ass M1(07/2022)= 506,7€ M2(08/2022) = 523,6€ | M3(09/2022) = 523,6€ | M4(10/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = false;
	int nbEnfant = 4;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(466);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(520f));
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	Simulation simulation = createSimulation();

	// Lorsque je calcul le montant de la prime d'activité
	LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-01-2022");
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulation, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 0€ (résultat simulateur CAF : 0€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isZero();
    }
}