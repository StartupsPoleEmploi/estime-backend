package testsintegration.simulation.temporalite.aideLogement;

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
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAideLogementProchaineDeclarationMois2Tests extends Commun {

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;

    private static final int PROCHAINE_DECLARATION_TRIMESTRIELLE = 2;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerAPLProchaineDeclarationMois2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
	// Si DE Français, date naissance 05/07/1986, code postal 44200, 
	// en couple, 2 enfants 9 et 7 ans,
	// locataire d'un logement non meublé, loyer 500€, charges 50€, codeInsee 44109
	// APL 300€, RSA 500€, prochaine déclaration trimestrielle mois M+2
	// Salaire CDI 35h/semaine 940€ net, kilométrage domicile -> taf = 12kms + 20 trajets
	// Pas travaillé au cours des 3 derniers mois

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAPL(PROCHAINE_DECLARATION_TRIMESTRIELLE);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks("20-10-2020");
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(500f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(300f);
	    });
	});
	// Alors les prestations du second mois 12/2020 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(500f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(392f);
	    });
	});
	// Alors les prestations du troisième mois 01/2021 sont :
	// PA : 118 (simulateur CAF : 111€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(716f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(211f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(392f);
	    });
	});
	// Alors les prestations du quatrième mois 02/2021 sont :
	// PA : 118 (simulateur CAF : 111€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(716f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(211f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(392f);
	    });
	});
	// Alors les prestations du cinquième mois 03/2021 sont :
	// PA : 118€ (simulateur CAF : 111€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(716f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(211f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(392f);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// PA : 174€ (simulateur CAF : 167€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(112f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(631f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(376f);
	    });
	});
    }

    @Test
    void simulerALFProchaineDeclarationMois2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
	// Si DE Français, date naissance 05/07/1986, code postal 44200, 
	// en couple, 2 enfants 9 et 7 ans,
	// locataire d'un logement non meublé, loyer 500€, charges 50€, codeInsee 44109
	// APL 300€, RSA 500€, prochaine déclaration trimestrielle mois M+2
	// Salaire CDI 35h/semaine 940€ net, kilométrage domicile -> taf = 12kms + 20 trajets
	// Pas travaillé au cours des 3 derniers mois

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiALF(PROCHAINE_DECLARATION_TRIMESTRIELLE);

	// Lorsque je simule mes prestations le 20/10/2020
	initMocks("20-10-2020");
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 11/2020 sont :
	// RSA : 710€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(500f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(300f);
	    });
	});
	// Alors les prestations du second mois 12/2020 sont :
	// RSA : 710€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(500f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(438f);
	    });
	});
	// Alors les prestations du troisième mois 01/2021 sont :
	// PA : 258 (simulateur CAF : 260€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(716f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(211f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(438f);
	    });
	});
	// Alors les prestations du quatrième mois 02/2021 sont :
	// PA : 258 (simulateur CAF : 260€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("02");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(716f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(211f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(438f);
	    });
	});
	// Alors les prestations du cinquième mois 03/2021 sont :
	// PA : 258€ (simulateur CAF : 260€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("03");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(716f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(211f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(438f);
	    });
	});
	// Alors les prestations du sixième mois 04/2021 sont :
	// PA : 384€ (simulateur CAF : 390€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("04");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);
	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(112f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(631f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(438f);
	    });
	});
    }

    @Test
    void simulerALSProchaineDeclarationMois2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
	// Si DE Français, date naissance 05/07/1986, code postal 44200, 
	// célibataire, pas d'enfant,
	// locataire d'un logement non meublé, loyer 500€, charges 50€, codeInsee 44109
	// APL 300€, RSA 500€, prochaine déclaration trimestrielle mois M+2
	// Salaire CDI 35h/semaine 940€ net, kilométrage domicile -> taf = 12kms + 20 trajets
	// Pas travaillé au cours des 3 derniers mois

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiALS(PROCHAINE_DECLARATION_TRIMESTRIELLE);

	// Lorsque je simule mes prestations le 23/07/2021
	initMocks("23-07-2021");
	Simulation simulation = demandeurEmploiService.simulerAides(demandeurEmploi);

	// Alors les prestations du premier mois 08/2021 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois1 = simulation.getSimulationsMensuelles().get(0);
	assertThat(simulationMois1).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("08");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(500f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(300f);
	    });
	});
	// Alors les prestations du second mois 09/2021 sont :
	// RSA : 500€
	SimulationMensuelle simulationMois2 = simulation.getSimulationsMensuelles().get(1);
	assertThat(simulationMois2).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("09");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(500f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(271f);
	    });
	});
	// Alors les prestations du troisième mois 10/2021 sont :
	// RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
	SimulationMensuelle simulationMois3 = simulation.getSimulationsMensuelles().get(2);
	assertThat(simulationMois3).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("10");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(196f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(71f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(271f);
	    });
	});
	// Alors les prestations du quatrième mois 11/2021 sont :
	// RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
	SimulationMensuelle simulationMois4 = simulation.getSimulationsMensuelles().get(3);
	assertThat(simulationMois4).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(196f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(71f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(271f);
	    });
	});
	// Alors les prestations du cinquième mois 12/2021 sont :
	// RSA : 176 (simulateur CAF : 180€), PA : 196 (simulateur CAF : 200€)
	SimulationMensuelle simulationMois5 = simulation.getSimulationsMensuelles().get(4);
	assertThat(simulationMois5).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("12");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2021);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(3);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.RSA.getCode())).satisfies(rsa -> {
		assertThat(rsa.getMontant()).isEqualTo(196f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(71f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(271f);
	    });
	});
	// Alors les prestations du sixième mois 01/2022 sont :
	// RSA : 16 (simulateur CAF : 20€), PA : 294 (simulateur CAF : 290€)
	SimulationMensuelle simulationMois6 = simulation.getSimulationsMensuelles().get(5);
	assertThat(simulationMois6).satisfies(simulationMensuelle -> {
	    assertThat(simulationMensuelle.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
		assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("01");
		assertThat(dateMoisSimule.getYear()).isEqualTo(2022);
	    });
	    assertThat(simulationMensuelle.getRessourcesFinancieres().get(AideEnum.SALAIRE.getCode())).isNotNull();
	    assertThat(simulationMensuelle.getAides()).hasSize(2);

	    assertThat(simulationMensuelle.getAides().get(AideEnum.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
		assertThat(ppa.getMontant()).isEqualTo(208f);
	    });
	    assertThat(simulationMensuelle.getAides().get(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
		assertThat(apl.getMontant()).isEqualTo(272);
	    });
	});
    }
}
