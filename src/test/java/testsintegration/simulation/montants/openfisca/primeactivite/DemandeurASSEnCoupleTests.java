package testsintegration.simulation.montants.openfisca.primeactivite;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
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
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurASSEnCoupleTests extends Commun {

    private static final int NUMERA_MOIS_SIMULE_PPA = 6;

    @Autowired
    private OpenFiscaClient openFiscaClient;
    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = utileTests.getDate("01-01-2022");
    }

    /***************************************** en couple ***************************************************************/

    @Test
    void calculerPrimeActiviteEnCoupleTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 0 enfant,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 85€ (résultat simulateur CAF : 81€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(78f);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 0 enfant,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 1900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(1900);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(2428);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 0€ (résultat simulateur CAF : 0€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isZero();
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€, 1 enfant de 6ans,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));

	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 140€ (résultat simulateur CAF : 136€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(134f);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
	// 2 enfants à charge de 6ans et 8ans, af 132€,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 2;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(132);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 11/2022 est de 152€ (résultat simulateur CAF : 147€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(145f);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
	// 2 enfants à charge de 6ans et 8ans, af 132€, apl 20€,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 2;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(132);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(450f));
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// TODO : écart important avec la version d'openfisca utilisée par PNDS (146€), écart important avec la CAF 100€
	// Alors le montant de la prime d'activité pour le 11/2022 est de 196€ (résultat simulateur CAF : 197€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(90f);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
	// 3 enfants à charge de 4ans, 6ans et 8ans, af 303€, cf 259€,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 3;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(303);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// TODO montant : écart de 21€ avec CAF
	// Alors le montant de la prime d'activité pour le 11/2022 est de 169f€ (résultat simulateur CAF : 148€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(103f);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
	// 3 enfants à charge de 4ans, 6ans et 8ans, af 303€, cf 259€, apl 140€,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 3;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(466);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(520f));
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// TODO montant : écart de 30€ avec CAF
	// Alors le montant de la prime d'activité pour le 11/2022 est de 0€ (résultat simulateur CAF : 30€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isZero();
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest8() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
	// 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af 472€, cf 259€,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 4;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// TODO montant : écart de 34€ avec CAF
	// Alors le montant de la prime d'activité pour le 11/2022 est de 128€ (résultat simulateur CAF : 94€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(121f);
    }

    @Test
    void calculerPrimeActiviteEnCoupleTest9() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, en couple, conjoint salaire 1200€,
	// 4 enfants à charge de 4ans, 6ans, 8ans et 12 ans, af 472€, cf 259€, apl 230€,
	// ass M1(06/2022)= 506,7 M2(07/2022) = 523,6€ | M3(08/2022) = 523,6€ | M4(09/2022) = 0€,
	// futur contrat CDI avec salaire net 900€/mois
	boolean isEnCouple = true;
	int nbEnfant = 4;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(472);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setComplementFamilial(259);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(230f));
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setSalaire(createSalaireConjoint());
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// TODO montant : écart de 34€ avec CAF
	// Alors le montant de la prime d'activité pour le 11/2022 est de 74€ (résultat simulateur CAF : 40€ )
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(66f);
    }
}