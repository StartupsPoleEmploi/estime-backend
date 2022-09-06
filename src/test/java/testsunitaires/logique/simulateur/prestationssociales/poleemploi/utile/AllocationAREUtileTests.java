package testsunitaires.logique.simulateur.prestationssociales.poleemploi.utile;

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
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AllocationAREUtileTests {

    @Autowired
    private AreUtile areUtile;

    @Autowired
    private Utile testUtile;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = testUtile.getDate("01-01-2022");
    }

    /***********  tests du calcul montant ARE **********/

    @Test
    void calculerMontantTest1() throws ParseException {

	//Si DE avec montant ARE journalière brute = 28€
	//Mois simulé janvier 2022 (31 jours) 

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setNombreJoursRestants(100);
	allocationARE.setAllocationJournaliereBrute(28f);
	allocationARE.setSalaireJournalierReferenceBrut(40f);
	allocationARE.setHasDegressiviteAre(false);
	aidesPoleEmploi.setAllocationARE(allocationARE);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	int numeroMoisSimule = 1;

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Float montantAre = areUtile.calculerMontantMensuelARE(demandeurEmploi, numeroMoisSimule, dateDebutSimulation);

	//alors 
	//le montant de l'ARE sur le mois de janvier 2022 est de 28€ (allocation journalière brute) * 31 (nombre de jours en janvier 2022)
	assertThat(montantAre).isEqualTo(28 * 31);
    }

    @Test
    void messageAlerteFinDeDroitARETest() throws ParseException {

	//Si DE avec montant ARE journalière brute = 28€
	//Mois simulé janvier 2022 (31 jours) 

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setNombreJoursRestants(100);
	allocationARE.setAllocationJournaliereBrute(28f);
	allocationARE.setSalaireJournalierReferenceBrut(40f);
	allocationARE.setHasDegressiviteAre(false);
	aidesPoleEmploi.setAllocationARE(allocationARE);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Aide are = areUtile.creerComplementARE(500, 0);

	List<String> messagesAlertesAttendus = new ArrayList<>();
	messagesAlertesAttendus.add(MessageInformatifEnum.ACTUALISATION_ARE.getMessage());
	messagesAlertesAttendus.add(MessageInformatifEnum.FIN_DE_DROIT_ARE.getMessage());

	//le message d'alerte sur la fin de droit ARE est présent ainsi que celui sur l'actualisation
	assertThat(are.getMessagesAlerte()).isEqualTo(messagesAlertesAttendus);
    }

    @Test
    void messageAlerteARETest() throws ParseException {

	//Si DE avec montant ARE journalière brute = 28€
	//Mois simulé janvier 2022 (31 jours) 

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setNombreJoursRestants(100);
	allocationARE.setAllocationJournaliereBrute(28f);
	allocationARE.setSalaireJournalierReferenceBrut(40f);
	allocationARE.setHasDegressiviteAre(false);
	aidesPoleEmploi.setAllocationARE(allocationARE);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Aide are = areUtile.creerComplementARE(500, 5);

	List<String> messagesAlertesAttendus = new ArrayList<>();
	messagesAlertesAttendus.add(MessageInformatifEnum.ACTUALISATION_ARE.getMessage());

	//le message d'alerte sur l'actualisation ARE est présent
	assertThat(are.getMessagesAlerte()).isEqualTo(messagesAlertesAttendus);
    }
}
