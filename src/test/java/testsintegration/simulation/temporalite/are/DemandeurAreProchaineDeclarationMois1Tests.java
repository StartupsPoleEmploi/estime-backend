package testsintegration.simulation.temporalite.are;

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
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAreProchaineDeclarationMois1Tests extends Commun {

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    private static int PROCHAINE_DECLARATION_TRIMESTRIELLE = 1;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerMesRessourcesFinancieresTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
	// RSA 500€, déclaration trimetrielle en M1, non travaillé au cours des 3 derniers mois
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
	demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
	demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks("20-10-2020");
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 497€ (simulateur CAF : 497€)
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).isEmpty();
	});
	// Alors les prestations du second mois 12/2020 sont :
	// RSA : 497€ (simulateur CAF : 497€)
	// ALS
	SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(81);
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
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(81);
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
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(81);
	    });
	});
	// Alors les prestations du cinquième mois 03/2021 sont :
	// PA : 242€ (simulateur CAF : 233€, pnds : 240€)
	SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(241);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// PA : 242€ (simulateur CAF : 233€, pnds : 240€)
	SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(241);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, date naissance 5/07/1986, code postal 44200, en couple, non propriétaire
	// Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
	// RSA 170€, déclaration trimetrielle en M1, non travaillé au cours des 3 derniers mois
	// conjoint salaire 700€
	// enfant 4 ans (05/03/2017)
	boolean isEnCouple = true;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);

	RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
	Salaire salaireConjoint = new Salaire();
	salaireConjoint.setMontantNet(700);
	salaireConjoint.setMontantBrut(912);
	ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieres(ressourcesFinancieresConjoint);

	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDate("05-03-2017"));

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks("20-10-2020");
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 175€ (simulateur CAF : 149€), PA : 431 (simulateur CAF : 452€)
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).isEmpty();
	});
	// Alors les prestations du second mois 12/2020 sont :
	// PA : 431€ (simulateur CAF : 441€, pnds : 430€)
	// RSA : 343 (simulateur CAF : ?€)
	SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(435);
	    });
	});
	// Alors les prestations du troisième mois 01/2021 sont :
	// PA : 431€ (simulateur CAF : 441€, pnds : 430€)
	// RSA : 343 (simulateur CAF : ?€)
	SimulationMensuelle simulationMois3 = simulationAides.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(435);
	    });
	});
	// Alors les prestations du quatrième mois 02/2021 sont :
	// PA : 431€ (simulateur CAF : 441€, pnds : 430€)
	// RSA : 343 (simulateur CAF : ?€)
	SimulationMensuelle simulationMois4 = simulationAides.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(435);
	    });
	});
	// Alors les prestations du cinquième mois 03/2021 sont :
	// PA : 441€ (simulateur CAF : 427€, pnds : 440€)
	SimulationMensuelle simulationMois5 = simulationAides.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(439);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// PA : 441€ (simulateur CAF : 427€, pnds : 440€)
	SimulationMensuelle simulationMois6 = simulationAides.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(439);
	    });
	});
    }

    @Test
    void simulerMesRessourcesFinancieresTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

	// Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
	// Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
	// RSA 500€, déclaration trimetrielle en M1, non travaillé au cours des 3 derniers mois
	// APL 310€
	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setLogement(initLogement());
	demandeurEmploi.getSituationFamiliale().setIsEnCouple(false);
	demandeurEmploi.getSituationFamiliale().setIsSeulPlusDe18Mois(true);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(10);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(20);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
	demandeurEmploi.getRessourcesFinancieres().getAidesCAF().setAidesLogement(utile.creerAidePersonnaliseeLogement(310f));

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks("20-10-2020");
	SimulationAides simulationAides = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 497€ (simulateur CAF : 497€)
	SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(310f);
	    });
	});
	// Alors les prestations du second mois 12/2020 sont :
	// RSA : 497€ (simulateur CAF : 497€)
	// ALS
	SimulationMensuelle simulationMois2 = simulationAides.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulation -> {
	    assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(271f);
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
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(271f);
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
	    assertThat(simulation.getMesAides()).hasSize(1);
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(271f);
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
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(176);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(271f);
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
	    assertThat(simulation.getMesAides()).hasSize(2);
	    assertThat(simulation.getMesAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(176);
	    });
	    assertThat(simulation.getMesAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(271f);
	    });
	});
    }
}