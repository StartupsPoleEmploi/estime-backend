package testsintegration.simulation.temporalite.aah;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.PrestationsSociales;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SimulationPrestationsSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAah1MoisTravailleAvantSimulation extends CommunTests {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private IndividuService individuService;

    @Test
    void simulerPopulationAahTravaille1MoisAvantSimulation() throws Exception {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, asf = 117€
        // AAH = 900€, travaillé avant simulation en M-1 salaire net 800€
        // futur contrat CDI, salaire 1200€ brut par mois soit 940€ net par
        // mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(1);
        
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();       
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(0, 0));
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(1038, 800));
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(utileTests.creerSalaireAvantPeriodeSimulation(0, 0));        
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks(demandeurEmploi);
        SimulationPrestationsSociales simulationPrestationsSociales = individuService.simulerPrestationsSociales(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // AGEPI : 400€, Aide mobilité : 450€, AAH : 900€
        SimulationMensuelle simulationMois1 = simulationPrestationsSociales.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(3);
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.AGEPI.getCode())).satisfies(agepi -> {
                assertThat(agepi.getMontant()).isEqualTo(400);
            });
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
                assertThat(aideMobilite.getMontant()).isEqualTo(504);
            });
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode()))
                    .satisfies(ass -> {
                        assertThat(ass.getMontant()).isEqualTo(900);
                    });
        });
        
        // Alors les prestations du second mois 12/2020 sont :
        // AAH : 900€
        // Prime d'activité : 146€ (simulateur CAF : 122€)
        SimulationMensuelle simulationMois2 = simulationPrestationsSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode()))
                    .satisfies(ass -> {
                        assertThat(ass.getMontant()).isEqualTo(900);
                    });
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
        });
        
        // Alors les prestations du troisième mois 01/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois3 = simulationPrestationsSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode()))
                    .satisfies(ass -> {
                        assertThat(ass.getMontant()).isEqualTo(900);
                    });
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
        });
        
        // Alors les prestations du quatrième mois 02/2021 sont :
        // AAH : 900€
        // Prime d'activité : 146€
        SimulationMensuelle simulationMois4 = simulationPrestationsSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode()))
                    .satisfies(ass -> {
                        assertThat(ass.getMontant()).isEqualTo(900);
                    });
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(146);
            });
        });
        
        //TODO montant : écart de 37€ avec CAF
        // Alors les prestations du cinquième mois 03/2021 sont :
        // AAH : 900€
        // Prime d'activité : 193€ (simulateur CAF : 156€)
        SimulationMensuelle simulationMois5 = simulationPrestationsSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode()))
                    .satisfies(ass -> {
                        assertThat(ass.getMontant()).isEqualTo(900);
                    });
        });
        
        // Alors les prestations du sixième mois 04/2021 sont :
        // AAH : 180€
        // Prime d'activité : 193€
        SimulationMensuelle simulationMois6 = simulationPrestationsSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesPrestationsSociales().size()).isEqualTo(2);
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(193);
            });
            assertThat(simulation.getMesPrestationsSociales().get(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode()))
                    .satisfies(ass -> {
                        assertThat(ass.getMontant()).isEqualTo(180);
                    });
        });
    }

}
