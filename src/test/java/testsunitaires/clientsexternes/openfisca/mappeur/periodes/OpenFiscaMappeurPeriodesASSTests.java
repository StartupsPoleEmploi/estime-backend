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
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurPeriodesASSTests extends Commun {

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

    /********************
     * TESTS SUR creerPeriodesAidee
     ************************************************/

    @Test
    void creerPeriodeAideeASSTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesASSTests/periode-ass-cumul-0-mois-ass-salaire-simulation.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiASSTests(0);
	OpenFiscaPeriodes periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesOpenFiscaASS(demandeurEmploi, dateDebutSimulation);
	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeASSTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesASSTests/periode-ass-cumul-1-mois-ass-salaire-simulation.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiASSTests(1);

	OpenFiscaPeriodes periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesOpenFiscaASS(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeASSTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesASSTests/periode-ass-cumul-2-mois-ass-salaire-simulation.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiASSTests(2);

	OpenFiscaPeriodes periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesOpenFiscaASS(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void creerPeriodeAideeASSTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	String openFiscaPayloadExpected = utileTests
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesASSTests/periode-ass-cumul-3-mois-ass-salaire-simulation.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiASSTests(3);

	OpenFiscaPeriodes periodeAideeASS = openFiscaMappeurPeriode.creerPeriodesOpenFiscaASS(demandeurEmploi, dateDebutSimulation);

	assertThat(periodeAideeASS.toString()).hasToString(openFiscaPayloadExpected);
    }

    /*****************
     * TESTS SUR creerPeriodesSalaireDemandeur
     ********************************************************/

    /**
     * CONTEXTE DES TESTS 
     * 
     * date demande simulation : 01-01-2022
     * 
     * avant simulation
     * M-2 01-12-2021       
     * M-1 01-11-2021 
     * 
     * période de la simulation sur 6 mois
     * M1 02/2022       
     * M2 03/2022   
     * M3 04/2022    
     * M4 05/2022
     * M5 06/2022
     * M6 07/2022
     *      
     *  
     ********************************************************************************/

    /***************** METHODES UTILES POUR TESTS *******************************/

    private DemandeurEmploi creerDemandeurEmploiASSTests(int nombreMoisTravaillesAvantSimulation) throws ParseException {

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1228);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(950);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	if (nombreMoisTravaillesAvantSimulation > 0) {
	    PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = utileTests.creerPeriodeTravailleeAvantSimulation(850, 1101, nombreMoisTravaillesAvantSimulation);
	    demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);
	}

	return demandeurEmploi;
    }
}
