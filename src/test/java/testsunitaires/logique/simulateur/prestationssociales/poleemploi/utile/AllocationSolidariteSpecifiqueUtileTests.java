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

import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AllocationSolidariteSpecifiqueUtileTests {

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private Utile utileTests;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = utileTests.getDate("01-01-2022");
    }

    /***** tests nombre mois eligible ASS si pas travaillé avant la simulation *****/

    private DemandeurEmploi initDemandeurEmploiASS() {
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireASS(true);
	demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
	// Initialisation des salaires à zéro
	ressourcesFinancieres.setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(0, 0, 13));

	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationJournaliereNet(16.89f);
	allocationASS.setDateDerniereOuvertureDroit(dateUtile.enleverMoisALocalDate(dateDebutSimulation, 10));
	aidesPoleEmploi.setAllocationASS(allocationASS);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);

	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.setFuturTravail(futurTravail);

	return demandeurEmploi;
    }

    /*
     * Situations sans impact de la date d'ouverture ASS
     */

    @Test
    void getNombreMoisEligibleMoisPasTravailleTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire 0 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(4);
    }

    @Test
    void getNombreMoisEligibleMoisTravaillesAvecPause3MoisTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[6].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[5].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[4].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(4);
    }

    @Test
    void getNombreMoisEligible1MoisTravaillesTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(3);
    }

    @Test
    void getNombreMoisEligible2MoisTravaillesTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[1].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(2);
    }

    @Test
    void getNombreMoisEligible2MoisTravaillesAvecPauses1Et2MoisTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[4].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(2);
    }

    @Test
    void getNombreMoisEligible3MoisTravaillesTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[1].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(1);
    }

    @Test
    void getNombreMoisEligible3MoisTravaillesAvecPause1MoisTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[1].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(1);
    }

    @Test
    void getNombreMoisEligible3MoisTravaillesAvecPause2MoisTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[4].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(1);
    }

    @Test
    void getNombreMoisEligible3MoisTravaillesAvecPauses1Et2MoisTest() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[5].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(1);
    }

    /*
     * Situations avec impact de la date d'ouverture ASS
     */

    @Test
    void getNombreMoisEligible1MoisTravailleAvecOuvertureASSM0() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M0
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 1);
	// on positionne sur un autre jour que le premier jour du mois
	dateDerniereOuvertureDroit = dateUtile.ajouterJourALocalDate(dateDerniereOuvertureDroit, 1);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(4);
    }

    @Test
    void getNombreMoisEligible1MoisTravailleAvecOuvertureASSM0PremierDuMois() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M0
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 1);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(3);
    }

    @Test
    void getNombreMoisEligible2MoisTravaillesAvecOuvertureASSMMoins1() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 2);
	// on positionne sur un autre jour que le premier jour du mois
	dateDerniereOuvertureDroit = dateUtile.ajouterJourALocalDate(dateDerniereOuvertureDroit, 1);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[1].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(3);
    }

    @Test
    void getNombreMoisEligible3MoisTravaillesAvecOuvertureASSMMoins1PremierDuMois() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 2);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[1].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(2);
    }

    @Test
    void getNombreMoisEligible3MoisTravailles1MoisDePauseAvecOuvertureASSMMoins1PremierDuMois() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 2);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[1].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(3);
    }

    @Test
    void getNombreMoisEligible3MoisTravailles2MoisDePauseAvecOuvertureASSMMoins5() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 6);
	// on positionne sur un autre jour que le premier jour du mois
	dateDerniereOuvertureDroit = dateUtile.ajouterJourALocalDate(dateDerniereOuvertureDroit, 1);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[4].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(1);
    }

    @Test
    void getNombreMoisEligible3MoisTravailles2MoisDePauseAvecOuvertureASSMMoins5PremierDuMois() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[4].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(1);
    }

    @Test
    void getNombreMoisEligible4MoisTravaillesAvecOuvertureASSMMoins4() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 5);
	// on positionne sur un autre jour que le premier jour du mois
	dateDerniereOuvertureDroit = dateUtile.ajouterJourALocalDate(dateDerniereOuvertureDroit, 1);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[1].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isZero();
    }

    @Test
    void getNombreMoisEligible5MoisTravaillesAvecOuvertureASSMMoins4PremierDuMois() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 5);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[4].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[1].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[0].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isZero();
    }

    @Test
    void getNombreMoisEligible2MoisTravailles2MoisDePauseAvecOuvertureASSMMoins4PremierDuMois() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 5);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[3].getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(2);
    }

    @Test
    void getNombreMoisEligible1MoisTravaille2MoisDePauseAvecOuvertureASSMMoins3PremierDuMois() throws ParseException {

	//Si DE a cumulé ASS+Salaire plusieurs mois mais a eu une pause de cumul d'au moins 3 mois
	DemandeurEmploi demandeurEmploi = initDemandeurEmploiASS();
	// on positionne la date d'ouverture de droit au M-5
	LocalDate dateDerniereOuvertureDroit = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 4);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroit);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois()[2].getSalaire().setMontantMensuelNet(1000);

	//Lorsque je calcul le nombre de mois éligible à l'ASS
	int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	assertThat(nombreMoisEligible).isEqualTo(3);
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
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 6);
	allocationASS.setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	int numeroMoiSimule = 1;

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoiSimule, dateDebutSimulation);

	//alors 
	//le montant de l'ASS sur le mois de janvier 2022 est de 523€ (décembre reporté)
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
	allocationASS.setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 6);
	allocationASS.setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	int numeroMoiSimule = 2;

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoiSimule, dateDebutSimulation);

	//alors le montant de l'ASS sur le mois de février 2022 est de 523€ (montant de janvier reporté)
	assertThat(ass.get().getMontant()).isEqualTo(523f);
    }

    @Test
    void calculerMontantTest3() throws ParseException {

	//Si DE avec montant ASS journalier net = 16,89€
	//Mois simulé février 2022 (28 jours) 
	//Date derniere ouverture droit 01/08/2021 soit date fin droit 01/01/2022 (avant 3ème mois simulé)

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationJournaliereNet(16.89f);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	allocationASS.setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 6);
	allocationASS.setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	int numeroMoiSimule = 3;

	//Lorsque je calcul le montant de l'ASS sur le mois total 
	Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoiSimule, dateDebutSimulation);

	//alors le montant de l'ASS sur le mois de mars 2022 est de 523€ (montant de février reporté)
	assertThat(ass.get().getMontant()).isEqualTo(472f);
    }
}
