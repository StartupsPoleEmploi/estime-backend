package testsintegration.temporalite.aah;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class Demandeur3MoisTravaillesAvantSimulation {
    
    @Configuration
    @ComponentScan({"utiletests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

    @Autowired
    private IndividuService individuService;
    
    @Autowired
    private CommunUtile communUtile;
    
    @SpyBean
    private DateUtile dateUtile; 
    
    @Test
    void simulerPopulationAahTravaille3MoisAvantSimulation() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        //Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, af = 90 euros
        //AAH = 900 euros, 3 mois travaillé avant simulation
        //futur contrat CDI, salaire 1200 euros brut par mois soit 940 euros net par mois, 35h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = communUtile.createDemandeurEmploi();
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireMoisDemande);
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireMoisMoins1Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        
        //Lorsque je simule mes aides le 20/10/2020
        communUtile.initMocks(demandeurEmploi);
        SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

        //Alors les aides du premier mois 11/2020 sont :
        // AGEPI : 400 euros, Aide mobilité : 450 euros, AAH : 900 euros
        SimulationMensuelle simulationMois1 = simulationAidesSociales.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(3);
            assertThat(simulation.getMesAides().get(AidesSociales.AGEPI.getCode())).satisfies(agepi -> {
                assertThat(agepi.getMontant()).isEqualTo(400);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.AIDE_MOBILITE.getCode())).satisfies(aideMobilite -> {
                assertThat(aideMobilite.getMontant()).isEqualTo(504);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
                assertThat(ass.getMontant()).isEqualTo(900);
            });
        }); 
        //Alors les aides du second mois 12/2020 sont :
        //AAH : 900 euros
        //Prime d'activité : 222 euros (simulateur CAF : 211 euros)
        SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
                assertThat(ass.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(222);
            });
        });
        //Alors les aides du troisième mois 01/2021 sont :
        //AAH : 900 euros
        //Prime d'activité : 222 euros (simulateur CAF : 211 euros)
        SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
                assertThat(ass.getMontant()).isEqualTo(900);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(222);
            });
        });
        //Alors les aides du quatrième mois 02/2021 sont :
        //AAH : 180 euros
        //Prime d'activité : 222 euros (simulateur CAF : 211 euros)
        SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
                assertThat(ass.getMontant()).isEqualTo(180);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(222);
            });
        });
        //Alors les aides du cinquième mois 03/2021 sont :
        //AAH : 180 euros
        //Prime d'activité : 274 euros (simulateur CAF : 265 euros)
        SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(274);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
                assertThat(ass.getMontant()).isEqualTo(180);
            });
        });
        //Alors les aides du sixième mois 04/2021 sont :
        //AAH : 180 euros
        //Prime d'activité : 274 euros (simulateur CAF : 265 euros)
        SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(274);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(ass -> {
                assertThat(ass.getMontant()).isEqualTo(180);
            });
        });
    }    
}
