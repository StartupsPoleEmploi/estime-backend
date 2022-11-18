package testsintegration.simulation.parcoursComplementARE;

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

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.services.DemandeurEmploiService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurNonEligibileComplementARETests extends Commun {

    private LocalDate dateDebutSimulation;

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
    }

    /***************************************** célibataire ***************************************************************/

    @Test
    void calculerComplementAre() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	dateDebutSimulation = utile.getDate("01-02-2022");
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(dateDebutSimulation);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(4000f);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(5065f);

	Simulation simulation = demandeurEmploiService.simulerComplementARE(demandeurEmploi);

	// Alors les données de complément ARE sont :
	// RSA : 500€
	SimulationMensuelle simulationMois = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isZero();
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CRC.getCode())).satisfies(crc -> {
		assertThat(crc.getMontant()).isZero();
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CRDS.getCode())).satisfies(crds -> {
		assertThat(crds.getMontant()).isZero();
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CSG.getCode())).satisfies(csg -> {
		assertThat(csg.getMontant()).isZero();
	    });
	});
    }
}
