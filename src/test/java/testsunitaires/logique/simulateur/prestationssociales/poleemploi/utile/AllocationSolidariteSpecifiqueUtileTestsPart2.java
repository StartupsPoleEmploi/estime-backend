package testsunitaires.logique.simulateur.prestationssociales.poleemploi.utile;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AllocationSolidariteSpecifiqueUtileTestsPart2 {

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

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

    /***********  tests du calcul montant ASS **********/

    @Test
    void calculerMontantTest1() throws ParseException {

	//Si DE avec montant ASS journalier net = 16,89€
	//Mois simulé janvier 2022 (31 jours) 
	//Date derniere ouverture droit 01/08/2021 soit date fin droit 01/01/2022 (avant 3ème mois simulé)

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationJournaliereNet(16.89f);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	int numeroMoiSimule = 1;

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoiSimule, dateDebutSimulation);

	//alors 
	//le montant de l'ASS sur le mois de janvier 2022 est de 523€
	assertThat(ass.get().getMontant()).isEqualTo(523f);
    }

    @Test
    void calculerMontantTest2() throws ParseException {

	//Si DE avec montant ASS journalier net = 16,89€
	//Mois simulé février 2022 (28 jours) 
	//Date derniere ouverture droit 01/08/2021 soit date fin droit 01/01/2022 (avant 3ème mois simulé)

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationJournaliereNet(16.89f);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	int numeroMoiSimule = 2;

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoiSimule, dateDebutSimulation);

	//alors le montant de l'ASS sur le mois de février 2022 est de 472€
	assertThat(ass.get().getMontant()).isEqualTo(472f);
    }

    @Test
    void calculerMontantTest3() throws ParseException {

	//Si DE avec montant ASS journalier net = 16,89€ 
	//Mois simulé mars 2022 (31 jours)
	//Date derniere ouverture droit 01/12/2021 soit date fin droit 01/05/2022 (après 3ème mois simulé)

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationJournaliereNet(16.89f);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	int numeroMoiSimule = 3;

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoiSimule, dateDebutSimulation);

	//alors 
	//le montant de l'ASS sur le mois de mars 2022 est de 523€
	assertThat(ass.get().getMontant()).isEqualTo(523f);
	//le message d'alerte sur le renouvellement de l'aide n'est pas présent
	assertThat(ass.get().getMessagesAlerte()).isNull();
    }

    @Test
    void calculerMontantTest4() throws ParseException {

	//Si DE avec montant ASS journalier net = 16,89€
	//Mois simulé février 2022 (28 jours) 
	//Date derniere ouverture droit 14/09/2022 soit date fin droit 14/03/2022 (après 3ème mois simulé)

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationJournaliereNet(16.89f);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	int numeroMoiSimule = 4;

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoiSimule, dateDebutSimulation);

	//alors 
	//le montant de l'ASS sur le mois d'avril 2022 est de 506f
	assertThat(ass.get().getMontant()).isEqualTo(506f);
	//le message d'alerte sur le renouvellement de l'aide n'est pas présent
	assertThat(ass.get().getMessagesAlerte()).isNull();
	;
    }
}
