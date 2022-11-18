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
class DemandeurEligibileComplementARETests extends Commun {

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
    void calculerComplementAreMois28Jours() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	dateDebutSimulation = utile.getDate("01-02-2022");
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(dateDebutSimulation);

	Simulation simulation = demandeurEmploiService.simulerComplementARE(demandeurEmploi);

	// Alors les données de complément ARE sont :
	// RSA : 500€
	SimulationMensuelle simulationMois = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(4);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(2016f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CRC.getCode())).satisfies(crc -> {
		assertThat(crc.getMontant()).isEqualTo(106.20f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CRDS.getCode())).satisfies(crds -> {
		assertThat(crds.getMontant()).isEqualTo(9.36f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CSG.getCode())).satisfies(csg -> {
		assertThat(csg.getMontant()).isEqualTo(116.28f);
	    });

	});
    }

    @Test
    void calculerComplementAreMois30Jours() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	dateDebutSimulation = utile.getDate("01-04-2022");
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(dateDebutSimulation);

	Simulation simulation = demandeurEmploiService.simulerComplementARE(demandeurEmploi);

	// Alors les données de complément ARE sont :
	// RSA : 500€
	SimulationMensuelle simulationMois = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(4);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(2240f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CRC.getCode())).satisfies(crc -> {
		assertThat(crc.getMontant()).isEqualTo(118f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CRDS.getCode())).satisfies(crds -> {
		assertThat(crds.getMontant()).isEqualTo(10.4f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CSG.getCode())).satisfies(csg -> {
		assertThat(csg.getMontant()).isEqualTo(129.2f);
	    });

	});
    }

    @Test
    void calculerComplementAreMois31Jours() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	dateDebutSimulation = utile.getDate("01-01-2022");
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(dateDebutSimulation);

	Simulation simulation = demandeurEmploiService.simulerComplementARE(demandeurEmploi);

	// Alors les données de complément ARE sont :
	// RSA : 500€
	SimulationMensuelle simulationMois = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(4);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(2352f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CRC.getCode())).satisfies(crc -> {
		assertThat(crc.getMontant()).isEqualTo(123.9f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CRDS.getCode())).satisfies(crds -> {
		assertThat(crds.getMontant()).isEqualTo(10.92f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.CSG.getCode())).satisfies(csg -> {
		assertThat(csg.getMontant()).isEqualTo(135.66f);
	    });

	});
    }
}
