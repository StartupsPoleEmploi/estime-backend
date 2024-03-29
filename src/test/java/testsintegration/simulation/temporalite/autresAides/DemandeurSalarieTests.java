package testsintegration.simulation.temporalite.autresAides;

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
class DemandeurSalarieTests extends Commun {

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerMesRessourcesFinancieresSalarieAvecCumulEtPrimeActiviteTest()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
	// Montant net journalier ASS = 16,89€, 0 mois cumulé ASS + salaire sur 3 derniers mois
	// futur contrat CDI, salaire brut 1600€, soit 1245€ par mois, 20h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
	// DE salarié qui cumule ancien et nouveau salaire et qui perçoit de la prime d'activité
	DemandeurEmploi demandeurEmploi = createDemandeurEmploiSalarie();
	demandeurEmploi.getInformationsPersonnelles().setCumulAncienEtNouveauSalaire(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setHasPrimeActivite(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setPrimeActivite(500f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(3);

	// Lorsque je simule mes prestations le 01/01/2022
	initMocks();
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// AGEPI : 400€, Aide mobilité : 450€, ASS : 506€, CA abattu = (1200 / 12) * 29
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(500f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// ASS : 523€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(4);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(192f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(500f);
	    });
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// ASS : 523€
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(500f);
	    });
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// aucune aide
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(275f);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// Prime d'activité : 140€ (simulateur CAF : 129€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(275f);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// Prime d'activité : 140€
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(275f);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresSalarieAvecCumulSansPrimeActiviteTest()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
	// Montant net journalier ASS = 16,89€, 0 mois cumulé ASS + salaire sur 3 derniers mois
	// futur contrat CDI, salaire brut 1600€, soit 1245€ par mois, 20h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
	// DE salarié qui cumule ancien et nouveau salaire et qui perçoit de la prime d'activité
	DemandeurEmploi demandeurEmploi = createDemandeurEmploiSalarie();
	demandeurEmploi.getInformationsPersonnelles().setCumulAncienEtNouveauSalaire(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setHasPrimeActivite(false);

	// Lorsque je simule mes prestations le 01/01/2022
	initMocks();
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// AGEPI : 400€, Aide mobilité : 450€, ASS : 506€, CA abattu = (1200 / 12) * 0.5
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// ASS : 523€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(4);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(192f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(141f);
	    });
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// ASS : 523€
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(141f);
	    });
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// aucune aide
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(141f);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// Prime d'activité : 140€ (simulateur CAF : 129€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(207f);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// Prime d'activité : 140€
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(1800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(207f);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresSansCumulAvecPrimeActiviteTest()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
	// Montant net journalier ASS = 16,89€, 0 mois cumulé ASS + salaire sur 3 derniers mois
	// futur contrat CDI, salaire brut 1600€, soit 1245€ par mois, 20h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
	// DE salarié qui cumule ancien et nouveau salaire et qui perçoit de la prime d'activité
	DemandeurEmploi demandeurEmploi = createDemandeurEmploiSalarie();
	demandeurEmploi.getInformationsPersonnelles().setCumulAncienEtNouveauSalaire(false);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setHasPrimeActivite(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setPrimeActivite(500f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(3);

	// Lorsque je simule mes prestations le 01/01/2022
	initMocks();
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// AGEPI : 400€, Aide mobilité : 450€, ASS : 506€, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(500f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// ASS : 523€, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(4);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(192f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(500f);
	    });
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// ASS : 523€, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(500f);
	    });
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// aucune aide, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(441f);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// Prime d'activité : 140€ (simulateur CAF : 129€), CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(441f);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// Prime d'activité : 140€, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(441f);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresSansCumulSansPrimeActiviteTest()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
	// Montant net journalier ASS = 16,89€, 0 mois cumulé ASS + salaire sur 3 derniers mois
	// futur contrat CDI, salaire brut 1600€, soit 1245€ par mois, 20h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
	// DE salarié qui ne cumule pas ancien et nouveau salaire et qui ne perçoit pas de la prime d'activité
	DemandeurEmploi demandeurEmploi = createDemandeurEmploiSalarie();
	demandeurEmploi.getInformationsPersonnelles().setCumulAncienEtNouveauSalaire(false);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setHasPrimeActivite(false);

	// Lorsque je simule mes prestations le 01/01/2022
	initMocks();
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// AGEPI : 400€, Aide mobilité : 450€, ASS : 506€, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// ASS : 523€, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(4);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(192f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(141f);
	    });
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// ASS : 523€, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(141f);
	    });
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// aucune aide, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(141f);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// Prime d'activité : 140€ (simulateur CAF : 129€), CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(456f);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// Prime d'activité : 140€, CA abattu = (1200 / 12) * 0.66
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).satisfies(salaire -> {
		assertThat(salaire.getMontant()).isEqualTo(800);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(456f);
	    });
	});
    }
}
