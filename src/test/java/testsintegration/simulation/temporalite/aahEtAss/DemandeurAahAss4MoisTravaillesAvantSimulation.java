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

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAahAss4MoisTravaillesAvantSimulation extends CommunTests {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private IndividuService individuService;

    @Test
    void simulerPopulationAah4MoisSur6Ass1MoisSur3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
	// enfant à charge de 9ans, af = 90€
	// Montant net journalier ASS = 16,89€, 1 mois cumulé ASS + salaire sur 3 derniers mois
	// AAH : 900€, 4 mois cumulé AAH + salaire sur 6 derniers mois
	// futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(4);
	
	SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();       
    salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(1101, 850));
    salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(0, 0));
    salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(0, 0));        
    demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

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
	    assertThat(simulation.getMesAides().size()).isEqualTo(4);
	    assertThat(simulation.getMesAides().get(Aides.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getMesAides().get(Aides.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(504);
	    });
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(506f);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(523f);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(1);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(146);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(146);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(146);
	    });
	});
    }

    @Test
    void simulerPopulationAah4MoisSur6Ass2MoisSur3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
	// enfant à charge de 9ans, af = 90€
	// Montant net journalier ASS = 16,89€, 2 mois cumulé ASS + salaire sur 3 derniers mois
	// AAH : 900€, 4 mois cumulé AAH + salaire sur 6 derniers mois
	// futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(4);
	
	SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();       
    salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(1101, 850));
    salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(1200, 1000));
    salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(0, 0));        
    demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
   
	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, AAH : 900€, ASS 506€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides().size()).isEqualTo(4);
	    assertThat(simulation.getMesAides().get(Aides.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getMesAides().get(Aides.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(504);
	    });
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(506f);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(1);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
	    });
	});
	
	//TODO montant : écart de 30€ avec CAF
	// Alors les prestations du sixième mois 04/2021 sont :
	// AAH : 180€ (report AAH dégressif)
	// Prime d'activité : 437€ (Simulateur CAF : 407€)
	SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(437);
	    });
	});
    }

    @Test
    void simulerPopulationAah4MoisSur6Ass3MoisSur3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
	// enfant à charge de 9ans, af = 90€
	// Montant net journalier ASS = 16,89€, 3mois cumulé ASS + salaire sur 3 derniers mois
	// AAH : 900€, 4 mois cumulé AAH + salaire sur 6 derniers mois
	// futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(4);
	
	SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();       
    salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(1101, 850));
    salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(1200, 1000));
    salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(1200, 1000));        
    demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
	

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks(demandeurEmploi);
	SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

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
	    assertThat(simulation.getMesAides().size()).isEqualTo(3);
	    assertThat(simulation.getMesAides().get(Aides.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getMesAides().get(Aides.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(504);
	    });
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(900);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(64);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(355);
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
	    assertThat(simulation.getMesAides().size()).isEqualTo(2);
	    assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(180);
	    });
	    assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(355);
	    });
	});
    }
}
