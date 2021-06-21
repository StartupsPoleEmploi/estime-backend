package fr.poleemploi.estime.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import fr.poleemploi.estime.clientsexternes.emploistoredev.EmploiStoreDevClient;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.test.utile.TestUtile;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class SimulerAidesSocialesRSAProchaineDeclarationMois1 {

    @Autowired
    private IndividuService individuService;

    @Autowired
    private TestUtile testUtile;

    @SpyBean
    private EmploiStoreDevClient detailIndemnisationPoleEmploiClient;

    @SpyBean
    private DateUtile dateUtile;
    
    private static final Integer PROCHAINE_DECLARATION_RSA = 1;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    //TODO: vérifier tous les montants PPA et RSA avec le simulateur CAF
    
    /***************************** Tests service simulerAidesSociales pour Demandeur RSA avec prochaine declaration au mois 0 ***************/
    
    /***************************** Tests service simulerAidesSociales pour Demandeur RSA avec 0 mois cumulé ASS + salaire *******************/

    @Test
    void simulerMesAidesSocialesProchaineDeclarationRSAMois1Test1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        //Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, af = 90 euros
        //0 mois cumulé ASS + salaire sur 3 derniers mois
        //futur contrat CDI, salaire brut 912 euros, soit 700 euros, 20h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
        

        //Lorsque je simule mes aides le 20/10/2020
        initMocks();
        SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

        //Alors les aides du premier mois 11/2020 sont :
        // AGEPI : 400 euros, Aide mobilité : 450 euros, RSA : 848f euros
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
                assertThat(aideMobilite.getMontant()).isEqualTo(450);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        }); 
        //Alors les aides du second mois 12/2020 sont :
        //RSA : 848f euros (simulateur CAF: ? euros)
        SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        });
        //Alors les aides du troisième mois 01/2021 sont :
        //RSA : 848f (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        });
        //Alors les aides du quatrième mois 02/2021 sont :
        //PPA : 430 (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
        //Alors les aides du cinquième mois 03/2021 sont :
        //Prime d'activité : 430 euros (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
        //Alors les aides du sixième mois 04/2021 sont :
        //Prime d'activité : 430 euros (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
    }

    @Test
    void simulerMesAidesSocialesProchaineDeclarationRSAMois1Test2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        //Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, af = 90 euros
        //1 mois cumulé rsa + salaire sur 3 derniers mois
        //futur contrat CDI, 700 euros par mois, 20h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);        
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireMoisDemande);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);


        //Lorsque je simule mes aides le 20/10/2020
        initMocks();
        SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

        //Alors les aides du premier mois 11/2020 sont :
        // AGEPI : 400 euros, Aide mobilité : 258 euros, RSA : 848f euros
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
                assertThat(aideMobilite.getMontant()).isEqualTo(450);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        }); 
        //Alors les aides du second mois 12/2020 sont :
        //RSA : 848f euros
        SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        });
        //Alors les aides du troisième mois 01/2021 sont :
        //RSA : 848f (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        });
        //Alors les aides du quatrième mois 02/2021 sont :
        //Prime d'activité : 430 euros (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
        //Alors les aides du cinquième mois 03/2021 sont :
        //Prime d'activité : 430 euros (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
        //Alors les aides du sixième mois 04/2021 sont :
        //Prime d'activité : 430 euros (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
    }

    @Test
    void simulerMesAidesSocialesProchaineDeclarationRSAMois1Test3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        //Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, af = 90 euros
        //Salaire m-1 par rapport au début de la simulation : 1200 euros
        //2 mois cumulé rsa + salaire sur 3 derniers mois
        //futur contrat CDI, 700 euros par mois, 20h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets 
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
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
        initMocks();
        SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

        //Alors les aides du premier mois 11/2020 sont :
        // AGEPI : 400 euros, Aide mobilité : 258 euros, RSA : 848f euros
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
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        }); 
        //Alors les aides du second mois 12/2020 sont :
        //RSA : 848f euros
        SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        });
        //Alors les aides du troisième mois 01/2021 sont :
        //RSA : 848f (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        });
        //Alors les aides du quatrième mois 02/2021 sont :
        //Prime d'activité : 430 euros (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
        //Alors les aides du cinquième mois 03/2021 sont :
        //Prime d'activité : 430 euros (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa).isNotNull();
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
        //Alors les aides du sixième mois 04/2021 sont :
        //Prime d'activité : 430 euros (Simulateur CAF : ? euros), RSA : 148f (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa).isNotNull();
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
    }

    @Test
    void simulerMesAidesSocialesProchaineDeclarationRSAMois1Test4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        //Si DE Français de France métropolitaine né le 5/07/1986, célibataire, 1 enfant à charge de 9ans, af = 90 euros
        //3 mois cumulé rsa + salaire sur 3 derniers mois
        //futur contrat CDI, 700 euros par mois, 20h/semaine, kilométrage domicile -> taf = 80kms + 20 trajets        
        
        
        DemandeurEmploi demandeurEmploi = createDemandeurEmploi();
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);        
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireMoisDemande);
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireMoisMoins1Mois);
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(750);
        salaireMoisMoins2Mois.setMontantBrut(975); 
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireMoisMoins2Mois);
        demandeurEmploi.getRessourcesFinancieres().setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        
        //Lorsque je simule mes aides le 20/10/2020
        initMocks();
        SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

        //Alors les aides du premier mois 11/2020 sont :
        // AGEPI : 400 euros, Aide mobilité : 258 euros, RSA : 848f euros
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
                assertThat(aideMobilite.getMontant()).isEqualTo(450);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        }); 
        //Alors les aides du second mois 12/2020 sont :
        //RSA : 848f euros
        SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        });
        //Alors les aides du troisième mois 01/2021 sont :
        //RSA : 848f (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(848f);
            });
        });
        //Alors les aides du quatrième mois 02/2021 sont :
        //Prime d'activité : 430 euros (simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
        //Alors les aides du cinquième mois 03/2021 sont :
        //Prime d'activité : 430 euros (Simulateur CAF : ? euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
        //Alors les aides du sixième mois 04/2021 sont :
        //Prime d'activité : 430 euros (Simulateur CAF : 413 euros), RSA : 174 (simulateur CAF : ? euros)
        SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(430);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(174);
            });
        });
    }
    
    private DemandeurEmploi createDemandeurEmploi() throws ParseException {
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNom("LANCELEUR");
        informationsPersonnelles.setPrenom("Jérémie");
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        informationsPersonnelles.setNationalite(Nationalites.FRANCAISE.getValeur());
        informationsPersonnelles.setCodePostal("44200");
        informationsPersonnelles.setIsProprietaireSansPretOuLogeGratuit(false);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        situationFamiliale.setIsSeulPlusDe18Mois(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDI.name());        
        Salaire salaire = new Salaire();
        salaire.setMontantNet(700);
        salaire.setMontantBrut(912);        
        futurTravail.setSalaire(salaire);        
        futurTravail.setDistanceKmDomicileTravail(80);
        futurTravail.setNombreHeuresTravailleesSemaine(20);
        futurTravail.setNombreTrajetsDomicileTravail(12);
        demandeurEmploi.setFuturTravail(futurTravail);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(90);
        allocationsCAF.setAllocationMensuelleNetRSA(400f);
        allocationsCAF.setProchaineDeclarationRSA(PROCHAINE_DECLARATION_RSA);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres); 

        return demandeurEmploi;
    }

    void initMocks() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        //mock création date de demande de simulation
        doReturn(testUtile.getDate("20-10-2020")).when(dateUtile).getDateJour();

        //mock retour appel détail indemnisation de l'ESD 
        DetailIndemnisationESD detailIndemnisationESD = testUtile.createDetailIndemnisationESDPopulationASS();        
        doReturn(detailIndemnisationESD).when(detailIndemnisationPoleEmploiClient).callDetailIndemnisationEndPoint(Mockito.any(String.class)); 
    }
}
