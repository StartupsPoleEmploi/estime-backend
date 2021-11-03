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

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAideLogementProchaineDeclarationMois0Tests extends Commun {

    @Autowired
    private IndividuService individuService;

    private static int PROCHAINE_DECLARATION_TRIMESTRIELLE = 0;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerAPLProchaineDeclarationMois0() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
        // Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 500€, déclaration trimetrielle en M, non travaillé au cours des 3 derniers mois
        // APL 310€
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiAPL(PROCHAINE_DECLARATION_TRIMESTRIELLE);

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
                assertThat(rsa.getMontant()).isEqualTo(1018f);
            });
            assertThat(simulation.getMesAides().get(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(392f);
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
                assertThat(rsa.getMontant()).isEqualTo(1018f);
            });
            assertThat(simulation.getMesAides().get(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(392f);
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
                assertThat(rsa.getMontant()).isEqualTo(1018f);
            });
            assertThat(simulation.getMesAides().get(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(392f);
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
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(414f);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(422f);
            });
            assertThat(simulation.getMesAides().get(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(392f);
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
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(414f);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(422f);
            });
            assertThat(simulation.getMesAides().get(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(392f);
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
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(414f);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(422f);
            });
            assertThat(simulation.getMesAides().get(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(392f);
            });
        });
    }

    @Test
    void simulerALFProchaineDeclarationMois0() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, en couple, non propriétaire
        // Futur contrat CDI 35h, salaire net 1231€ brut 1583€,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 710€, déclaration trimetrielle en M, non travaillé au cours des 3 derniers mois
        // APL 370€
        // conjoint sans ressources finançières
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiALF(PROCHAINE_DECLARATION_TRIMESTRIELLE);

        // Lorsque je simule mes prestations le 20/10/2020
        initMocks("20-10-2020");
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);

        // Alors les prestations du premier mois 11/2020 sont :
        // RSA : 710€
        SimulationMensuelle simulationMois1 = simulationAides.getSimulationsMensuelles().get(0);
        assertThat(simulationMois1).satisfies(simulation -> {
            assertThat(simulation.getDatePremierJourMoisSimule()).satisfies(dateMoisSimule -> {
                assertThat(dateUtile.getMonthFromLocalDate(dateMoisSimule)).isEqualTo("11");
                assertThat(dateMoisSimule.getYear()).isEqualTo(2020);
            });
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(1018f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(438f);
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
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(1018f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(438f);
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
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(1018f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(438f);
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
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(414f);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(422f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(438f);
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
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(414f);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(422f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(438f);
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
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(414f);
            });
            assertThat(simulation.getMesAides().get(Aides.PRIME_ACTIVITE.getCode())).satisfies(ppa -> {
                assertThat(ppa.getMontant()).isEqualTo(422f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(438f);
            });
        });
    }

    @Test
    void simulerALSProchaineDeclarationMois0() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {

        // Si DE Français, date naissance 5/07/1986, code postal 44200, célibataire, seul depuis plus de 18 mois, non propriétaire
        // Futur contrat CDI 15h, salaire net 500€ 659€ brut ,kilométrage domicile -> taf = 10kms + 20 trajets
        // RSA 500€, déclaration trimetrielle en M, travaillé au cours des 3 derniers mois avec salaire 0 juillet, salaire 380 juin, salaire 0 mai
        // APL 310€
        DemandeurEmploi demandeurEmploi = creerDemandeurEmploiALS(PROCHAINE_DECLARATION_TRIMESTRIELLE);
        
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
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(271f);
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
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(271f);
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
            assertThat(simulation.getMesAides()).hasSize(2);
            assertThat(simulation.getMesAides().get(Aides.RSA.getCode())).satisfies(rsa -> {
                assertThat(rsa.getMontant()).isEqualTo(497f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(271f);
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
                assertThat(ppa.getMontant()).isEqualTo(141f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(271f);
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
                assertThat(ppa.getMontant()).isEqualTo(141f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(271f);
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
                assertThat(ppa.getMontant()).isEqualTo(141f);
            });
            assertThat(simulation.getMesAides().get(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())).satisfies(apl -> {
                assertThat(apl.getMontant()).isEqualTo(271f);
            });
        });
    }
}
