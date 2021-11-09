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
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurRsaProchaineDeclarationMois3Tests extends Commun {

    @Autowired
    private IndividuService individuService;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    private static final int PROCHAINE_DECLARATION_TRIMESTRIELLE = 3;

    @Test
    void simulerMesRessourcesFinancieresTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
        // Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 500€, déclaration trimetrielle en M3, non travaillé au cours des 3 derniers mois
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
        demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks("20-10-2020");
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // RSA : 500€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
        });
        // Alors les prestations du second mois 12/2020 sont :
        // RSA : 500€
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // PA : 118 (simulateur CAF : 111€)
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // PA : 118 (simulateur CAF : 111€)
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(162);
            });
        });
        // Alors les prestations du cinquième mois 03/2021 sont :
        // PA : 118€ (simulateur CAF : 111€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(162);
            });
        });
        // Alors les prestations du sixième mois 04/2021 sont :
        // PA : 174€ (simulateur CAF : 167€)
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(162);
            });
        });
    }

    @Test
    void simulerMesRessourcesFinancieresTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, en couple, non propriétaire
        // Futur contrat CDI 15h, salaire net 500€ brut 659€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 350€, déclaration trimetrielle en M3, non travaillé au cours des 3 derniers mois
        // AF 130€, PAGE 170€
        // conjoint salaire 380€
        // enfant 2 ans, enfant 4 ans
        boolean isEnCouple = true;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(350f);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        AidesFamiliales aidesFamiliales = new AidesFamiliales();
        aidesFamiliales.setAllocationSoutienFamilial(0);
        aidesFamiliales.setAllocationsFamiliales(130f);
        aidesFamiliales.setComplementFamilial(0);
        aidesFamiliales.setPrestationAccueilJeuneEnfant(170f);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesFamiliales(aidesFamiliales);

        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(380);
        salaireConjoint.setMontantBrut(508);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDate("09-09-2019"));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-03-2017"));
        ;

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks("20-10-2020");
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // RSA : 350€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(350);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
                assertThat(af.getMontant()).isEqualTo(130);
            });
            assertThat(simulation.getMesAides().get(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
                assertThat(paje.getMontant()).isEqualTo(170);
            });
        });
        // Alors les prestations du second mois 12/2020 sont :
        // RSA : 350€
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(350);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
                assertThat(af.getMontant()).isEqualTo(130);
            });
            assertThat(simulation.getMesAides().get(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
                assertThat(paje.getMontant()).isEqualTo(170);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // RSA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(3);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(350);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
                assertThat(af.getMontant()).isEqualTo(130);
            });
            assertThat(simulation.getMesAides().get(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
                assertThat(paje.getMontant()).isEqualTo(170);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // PA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(4);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(200);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(419);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
                assertThat(af.getMontant()).isEqualTo(130);
            });
            assertThat(simulation.getMesAides().get(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
                assertThat(paje.getMontant()).isEqualTo(170);
            });
        });
        // Alors les prestations du cinquième mois 03/2021 sont :
        // PA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(4);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(200);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(419);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
                assertThat(af.getMontant()).isEqualTo(130);
            });
            assertThat(simulation.getMesAides().get(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
                assertThat(paje.getMontant()).isEqualTo(170);
            });
        });
        // Alors les prestations du sixième mois 04/2021 sont :
        // PA : 367€ (simulateur CAF : 353€)
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(4);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(200);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(419);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
                assertThat(af.getMontant()).isEqualTo(130);
            });
            assertThat(simulation.getMesAides().get(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
                assertThat(paje.getMontant()).isEqualTo(170);
            });
        });
    }

    @Test
    void simulerMesRessourcesFinancieresTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
        // Futur contrat CDI 15h, salaire net 500€ brut 659€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 380€, déclaration trimetrielle en M3, travaillé au cours des 3 derniers mois avec salaire 380 juillet, salaire 380 juin, salaire 0 mai
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
        demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(380f);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(508, 380, 508, 380, 0, 0));
        // Lorsque je simule mes prestations le 23/07/2021
        initMocks("23-07-2021");
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 08/2021 sont :
        // RSA : 380€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("08");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(380);
            });
        });
        // Alors les prestations du second mois 09/2021 sont :
        // RSA : 380€
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("09");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(380);
            });
        });
        // Alors les prestations du troisième mois 10/2021 sont :
        // RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("10");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(380);
            });
        });
        // Alors les prestations du quatrième mois 11/2021 sont :
        // RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(270);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(123);
            });
        });
        // Alors les prestations du cinquième mois 12/2021 sont :
        // RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(270);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(123);
            });
        });
        // Alors les prestations du sixième mois 01/2022 sont :
        // RSA : 16 (simulateur CAF : 0€), PA : 294 (simulateur CAF : 291€)
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(270);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(123);
            });
        });
    }

    @Test
    void simulerMesRessourcesFinancieresTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
        // Futur contrat CDI 15h, salaire net 500€ brut 659€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 500€, déclaration trimetrielle en M, travaillé au cours des 3 derniers mois avec salaire 0€ juillet, salaire 380€ net juin, salaire 500€ net
        // mai
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
        demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
        demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(0, 0, 508, 380, 659, 500));
        // Lorsque je simule mes prestations le 23/07/2021
        initMocks("23-07-2021");
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 08/2021 sont :
        // RSA : 500€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("08");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
        });
        // Alors les prestations du second mois 09/2021 sont :
        // RSA : 500€
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("09");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
        });
        // Alors les prestations du troisième mois 10/2021 sont :
        // RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("10");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(1);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
        });
        // Alors les prestations du quatrième mois 11/2021 sont :
        // RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(196);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(245);
            });
        });
        // Alors les prestations du cinquième mois 12/2021 sont :
        // RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(196);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(245);
            });
        });
        // Alors les prestations du sixième mois 01/2022 sont :
        // RSA : 16 (simulateur CAF : 20€), PA : 294 (simulateur CAF : 290€)
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(196);
            });
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(245);
            });
        });
    }

    @Test
    void simulerMesRessourcesFinancieresTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
        // Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 500€, déclaration trimetrielle en M3, non travaillé au cours des 3 derniers mois
        // APL 310€
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getInformationsPersonnelles().setLogement(initLogement());
        demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
        demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesLogement(utileTests.creerAidePersonnaliseeLogement(310f));

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks("20-10-2020");
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // RSA : 500€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
            assertThat(simulation.getMesAides().get(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(310f);
            });
        });
        // Alors les prestations du second mois 12/2020 sont :
        // RSA : 500€
        SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
        assertThat(simulationMois2).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
                assertThat(als.getMontant()).isEqualTo(271f);
            });
        });
        // Alors les prestations du troisième mois 01/2021 sont :
        // PA : 118 (simulateur CAF : 111€)
        SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
        assertThat(simulationMois3).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(500);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
                assertThat(als.getMontant()).isEqualTo(271f);
            });
        });
        // Alors les prestations du quatrième mois 02/2021 sont :
        // PA : 118 (simulateur CAF : 111€)
        SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
        assertThat(simulationMois4).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(118);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
                assertThat(als.getMontant()).isEqualTo(271f);
            });
        });
        // Alors les prestations du cinquième mois 03/2021 sont :
        // PA : 118€ (simulateur CAF : 111€)
        SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
        assertThat(simulationMois5).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(118);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
                assertThat(als.getMontant()).isEqualTo(271f);
            });
        });
        // Alors les prestations du sixième mois 04/2021 sont :
        // PA : 174€ (simulateur CAF : 167€)
        SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
        assertThat(simulationMois6).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(118);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
                assertThat(als.getMontant()).isEqualTo(271f);
            });
        });
    }
}
