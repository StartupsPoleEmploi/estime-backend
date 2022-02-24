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
class DemandeurAahAss4MoisTravaillesAvantSimulationTests extends Commun {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    @Test
    void simulerPopulationAah4MoisSur6Ass1MoisSur3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
	// enfant à charge de 9ans, af = 90€
	// AAH : 900€
	// 4 mois travaillés avant simulation dont 1 mois dans les 3 derniers mois avant la simulation
	// Montant net journalier ASS = 16,89€
	// futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 4);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins3 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins4 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins5 = utileTests.creerSalaire(850, 1101);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins3, 3);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins4, 4);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins5, 5);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	Simulation simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€
	// Aide mobilité : 450€
	// AAH : 900€
	// ASS 506€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(5);
	    assertThat(simulation.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(450);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(506);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du second mois 12/2020 sont :
	// AAH : 900€
	// ASS 523€
	SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);

	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(523);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du troisième mois 01/2021 sont :
	// AAH : 180€ (dégressivité : 900 - 60% * 1200 = 180)
	// Prime d'activité : 64€ (Simulateur CAF : 52€)
	SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(2);

	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du quatrième mois 02/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 64€
	SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);

	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(146);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du cinquième mois 03/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 64€
	SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);

	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(146);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du sixième mois 04/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 437€ (Simulateur CAF : ?€)
	SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);

	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(146);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
    }

    @Test
    void simulerPopulationAah4MoisSur6Ass2MoisSur3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
	// enfant à charge de 9ans, af = 90€
	// AAH : 900€
	// 4 mois travaillés avant simulation dont 2 mois dans les 3 derniers mois avant la simulation
	// Montant net journalier ASS = 16,89€
	// futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 6);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins2 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins4 = utileTests.creerSalaire(500, 659);
	Salaire salaireMoisMoins5 = utileTests.creerSalaire(500, 659);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins2, 2);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins4, 4);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins5, 5);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	Simulation simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, AAH : 900€, ASS 506€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(5);
	    assertThat(simulation.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(450);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(506);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du second mois 12/2020 sont :
	// AAH : 900€
	SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(2);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du troisième mois 01/2021 sont :
	// AAH : 180€ (dégressivité : 900 - 60% * 1200 = 180)
	// Prime d'activité : 64€ (Simulateur CAF : 52€)
	SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du quatrième mois 02/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 64€
	SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du cinquième mois 03/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 64€
	SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// TODO montant : écart de 30€ avec CAF
	// Alors les prestations du sixième mois 04/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 437€ (Simulateur CAF : 407€)
	SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(437);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

    }

    @Test
    void simulerPopulationAah4MoisSur6Ass3MoisSur3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
	// enfant à charge de 9ans, af = 90€
	// AAH : 900€
	// 4 mois travaillés avant simulation dont 3 mois dans les 3 derniers mois avant la simulation
	// Montant net journalier ASS = 16,89€
	// futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1101, 850, 4));

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	Simulation simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€
	// Aide mobilité : 450€
	// AAH : 900€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(4);
	    assertThat(simulation.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(450);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du second mois 12/2020 sont :
	// AAH : 900€
	// Prime d'activité : 64€ (Simulateur CAF : 52€)
	SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du troisième mois 01/2021 sont :
	// AAH : 180€ (dégressivité : 900 - 60% * 1200 = 180)
	// Prime d'activité : 64€
	SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du quatrième mois 02/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 64€
	SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du cinquième mois 03/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 355€ (Simulateur CAF : 323€)
	SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(355);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});

	// Alors les prestations du sixième mois 04/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 355€
	SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getAides()).hasSize(3);
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(355);
	    });
	    assertThat(simulation.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
    }
}
