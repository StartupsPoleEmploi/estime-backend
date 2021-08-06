package testsintegration.simulation.temporalite.aahEtAss;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;

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

import fr.poleemploi.estime.commun.enumerations.PrestationsSociales;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationPrestationsSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAahAss0MoisTravailleAvantSimulation extends CommunTests {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private IndividuService individuService;

    @Test
    void simulerPopulationAahNonTravailleAvantSimulation() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
	// enfant à charge de 9ans, af = 90€
	// Montant net journalier ASS = 16,89€, 0 mois cumulé ASS + salaire sur 3 derniers mois
	// AAH = 900€, 0 mois travaillé avant simulation
	// futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
	demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	SimulationPrestationsSociales simulationPrestationsSociales = individuService.simulerPrestationsSociales(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, AAH : 900€, ASS : 506€
	SimulationMensuelle simulationMois1 = simulationPrestationsSociales.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(4);
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(504);
	    });
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(506);
	    });
	});
	
	// Alors les prestations du second mois 12/2020 sont :
	// AAH : 900€
	// ASS : 523
	// Pas de PPA car c'est la temporalité ASS qui prend le pas sur la temporalité AAH
	SimulationMensuelle simulationMois2 = simulationPrestationsSociales.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(523);
	    });
	});
	
	// Alors les prestations du troisième mois 01/2021 sont :
	// AAH : 900€
	// ASS : 523
	// Pas de PPA car c'est la temporalité ASS qui prend le pas sur la temporalité AAH
	SimulationMensuelle simulationMois3 = simulationPrestationsSociales.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(523);
	    });
	});
	
	// Alors les prestations du quatrième mois 02/2021 sont :
	// AAH : 900€
	// Pas de PPA car c'est la temporalité ASS qui prend le pas sur la temporalité AAH
	SimulationMensuelle simulationMois4 = simulationPrestationsSociales.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(1);
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	});
	
	// Alors les prestations du cinquième mois 03/2021 sont :
	// Prime d'activité : 64€ (simulateur CAF : 52€)
	// AAH : 900€
	SimulationMensuelle simulationMois5 = simulationPrestationsSociales.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// Prime d'activité : 64€
	// AAH : 900€
	SimulationMensuelle simulationMois6 = simulationPrestationsSociales.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	});
    }
}
