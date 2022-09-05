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
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurASSCelibatairePensionInvaliditeTests extends Commun {

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

    /***************************************** célibataire ***************************************************************/

    @Test
    void calculerPrimeActivitePensionInvaliditeTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 0 enfant à charge,
	// ASS de 16.89€ journalière
	// pension d'invalidité 200€ par mois,
	// futur contrat CDI avec salaire net 800€/mois
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(800);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1038);

	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setAidesCPAM(aidesCPAM);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 06/2022 est de 19f€ (résultat simulateur CAF : 30€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(19f);
    }

    @Test
    void calculerPrimeActivitePensionInvaliditeTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 1 enfant à charge de 4ans
	// ASS de 16.89€ journalière
	// pension d'invalidité 200€ par mois,
	// futur contrat CDI avec salaire net 800€/mois
	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(6));

	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(800);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1038);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(117);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(150f));
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setAidesCPAM(aidesCPAM);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// TODO montant : écart de 34€ avec CAF
	// Alors le montant de la prime d'activité pour le 06/2022 est de 29f€ (résultat simulateur CAF : 83€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(29f);
    }

    @Test
    void calculerPrimeActivitePensionInvaliditeTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 2 enfants à charge de 4 ans et 5 ans,
	// ASS de 16.89€ journalière
	// asf 233€, af 133€, apl 150€, pension invalidité 200€
	// futur contrat CDI avec salaire net 800€/mois
	boolean isEnCouple = false;
	int nbEnfant = 2;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(4));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(800);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1038);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationSoutienFamilial(233);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().setAllocationsFamiliales(134);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(150f));
	demandeurEmploi.getInformationsPersonnelles().setLogement(createLogement());

	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setAidesCPAM(aidesCPAM);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(openFiscaRoot, dateDebutSimulation, NUMERA_MOIS_SIMULE_PPA);

	// Alors le montant de la prime d'activité pour le 06/2022 est de 18€ (résultat simulateur CAF : 0€)
	assertThat(openFiscaRetourSimulation.getMontantPrimeActivite()).isEqualTo(0);
    }
}
