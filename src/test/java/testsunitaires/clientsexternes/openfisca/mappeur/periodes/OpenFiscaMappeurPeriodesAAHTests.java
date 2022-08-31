package testsunitaires.clientsexternes.openfisca.mappeur.periodes;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.github.tsohr.JSONException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPeriode;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaPeriodes;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurPeriodesAAHTests extends Commun {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    @Autowired
    private Utile testUtile;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = testUtile.getDate("01-01-2022");
    }

    /********************
     * TESTS SUR creerPeriodesAidee
     ************************************************/

    @Test
    void creerPeriodeAideeAAHTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesAAHTests/periode-aah-non-travaille-6-derniers-mois.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAAHTests(0);

	OpenFiscaPeriodes periodeAideeAAH = openFiscaMappeurPeriode.creerPeriodesOpenFiscaAAH(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeAAH.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeAAHTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesAAHTests/periode-aah-1-mois-travaille-6-derniers-mois.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAAHTests(1);

	OpenFiscaPeriodes periodeAideeAAH = openFiscaMappeurPeriode.creerPeriodesOpenFiscaAAH(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeAAH.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeAAHTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesAAHTests/periode-aah-2-mois-travaille-6-derniers-mois.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAAHTests(2);

	OpenFiscaPeriodes periodeAideeAAH = openFiscaMappeurPeriode.creerPeriodesOpenFiscaAAH(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeAAH.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeAAHTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesAAHTests/periode-aah-3-mois-travaille-6-derniers-mois.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAAHTests(3);

	OpenFiscaPeriodes periodeAideeAAH = openFiscaMappeurPeriode.creerPeriodesOpenFiscaAAH(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeAAH.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeAAHTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesAAHTests/periode-aah-4-mois-travaille-6-derniers-mois.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAAHTests(4);

	OpenFiscaPeriodes periodeAideeAAH = openFiscaMappeurPeriode.creerPeriodesOpenFiscaAAH(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeAAH.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeAAHTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesAAHTests/periode-aah-5-mois-travaille-6-derniers-mois.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAAHTests(5);

	OpenFiscaPeriodes periodeAideeAAH = openFiscaMappeurPeriode.creerPeriodesOpenFiscaAAH(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeAAH.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeAAHTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesAAHTests/periode-aah-6-mois-travaille-6-derniers-mois.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAAHTests(6);

	OpenFiscaPeriodes periodeAideeAAH = openFiscaMappeurPeriode.creerPeriodesOpenFiscaAAH(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeAAH.toString()).hasToString(openFiscaPayloadExpected);
    }

    /***************** METHODES UTILES POUR TESTS ********/

    private DemandeurEmploi creerDemandeurEmploiAAHTests(int nombreMoisTravaillesAvantSimulation) throws ParseException {
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.AAH.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1200);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAllocationAAH(900f);
	if (nombreMoisTravaillesAvantSimulation > 0) {
	    PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = utileTests.creerPeriodeTravailleeAvantSimulation(850, 1101, nombreMoisTravaillesAvantSimulation);
	    demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);
	}
	return demandeurEmploi;
    }

}
