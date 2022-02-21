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
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurMicroEntrepreneurTests extends Commun {

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerMesRessourcesFinancieresMicroEntrepreneurTest()
	    throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
	// Montant net journalier ASS = 16,89€, 0 mois cumulé ASS + salaire sur 3 derniers mois
	// futur contrat CDI, salaire brut 1600€, soit 1245€ par mois, 20h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
	// DE micro entrepreneur
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
	demandeurEmploi.getInformationsPersonnelles().setMicroEntrepreneur(true);
	demandeurEmploi.getRessourcesFinancieres().setBeneficesMicroEntrepriseDernierExercice(1200f);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks();
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// AGEPI : 400€, Aide mobilité : 450€, ASS : 506€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(6);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.MICRO_ENTREPRENEUR.getCode())).satisfies(micro -> {
		assertThat(micro.getMontant()).isEqualTo(100f);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
		assertThat(agepi.getMontant()).isEqualTo(400);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
		assertThat(aideMobilite.getMontant()).isEqualTo(450);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(506);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du second mois 12/2020 sont :
	// ASS : 523€
	SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(4);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.MICRO_ENTREPRENEUR.getCode())).satisfies(micro -> {
		assertThat(micro.getMontant()).isEqualTo(100f);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(523);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du troisième mois 01/2021 sont :
	// ASS : 523€
	SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(4);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.MICRO_ENTREPRENEUR.getCode())).satisfies(micro -> {
		assertThat(micro.getMontant()).isEqualTo(100f);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode())).satisfies(ass -> {
		assertThat(ass.getMontant()).isEqualTo(523);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du quatrième mois 02/2021 sont :
	// aucune aide
	SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.MICRO_ENTREPRENEUR.getCode())).satisfies(micro -> {
		assertThat(micro.getMontant()).isEqualTo(100f);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du cinquième mois 03/2021 sont :
	// Prime d'activité : 140€ (simulateur CAF : 129€)
	SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(4);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.MICRO_ENTREPRENEUR.getCode())).satisfies(micro -> {
		assertThat(micro.getMontant()).isEqualTo(100f);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(130);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// Prime d'activité : 140€
	SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(4);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.MICRO_ENTREPRENEUR.getCode())).satisfies(micro -> {
		assertThat(micro.getMontant()).isEqualTo(100f);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(130);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
		assertThat(asf.getMontant()).isEqualTo(117);
	    });
	});
    }
}
