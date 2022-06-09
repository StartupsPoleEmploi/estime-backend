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
import fr.poleemploi.estime.services.DemandeurEmploiService;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAreProchaineDeclarationMois3Tests extends Commun {

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

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

	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(false);

	// Lorsque je simule mes prestations le 01/01/2022
	initMocks(demandeurEmploi.getFuturTravail().getSalaire().getMontantNet());
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(am -> {
		assertThat(am.getMontant()).isEqualTo(192f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(995f);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).isEmpty();
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// PA : 118 (simulateur CAF : 111€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).isEmpty();
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// PA : 118 (simulateur CAF : 111€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(77f);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// PA : 118€ (simulateur CAF : 111€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(77f);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// PA : 174€ (simulateur CAF : 167€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(77f);
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
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(false);
	AidesFamiliales aidesFamiliales = new AidesFamiliales();
	aidesFamiliales.setAllocationSoutienFamilial(0);
	aidesFamiliales.setAllocationsFamiliales(130f);
	aidesFamiliales.setComplementFamilial(0);
	aidesFamiliales.setPrestationAccueilJeuneEnfant(170f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesFamiliales(aidesFamiliales);

	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	Salaire salaireConjoint = new Salaire();
	salaireConjoint.setMontantNet(380);
	salaireConjoint.setMontantBrut(508);
	ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
	demandeurEmploi.getSituationFamiliale().getConjoint().setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDate("09-09-2019"));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDate("05-03-2017"));

	// Lorsque je simule mes prestations le 01/01/2022
	initMocks(demandeurEmploi.getFuturTravail().getSalaire().getMontantNet());
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// RSA : 350€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(4);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(192);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(130);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
		assertThat(paje.getMontant()).isEqualTo(170);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(995f);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// RSA : 350€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(130);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
		assertThat(paje.getMontant()).isEqualTo(170);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// RSA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(130);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode())).satisfies(paje -> {
		assertThat(paje.getMontant()).isEqualTo(170);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// PA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(128f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(130);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// PA : 32€ (simulateur CAF : 0€), PA : 319€ (simulateur CAF : 312€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(128f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(130);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// PA : 367€ (simulateur CAF : 353€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(128f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATIONS_FAMILIALES.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(130);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
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
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(utile.creerPeriodeTravailleeAvantSimulation(380, 508, 2));
	// Lorsque je simule mes prestations le 01/01/2022
	initMocks(demandeurEmploi.getFuturTravail().getSalaire().getMontantNet());
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// RSA : 380€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(192);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(995f);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// RSA : 380€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(1);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(73f);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// RSA : 54 (simulateur CAF : 38€), PA : 270 (simulateur CAF : 271€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(73f);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// RSA : 16 (simulateur CAF : 0€), PA : 294 (simulateur CAF : 291€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(73f);
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
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(500);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(659);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utile.creerSalaires(0, 0, 3);
	Salaire salaireMoisMoins1 = utile.creerSalaire(380, 508);
	Salaire salaireMoisMoins2 = utile.creerSalaire(380, 508);
	salaires = utile.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	salaires = utile.ajouterSalaire(salaires, salaireMoisMoins2, 2);
	periodeTravailleeAvantSimulation.setMois(utile.createMoisTravaillesAvantSimulation(salaires));
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);
	// Lorsque je simule mes prestations le 01/01/2022
	initMocks(demandeurEmploi.getFuturTravail().getSalaire().getMontantNet());
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(192);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(995f);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(1);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(1);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(73f);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(73f);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// RSA : 16 (simulateur CAF : 20€), PA : 294 (simulateur CAF : 290€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(148);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(73f);
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
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setLogement(initLogement());
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1231);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1583);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(PROCHAINE_DECLARATION_TRIMESTRIELLE);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(false);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAidesLogement(utile.creerAidePersonnaliseeLogement(310f));

	// Lorsque je simule mes prestations le 01/01/2022
	initMocks(demandeurEmploi.getFuturTravail().getSalaire().getMontantNet());
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 02/2022 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_RETOUR_EMPLOI.getCode())).satisfies(are -> {
		assertThat(are.getMontant()).isEqualTo(995f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_MOBILITE.getCode())).satisfies(af -> {
		assertThat(af.getMontant()).isEqualTo(192);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(310f);
	    });
	});
	// Alors les prestations du second mois 03/2022 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(1);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(272f);
	    });
	});
	// Alors les prestations du troisième mois 04/2022 sont :
	// PA : 118 (simulateur CAF : 111€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(1);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(272f);
	    });
	});
	// Alors les prestations du quatrième mois 05/2022 sont :
	// PA : 118 (simulateur CAF : 111€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("05");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(55f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(272f);
	    });
	});
	// Alors les prestations du cinquième mois 06/2022 sont :
	// PA : 118€ (simulateur CAF : 111€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("06");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(55f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(272f);
	    });
	});
	// Alors les prestations du sixième mois 07/2022 sont :
	// PA : 174€ (simulateur CAF : 167€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("07");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(55f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(als -> {
		assertThat(als.getMontant()).isEqualTo(272f);
	    });
	});
    }
}
