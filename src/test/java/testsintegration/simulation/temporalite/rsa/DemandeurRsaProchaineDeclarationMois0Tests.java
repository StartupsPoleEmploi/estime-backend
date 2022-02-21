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

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.DemandeurEmploiService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurRsaProchaineDeclarationMois0Tests extends Commun {

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    private static int PROCHAINE_DECLARATION_TRIMESTRIELLE = 0;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerMesRessourcesFinancieresTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
	// RSA 500€, déclaration trimetrielle en M, non travaillé au cours des 3 derniers moi
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
	demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
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
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(565);
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
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(565);
	    });
	});
	// Alors les prestations du troisième mois 01/2021 sont :
	// RSA : 565 (simulateur CAF : ?€)
	SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(565);
	    });
	});
	// Alors les prestations du quatrième mois 02/2021 sont :
	// PA : 162 (simulateur CAF : 167€)
	SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
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
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
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
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(162);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, date naissance 5/07/1986, code postal 44200, en couple, non propriétaire
	// Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
	// RSA 710€, déclaration trimetrielle en M, non travaillé au cours des 3 derniers mois
	// conjoint sans ressources finançières
	boolean isEnCouple = true;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(710f);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks("20-10-2020");
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 710€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(847);
	    });
	});
	// Alors les prestations du second mois 12/2020 sont :
	// RSA : 710€
	SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(847);
	    });
	});
	// Alors les prestations du troisième mois 01/2021 sont :
	// PA : 258 (simulateur CAF : 260€)
	SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(847);
	    });
	});
	// Alors les prestations du quatrième mois 02/2021 sont :
	// PA : 258 (simulateur CAF : 260€)
	SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(57);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(347);
	    });
	});
	// Alors les prestations du cinquième mois 03/2021 sont :
	// PA : 258€ (simulateur CAF : 260€)
	SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(57);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(347);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// PA : 384€ (simulateur CAF : 390€)
	SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(57);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(347);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 15h, salaire net 500€ 659€ brut ,kilométrage domicile -> taf = 10kms + 20 trajets
	// RSA 500€, déclaration trimetrielle en M, travaillé au cours des 3 derniers mois avec salaire 0 juillet, salaire 380 juin, salaire 0 mai
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
	demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 3);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(380, 508);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);
	// Lorsque je simule mes prestations le 23/07/2021
	initMocks("23-07-2021");
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 08/2021 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("08");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(74);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(565);
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(74);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(565);
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(74);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(565);
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(196);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(196);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(196);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(245);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 15h, salaire net 500€ brut 659€,kilométrage domicile -> taf = 10kms + 20 trajets
	// RSA 500€, déclaration trimetrielle en M, travaillé au cours des 3 derniers mois avec salaire 380 juillet, salaire 380 juin, salaire 380 mai
	// APL 310€
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
	demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAllocationRSA(500f);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 3);
	Salaire salaireMoisMoins0 = utileTests.creerSalaire(380, 508);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(380, 508);
	Salaire salaireMoisMoins2 = utileTests.creerSalaire(380, 508);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins0, 0);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins2, 2);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieres().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);// Lorsque je simule mes prestations le 23/07/2021
	initMocks("23-07-2021");
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 08/2021 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("08");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(149);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(322);
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(149);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(322);
	    });
	});
	// Alors les prestations du troisième mois 10/2021 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 231€)
	SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("10");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(149);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(322);
	    });
	});
	// Alors les prestations du quatrième mois 11/2021 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 231€)
	SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(270);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(123);
	    });
	});
	// Alors les prestations du cinquième mois 12/2021 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 231€)
	SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(270);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(270);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(123);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
	// RSA 500€, déclaration trimetrielle en M, non travaillé au cours des 3 derniers mois
	// APL 310€
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setLogement(initLogement());
	demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
	demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
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
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(497);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(271f);
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(497);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(497);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(118);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(118);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
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
	    assertThat(simulation.getMesAides()).hasSize(3);
	    assertThat(simulation.getMesAides().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(118);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(271f);
	    });
	});
    }
}
