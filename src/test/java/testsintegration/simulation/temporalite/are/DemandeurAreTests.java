package testsintegration.simulation.temporalite.are;

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
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAreTests extends Commun {

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerMesRessourcesFinancieresPasDeComplementARETest()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE ARE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 15h, salaire net 1291€ brut 1000€,kilométrage domicile -> taf = 10kms + 20 trajets
	// SJR : 48€, Allocation journalière brute : 37€, nombre de jours restants : 60 

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1544f);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1200f);
	initMocks("23-10-2021", demandeurEmploi.getFuturTravail().getSalaire().getMontantNet());
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).isEmpty();

	});
	// Alors les prestations du second mois 12/2020 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).isEmpty();
	});
	// Alors les prestations du troisième mois 01/2021 sont :
	// PA : 118 (simulateur CAF : 111€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).isEmpty();
	});
	// Alors les prestations du quatrième mois 02/2021 sont :
	// PA : 118 (simulateur CAF : 111€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).isEmpty();
	});
	// Alors les prestations du cinquième mois 03/2021 sont :
	// PA : 118€ (simulateur CAF : 111€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(238);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// PA : 174€ (simulateur CAF : 167€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(238);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresComplementARE3MoisTest()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE ARE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 15h, salaire net 1291€ brut 1000€,kilométrage domicile -> taf = 10kms + 20 trajets
	// SJR : 48€, Allocation journalière brute : 37€, nombre de jours restants : 10 

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setNombreJoursRestants(10f);
	initMocks("23-10-2021", demandeurEmploi.getFuturTravail().getSalaire().getMontantNet());
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 350€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du second mois 12/2020 sont :
	// RSA : 350€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du troisième mois 01/2021 sont :
	// RSA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(74);
	    });
	});
	// Alors les prestations du quatrième mois 02/2021 sont :
	// PA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).isEmpty();
	});
	// Alors les prestations du cinquième mois 03/2021 sont :
	// PA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(141);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// PA : 367€ (simulateur CAF : 353€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(141);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresComplementARE6MoisTest()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE ARE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 15h, salaire net 1291€ brut 1000€,kilométrage domicile -> taf = 10kms + 20 trajets
	// SJR : 48€, Allocation journalière brute : 37€, nombre de jours restants : 60 

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	initMocks("23-07-2021", demandeurEmploi.getFuturTravail().getSalaire().getMontantNet());
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 08/2021 sont :
	// RSA : 380€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("08");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});

	// Alors les prestations du second mois 09/2021 sont :
	// RSA : 380€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);

	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("09");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du troisième mois 10/2021 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("10");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du quatrième mois 11/2021 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du cinquième mois 12/2021 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(121);
	    });
	});
	// Alors les prestations du sixième mois 01/2022 sont :
	// RSA : 16 (simulateur CAF : 0€), PA : 294 (simulateur CAF : 291€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(121);
	    });
	});
    }
}
