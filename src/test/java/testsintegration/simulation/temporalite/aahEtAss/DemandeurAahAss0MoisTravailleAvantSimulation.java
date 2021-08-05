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

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
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

	// Lorsque je simule mes aides le 20/10/2020
	initMocks(demandeurEmploi);
	SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

	// Alors les aides du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, AAH : 900€, ASS : 506€
	SimulationMensuelle simulationMois1 = simulationAidesSociales.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides().size()).isEqualTo(4);
	    assertThat(simulation.getMesAides().get(AidesSociales.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getMesAides().get(AidesSociales.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(504);
	    });
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(506);
	    });
	});
	
	// Alors les aides du second mois 12/2020 sont :
	// AAH : 900€
	// ASS : 523
	// Pas de PPA car c'est la temporalité ASS qui prend le pas sur la temporalité AAH
	SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(523);
	    });
	});
	
	// Alors les aides du troisième mois 01/2021 sont :
	// AAH : 900€
	// ASS : 523
	// Pas de PPA car c'est la temporalité ASS qui prend le pas sur la temporalité AAH
	SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(523);
	    });
	});
	
	// Alors les aides du quatrième mois 02/2021 sont :
	// AAH : 900€
	// Pas de PPA car c'est la temporalité ASS qui prend le pas sur la temporalité AAH
	SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides().size()).isEqualTo(1);
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	});
	
	// Alors les aides du cinquième mois 03/2021 sont :
	// Prime d'activité : 64€ (simulateur CAF : 52€)
	// AAH : 900€
	SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	});
	// Alors les aides du sixième mois 04/2021 sont :
	// Prime d'activité : 64€
	// AAH : 900€
	SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	});
    }
}
