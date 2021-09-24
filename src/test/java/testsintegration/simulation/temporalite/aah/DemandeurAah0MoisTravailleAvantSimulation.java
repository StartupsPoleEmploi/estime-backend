package testsintegration.simulation.temporalite.aah;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAah0MoisTravailleAvantSimulation extends CommunTests {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private IndividuService individuService;

    @Test
    void simulerPopulationAahNonTravailleAvantSimulationProchaineDeclarationMois0() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€
        // 0 mois travaillé avant simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(0);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi);
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
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
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 64€ (simulateur CAF : 52€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(64);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 64€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(64);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 64€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // TODO montant : écart de 37€ avec CAF
        // Alors les prestations du cinquième mois 03/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€ (simulateur CAF : 156€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }
    
    @Test
    void simulerPopulationAahNonTravailleAvantSimulationProchaineDeclarationMois1() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€
        // 0 mois travaillé avant simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(1);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi);
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
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
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 64€ (simulateur CAF : 52€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(64);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 64€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(64);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 64€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(64);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // TODO montant : écart de 37€ avec CAF
        // Alors les prestations du cinquième mois 03/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€ (simulateur CAF : 156€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }
    
    @Test
    void simulerPopulationAahNonTravailleAvantSimulationProchaineDeclarationMois2() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€
        // 0 mois travaillé avant simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(2);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi);
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
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
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 64€ (simulateur CAF : 52€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 64€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(129);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 64€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(129);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // TODO montant : écart de 37€ avec CAF
        // Alors les prestations du cinquième mois 03/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€ (simulateur CAF : 156€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(129);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }
    
    @Test
    void simulerPopulationAahNonTravailleAvantSimulationProchaineDeclarationMois3() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€
        // 0 mois travaillé avant simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 12 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(3);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi);
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
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
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 64€ (simulateur CAF : 52€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(64);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 64€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(64);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 64€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // TODO montant : écart de 37€ avec CAF
        // Alors les prestations du cinquième mois 03/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€ (simulateur CAF : 156€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }
}
