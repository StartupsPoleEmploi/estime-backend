package testsintegration.simulation.temporalite.rsa;

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
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurRsaProchaineDeclarationMois1 extends CommunTests {

    @Autowired
    private IndividuService individuService;
    
    private static int PROCHAINE_DECLARATION_RSA = 1;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void simulerMesAidesSocialesTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        //Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
        //Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
        //RSA 500€, déclaration trimetrielle en M1, non travaillé au cours des 3 derniers mois
        //APL 310€
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = demandeurBaseTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);        
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
        demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
        demandeurEmploi.getInformationsPersonnelles().setIsProprietaireSansPretOuLogeGratuit(false);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetRSA(500f);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setProchaineDeclarationRSA(PROCHAINE_DECLARATION_RSA);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationsLogementMensuellesNetFoyer(demandeurBaseTests.creerAllocationsLogementMensuellesNetFoyer(310f));
        
        //Lorsque je simule mes aides le 20/10/2020
        initMocks("20-10-2020");
        SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

        //Alors les aides du premier mois 11/2020 sont :
        //RSA : 497€ (simulateur CAF : 497€)
        SimulationMensuelle simulationMois1 = simulationAidesSociales.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497);
            });
        }); 
        //Alors les aides du second mois 12/2020 sont :
        //RSA : 497€ (simulateur CAF : 497€)
        SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497);
            });
        });
        //Alors les aides du troisième mois 01/2021 sont :
        //RSA : 497€ (simulateur CAF : 497€)
        SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497);
            });
        });
        //Alors les aides du quatrième mois 02/2021 sont :
        //PA : 176 (simulateur CAF : 167 euros)
        SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(176);
            });
        });
        //Alors les aides du cinquième mois 03/2021 sont :
        //PA : 176 euros (simulateur CAF : 167 euros)
        SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(176);
            });
        });
        //Alors les aides du sixième mois 04/2021 sont :
        //PA : 176 euros (simulateur CAF : 167 euros)
        SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(176);
            });
        });
    }
    
    
    @Test
    void simulerMesAidesSocialesTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        //Si DE Français, date naissance 5/07/1986, code postal 44200, en couple, non propriétaire
        //Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
        //RSA 300€, déclaration trimetrielle en M1, non travaillé au cours des 3 derniers mois
        //APL 420€
        //conjoint salaire 700€
        //enfant 4 ans (05/03/2017)
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = demandeurBaseTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);        
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getInformationsPersonnelles().setIsProprietaireSansPretOuLogeGratuit(false);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationMensuelleNetRSA(300f);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setProchaineDeclarationRSA(PROCHAINE_DECLARATION_RSA);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().setAllocationsLogementMensuellesNetFoyer(demandeurBaseTests.creerAllocationsLogementMensuellesNetFoyer(420f));
        
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(700);
        salaireConjoint.setMontantBrut(912);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);
        
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-03-2017"));;
        
        //Lorsque je simule mes aides le 20/10/2020
        initMocks("20-10-2020");
        SimulationAidesSociales simulationAidesSociales = individuService.simulerAidesSociales(demandeurEmploi);

        //Alors les aides du premier mois 11/2020 sont :
        //RSA : 175€ (simulateur CAF : 149€), PA : 431 (simulateur CAF : 452 euros)
        SimulationMensuelle simulationMois1 = simulationAidesSociales.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(175);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(431);
            });
        }); 
        //Alors les aides du second mois 12/2020 sont :
        //RSA : 175€ (simulateur CAF : 149€), PA : 431 (simulateur CAF : 452 euros)
        SimulationMensuelle simulationMois2 = simulationAidesSociales.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(175);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(431);
            });
        });
        //Alors les aides du troisième mois 01/2021 sont :
        //RSA : 175€ (simulateur CAF : 149€), PA : 431 (simulateur CAF : 452 euros)
        SimulationMensuelle simulationMois3 = simulationAidesSociales.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(AidesSociales.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(175);
            });
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(431);
            });
        });
        //Alors les aides du quatrième mois 02/2021 sont :
        //PA : 277 (simulateur CAF : 263 euros)
        SimulationMensuelle simulationMois4 = simulationAidesSociales.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(277);
            });
        });
        //Alors les aides du cinquième mois 03/2021 sont :
        //PA : 277 euros (simulateur CAF : 263 euros)
        SimulationMensuelle simulationMois5 = simulationAidesSociales.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(277);
            });
        });
        //Alors les aides du sixième mois 04/2021 sont :
        //PA : 277 euros (simulateur CAF : 263 euros)
        SimulationMensuelle simulationMois6 = simulationAidesSociales.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> { 
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(277);
            });
        });
    }
}
