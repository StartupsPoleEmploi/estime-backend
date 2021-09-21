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

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurRsaProchaineDeclarationMois1 extends CommunTests {

    @Autowired
    private IndividuService individuService;

    private static int PROCHAINE_DECLARATION_RSA = 1;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerMesRessourcesFinancieresTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
        // Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 500€, déclaration trimetrielle en M1, non travaillé au cours des 3 derniers mois
        // APL 310€
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
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
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationRSA(PROCHAINE_DECLARATION_RSA);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationsLogement(utileTests.creerallocationsLogement(310f));

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks("20-10-2020");
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // RSA : 497€ (simulateur CAF : 497€)
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
        });
        // Alors les prestations du second mois 12/2020 sont :
        // RSA : 497€ (simulateur CAF : 497€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // RSA : 497€ (simulateur CAF : 497€)
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // PA : 176 (simulateur CAF : 167€)
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497);
            });
        });
        // Alors les prestations du cinquième mois 03/2021 sont :
        // PA : 176€ (simulateur CAF : 167€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(176);
            });
        });
        // Alors les prestations du sixième mois 04/2021 sont :
        // PA : 176€ (simulateur CAF : 167€)
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(176);
            });
        });
    }

    @Test
    void simulerMesRessourcesFinancieresTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, en couple, non propriétaire
        // Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 170€, déclaration trimetrielle en M1, non travaillé au cours des 3 derniers mois
        // APL 420€
        // conjoint salaire 700€
        // enfant 4 ans (05/03/2017)
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
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
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(170f);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationRSA(PROCHAINE_DECLARATION_RSA);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationsLogement(utileTests.creerallocationsLogement(420f));

        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(700);
        salaireConjoint.setMontantBrut(912);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-03-2017"));
        ;

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks("20-10-2020");
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // RSA : 175€ (simulateur CAF : 149€), PA : 431 (simulateur CAF : 452€)
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(170);
            });
        });
        // Alors les prestations du second mois 12/2020 sont :
        // RSA : 175€ (simulateur CAF : 149€), PA : 431 (simulateur CAF : 452€)
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(431);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(175);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // RSA : 175€ (simulateur CAF : 149€), PA : 431 (simulateur CAF : 452€)
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(431);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(175);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // PA : 277 (simulateur CAF : 263€)
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(431);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(175);
            });
        });
        // Alors les prestations du cinquième mois 03/2021 sont :
        // PA : 277€ (simulateur CAF : 263€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(277);
            });
        });
        // Alors les prestations du sixième mois 04/2021 sont :
        // PA : 277€ (simulateur CAF : 263€)
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides().size()).isEqualTo(1);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(277);
            });
        });
    }
}
