package testsunitaires.logique.simulateur.prestationssociales.poleemploi.utile;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
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
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.services.ressources.Aide;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AgepiUtileTests {

    @Autowired
    private AgepiUtile agepiUtile;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
    }

    @Test
    void messageAlerteAgepiTest() throws ParseException {

	Aide agepi = agepiUtile.creerAgepi(500f);

	List<String> messagesAlertesAttendus = new ArrayList<>();
	messagesAlertesAttendus.add(MessageInformatifEnum.AGEPI_IDF.getMessage());
	messagesAlertesAttendus.add(MessageInformatifEnum.AGEPI_AM_DELAI_DEMANDE.getMessage());

	//le message d'alerte sur le délai de demande Agepi est présent
	assertThat(agepi.getMessagesAlerte()).isEqualTo(messagesAlertesAttendus);
    }
}
