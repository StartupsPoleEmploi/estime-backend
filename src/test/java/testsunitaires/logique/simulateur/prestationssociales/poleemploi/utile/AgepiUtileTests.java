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
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;

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

	//Si DE avec montant ARE journalière brute = 28€
	//Mois simulé janvier 2022 (31 jours) 

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setNombreJoursRestants(100f);
	allocationARE.setAllocationJournaliereBrute(28f);
	allocationARE.setSalaireJournalierReferenceBrut(40f);
	allocationARE.setHasDegressiviteAre(false);
	aidesPoleEmploi.setAllocationARE(allocationARE);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	Aide are = agepiUtile.creerAgepi(500f);

	List<String> messagesAlertesAttendus = new ArrayList<>();
	messagesAlertesAttendus.add(MessageInformatifEnum.AGEPI_IDF.getMessage());
	messagesAlertesAttendus.add(MessageInformatifEnum.AGEPI_AM_DELAI_DEMANDE.getMessage());

	//le message d'alerte sur le délai de demande Agepi est présent
	assertThat(are.getMessagesAlerte()).isEqualTo(messagesAlertesAttendus);
    }
}
