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

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurPeriodesRSATests extends Commun {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = utileTests.getDate("01-01-2022");
    }

    @Test
    void creerPeriodeRSATest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesRSATests/periode-rsa-prochaine-declaration-tri-mois-0.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiRSATests(0);
	OpenFiscaPeriodes periodeAideeRSA = openFiscaMappeurPeriode.creerPeriodesOpenFiscaRSA(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeRSA.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeRSATest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesRSATests/periode-rsa-prochaine-declaration-tri-mois-1.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiRSATests(1);
	OpenFiscaPeriodes periodeAideeRSA = openFiscaMappeurPeriode.creerPeriodesOpenFiscaRSA(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeRSA.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeRSATest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesRSATests/periode-rsa-prochaine-declaration-tri-mois-2.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiRSATests(2);
	OpenFiscaPeriodes periodeAideeRSA = openFiscaMappeurPeriode.creerPeriodesOpenFiscaRSA(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeRSA.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeRSATest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesRSATests/periode-rsa-prochaine-declaration-tri-mois-3.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiRSATests(3);
	OpenFiscaPeriodes periodeAideeRSA = openFiscaMappeurPeriode.creerPeriodesOpenFiscaRSA(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeRSA.toString()).hasToString(openFiscaPayloadExpected);
    }

    /***************** METHODES UTILES POUR TESTS ********/

    private DemandeurEmploi creerDemandeurEmploiRSATests(int prochaineDeclarationTrimestrielle) throws ParseException {
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1200);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAllocationRSA(500f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(prochaineDeclarationTrimestrielle);
	return demandeurEmploi;
    }

}
