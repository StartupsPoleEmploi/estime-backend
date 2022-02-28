package testsintegration.simulation.temporalite.aah;

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

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.services.DemandeurEmploiService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAah2MoisTravaillesAvantSimulationTests extends Commun {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    @Test
    void simulerPopulationAahTravaille2MoisAvantSimulationProchaineDeclarationMois0()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, France métropolitaine, né le 5/07/1986, célibataire,
	// 1 enfant 9ans
	// asf = 117€
	// AAH = 900€
	// travaillé 2 mois avant simulation dont 2 mois dans les 3 derniers mois avant la simulation
	// CDI, salaire 940€ net 1200€ brut
	// mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(0);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 3);
	Salaire salaireMoisMoins0 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(800, 1038);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins0, 0);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(4);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(450);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// TODO montant : écart de 36€ avec CAF
	// Alors les prestations du second mois 12/2020 sont :
	// AAH : 900€
	// Prime d'activité : 222€ (simulateur CAF : 186€)
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(158);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du troisième mois 01/2021 sont :
	// AAH : 900€
	// Prime d'activité : 222€
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(158);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du quatrième mois 02/2021 sont :
	// AAH : 900€
	// Prime d'activité : 222€
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(204);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du cinquième mois 03/2021 sont :
	// AAH : 180€
	// Prime d'activité : 193€ (simulateur CAF : 182€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(204);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du sixième mois 04/2021 sont :
	// AAH : 180€
	// Prime d'activité : 193€
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(204);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
    }

    @Test
    void simulerPopulationAahTravaille2MoisAvantSimulationProchaineDeclarationMois1()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, France métropolitaine, né le 5/07/1986, célibataire,
	// 1 enfant 9ans
	// asf = 117€
	// AAH = 900€
	// travaillé 2 mois avant simulation dont 2 mois dans les 3 derniers mois avant la simulation
	// CDI, salaire 940€ net 1200€ brut
	// mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(1);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 3);
	Salaire salaireMoisMoins0 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(800, 1038);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins0, 0);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(4);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(450);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// TODO montant : écart de 36€ avec CAF
	// Alors les prestations du second mois 12/2020 sont :
	// AAH : 900€
	// Prime d'activité : 222€ (simulateur CAF : 186€)
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(158);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du troisième mois 01/2021 sont :
	// AAH : 900€
	// Prime d'activité : 222€
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(158);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du quatrième mois 02/2021 sont :
	// AAH : 900€
	// Prime d'activité : 222€
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(158);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du cinquième mois 03/2021 sont :
	// AAH : 180€
	// Prime d'activité : 193€ (simulateur CAF : 182€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(193);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du sixième mois 04/2021 sont :
	// AAH : 180€
	// Prime d'activité : 193€
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(193);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
    }

    @Test
    void simulerPopulationAahTravaille2MoisAvantSimulationProchaineDeclarationMois2()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, France métropolitaine, né le 5/07/1986, célibataire,
	// 1 enfant 9ans
	// asf = 117€
	// AAH = 900€
	// travaillé 2 mois avant simulation dont 2 mois dans les 3 derniers mois avant la simulation
	// CDI, salaire 940€ net 1200€ brut
	// mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(2);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 3);
	Salaire salaireMoisMoins0 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(800, 1038);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins0, 0);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(4);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(450);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// TODO montant : écart de 36€ avec CAF
	// Alors les prestations du second mois 12/2020 sont :
	// AAH : 900€
	// Prime d'activité : 222€ (simulateur CAF : 186€)
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(158);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du troisième mois 01/2021 sont :
	// AAH : 900€
	// Prime d'activité : 222€
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(222);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du quatrième mois 02/2021 sont :
	// AAH : 900€
	// Prime d'activité : 222€
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(222);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du cinquième mois 03/2021 sont :
	// AAH : 180€
	// Prime d'activité : 193€ (simulateur CAF : 182€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(222);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du sixième mois 04/2021 sont :
	// AAH : 180€
	// Prime d'activité : 193€
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(193);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
    }

    @Test
    void simulerPopulationAahTravaille2MoisAvantSimulationProchaineDeclarationMois3()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, France métropolitaine, né le 5/07/1986, célibataire,
	// 1 enfant 9ans
	// asf = 117€
	// AAH = 900€
	// travaillé 2 mois avant simulation dont 2 mois dans les 3 derniers mois avant la simulation
	// CDI, salaire 940€ net 1200€ brut
	// mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(3);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 3);
	Salaire salaireMoisMoins0 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(800, 1038);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins0, 0);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(4);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(450);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// TODO montant : écart de 36€ avec CAF
	// Alors les prestations du second mois 12/2020 sont :
	// AAH : 900€
	// Prime d'activité : 222€ (simulateur CAF : 186€)
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(158);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du troisième mois 01/2021 sont :
	// AAH : 900€
	// Prime d'activité : 222€
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(158);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du quatrième mois 02/2021 sont :
	// AAH : 900€
	// Prime d'activité : 222€
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(204);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du cinquième mois 03/2021 sont :
	// AAH : 180€
	// Prime d'activité : 193€ (simulateur CAF : 182€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(204);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du sixième mois 04/2021 sont :
	// AAH : 180€
	// Prime d'activité : 193€
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(204);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
		assertThat(aah.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
    }
}
