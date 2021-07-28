package testsintegration.simulation.temporalite.ass;

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

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SalaireAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAss2MoisCumulSalaireAvantSimulation extends CommunTests {

    @Autowired
    private IndividuService individuService;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerPopulationAssCumulSalaire2Mois() throws ParseException, JsonIOException, JsonSyntaxException,
            FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1
        // enfant à charge de 9ans, af = 90 euros
        // Salaire m-1 par rapport au début de la simulation : 1200 euros
        // Montant net journalier ASS = 16,89 euros, 2 mois cumulé ASS + salaire sur 3
        // derniers mois
        // futur contrat CDI, 1245 euros par mois, 20h/semaine, kilométrage domicile ->
        // taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(2);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salaireAvantPeriodeSimulationMoisDemande.setSansSalaire(false);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(1000);
        salaireMoisMoins1Mois.setMontantBrut(1200);
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSansSalaire(false);
        salairesAvantPeriodeSimulation
                .setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0);
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSansSalaire(true);
        salairesAvantPeriodeSimulation
                .setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);

        // Lorsque je simule mes aides le 20/10/2020
        initMocks();
        SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

        // Alors les aides du premier mois 11/2020 sont :
        // AGEPI : 400 euros, Aide mobilité : 258 euros, ASS : 506 euros
        SimulationMensuelle simulationMois1 = simulationAidesSociales.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(AidesSociales.AGEPI.getCode())).satisfies(agepi -> {
                assertThat(agepi).isNotNull();
                assertThat(agepi.getMontant()).isEqualTo(400);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
                assertThat(aideMobilite).isNotNull();
                assertThat(aideMobilite.getMontant()).isEqualTo(450);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode()))
                    .satisfies(ass -> {
                        assertThat(ass).isNotNull();
                        assertThat(ass.getMontant()).isEqualTo(506);
                    });
        });
        // Alors les aides du second mois 12/2020 sont :
        // aucune aide
        SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(0);
        });
        // Alors les aides du troisième mois 01/2021 sont :
        // Prime d'activité : 142 euros (simulateur CAF : 137 euros)
        SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(142);
            });
        });
        // Alors les aides du quatrième mois 02/2021 sont :
        // Prime d'activité : 142 euros (simulateur CAF : 137 euros)
        SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(142);
            });
        });
        // Alors les aides du cinquième mois 03/2021 sont :
        // Prime d'activité : 142 euros (simulateur CAF : 137 euros)
        SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa).isNotNull();
                assertThat(ppa.getMontant()).isEqualTo(142);
            });
        });
        // Alors les aides du sixième mois 04/2021 sont :
        // Prime d'activité : 422 euros (Simulateur CAF : 413 euros)
        SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa).isNotNull();
                assertThat(ppa.getMontant()).isEqualTo(422);
            });
        });
    }
}
