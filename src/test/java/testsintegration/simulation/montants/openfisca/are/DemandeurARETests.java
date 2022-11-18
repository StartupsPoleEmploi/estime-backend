package testsintegration.simulation.montants.openfisca.are;

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

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurARETests extends Commun {

    @Autowired
    private OpenFiscaClient openFiscaClient;
    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = utile.getDate("01-01-2022");
    }

    /***************************************** célibataire ***************************************************************/

    @Test
    void calculerAreMois28JoursTest() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation, false);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerComplementARE(openFiscaRoot, dateDebutSimulation, 2);

	// Alors le montant du complément ARE pour le 01/2022 est de € (résultat simulateur CAF : 30€)
	assertThat(openFiscaRetourSimulation.getMontantComplementARE()).isEqualTo(1785f);
	assertThat(openFiscaRetourSimulation.getMontantDeductionsComplementARE()).isEqualTo(-232f);
	assertThat(openFiscaRetourSimulation.getNombreJoursIndemnisesComplementARE()).isEqualTo(18);
    }

    @Test
    void calculerAreMois31JoursTest() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation, false);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerComplementARE(openFiscaRoot, dateDebutSimulation, 3);

	// Alors le montant de la prime d'activité pour le 06/2022 est de 19f€ (résultat simulateur CAF : 30€)
	assertThat(openFiscaRetourSimulation.getMontantComplementARE()).isEqualTo(2082f);
	assertThat(openFiscaRetourSimulation.getMontantDeductionsComplementARE()).isEqualTo(-270f);
	assertThat(openFiscaRetourSimulation.getNombreJoursIndemnisesComplementARE()).isEqualTo(21);
    }

    @Test
    void calculerAreMois30JoursTest() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation, false);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerComplementARE(openFiscaRoot, dateDebutSimulation, 4);

	// Alors le montant de la prime d'activité pour le 06/2022 est de 19f€ (résultat simulateur CAF : 30€)
	assertThat(openFiscaRetourSimulation.getMontantComplementARE()).isEqualTo(1983f);
	assertThat(openFiscaRetourSimulation.getMontantDeductionsComplementARE()).isEqualTo(-258f);
	assertThat(openFiscaRetourSimulation.getNombreJoursIndemnisesComplementARE()).isEqualTo(20);
    }

}
