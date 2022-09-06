package testsunitaires.logique.simulateur.prestationssociales.caf.utile;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.PrimeActiviteUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import utile.tests.Utile;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class PrimeActiviteUtileTests {

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    @Autowired
    protected Utile utileTests;
    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = utileTests.getDate("01-01-2022");
    }

    @Test
    void messageAlerteDemandePPATest1() {
	Aide ppa = primeActiviteUtile.creerAidePrimeActivite(500, false, dateDebutSimulation, 5);

	List<String> messagesAlertesAttendus = new ArrayList<>();
	messagesAlertesAttendus.add(String.format(MessageInformatifEnum.DEMANDE_PPA.getMessage(), "'avril"));

	//le message d'alerte sur la fin de droit ARE est présent ainsi que celui sur l'actualisation
	assertThat(ppa.getMessagesAlerte()).isEqualTo(messagesAlertesAttendus);
    }

    @Test
    void messageAlerteDemandePPATest2() {

	Aide ppa = primeActiviteUtile.creerAidePrimeActivite(500, false, dateDebutSimulation, 6);

	List<String> messagesAlertesAttendus = new ArrayList<>();
	messagesAlertesAttendus.add(String.format(MessageInformatifEnum.DEMANDE_PPA.getMessage(), "e mai"));

	//le message d'alerte sur la fin de droit ARE est présent ainsi que celui sur l'actualisation
	assertThat(ppa.getMessagesAlerte()).isEqualTo(messagesAlertesAttendus);
    }

}
