package testsintegration.simulation.temporalite.aah;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.services.DemandeurEmploiService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAah1MoisTravailleAvantSimulationTests extends Commun {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    @Test
    void simulerPopulationAahTravaille1MoisAvantSimulationProchaineDeclarationMois0() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€
        // travaillé 1 mois avant simulation dont 1 mois dans les 3 derniers mois avant la simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par
        // mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(0);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(1);
        demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utile.creerPeriodeTravailleeAvantSimulation(0, 0, 1038, 800, 0, 0));

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi, true, true, false);
        SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(4);
            assertThat(simulation.getMesAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
                assertThat(agepi).isNotNull();
            });
            assertThat(simulation.getMesAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
                assertThat(aideMobilite).isNotNull();
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 146€ (simulateur CAF : 122€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
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
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 180€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }

    @Test
    void simulerPopulationAahTravaille1MoisAvantSimulationProchaineDeclarationMois1() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€
        // travaillé 1 mois avant simulation dont 1 mois dans les 3 derniers mois avant la simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par
        // mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(1);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(1);
        demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utile.creerPeriodeTravailleeAvantSimulation(0, 0, 1038, 800, 0, 0));

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi, true, true, false);
        SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(4);
            assertThat(simulation.getMesAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
                assertThat(agepi).isNotNull();
            });
            assertThat(simulation.getMesAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
                assertThat(aideMobilite).isNotNull();
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 146€ (simulateur CAF : 122€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
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
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 180€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }

    @Test
    void simulerPopulationAahTravaille1MoisAvantSimulationProchaineDeclarationMois2() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€
        // travaillé 1 mois avant simulation dont 1 mois dans les 3 derniers mois avant la simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par
        // mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(2);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(1);
        demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utile.creerPeriodeTravailleeAvantSimulation(0, 0, 1038, 800, 0, 0));

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi, true, true, false);
        SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(4);
            assertThat(simulation.getMesAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
                assertThat(agepi).isNotNull();
            });
            assertThat(simulation.getMesAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
                assertThat(aideMobilite).isNotNull();
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 146€ (simulateur CAF : 122€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(210);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(210);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
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
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(210);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 180€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }

    @Test
    void simulerPopulationAahTravaille1MoisAvantSimulationProchaineDeclarationMois3() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€
        // travaillé 1 mois avant simulation dont 1 mois dans les 3 derniers mois avant la simulation
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par
        // mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi(3);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(1);
        demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utile.creerPeriodeTravailleeAvantSimulation(0, 0, 1038, 800, 0, 0));

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi, true, true, false);
        SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(4);
            assertThat(simulation.getMesAides().get(AideEnum.AGEPI.getCode())).satisfies(agepi -> {
                assertThat(agepi).isNotNull();
            });
            assertThat(simulation.getMesAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
                assertThat(aideMobilite).isNotNull();
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 146€ (simulateur CAF : 122€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
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
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });

        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 180€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aah -> {
                assertThat(aah.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode())).satisfies(asf -> {
                assertThat(asf.getMontant()).isEqualTo(117);
            });
        });
    }

}
