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
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAahAss6MoisTravaillesAvantSimulationTests extends Commun {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private IndividuService individuService;

    @Test
    void simulerPopulationAahAssTravaille6MoisAvantSimulation() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
        // enfant à charge de 9ans, af = 90€
        // Montant net journalier ASS = 16,89€, 3 mois cumulé ASS + salaire sur 3 derniers mois
        // AAH = 900€, 6 mois travaillé avant simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par
        // mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(6);
        demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utile.creerPeriodeTravailleeAvantSimulation(1101, 850, 1200, 1000, 1200, 1000));

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi, true, true, false);
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€
        // Aide mobilité : 450€,
        // AAH : 180€ (dégressivité : 900 - 60% * 1200 = 180)
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(4);
            assertThat(simulation.getMesAides().get(Aides.AGEPI.getCode())).satisfies(agepi -> {
                assertThat(agepi).isNotNull();
            });
            assertThat(simulation.getMesAides().get(Aides.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
                assertThat(aideMobilite).isNotNull();
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 180€ (report AAH dégressif)
        // Prime d'activité : 146€ (Simulateur CAF : 135€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 180€ (report AAH dégressif)
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 180€ (report AAH dégressif)
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du cinquième mois 03/2021 sont :
        // AAH : 180€ (report AAH dégressif)
        // Prime d'activité : 437€ (Simulateur CAF : 407€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(437);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 180€ (report AAH dégressif)
        // Prime d'activité : 437€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(437);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }
}
