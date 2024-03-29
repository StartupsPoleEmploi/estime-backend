package testsunitaires.logique.simulateur.prestationssociales.caf.utile;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.AllocationAdultesHandicapesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import utile.tests.Utile;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class AllocationAdultesHandicapesUtileTests {

    @Autowired
    private AllocationAdultesHandicapesUtile allocationAdultesHandicapesUtile;

    @Autowired
    protected Utile utileTests;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void simulerAAHTest1() {

	// Si DE a déjà travaillé 6 mois sur les 6 derniers mois
	// futur travail avec salaire brut = 1000€ (supérieur au palier AAH)
	// AAH = 451€
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	FuturTravail futurTravail = new FuturTravail();
	Salaire salaire = new Salaire();
	salaire.setMontantMensuelNet(770);
	salaire.setMontantMensuelBrut(1000);
	futurTravail.setSalaire(salaire);

	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(true);
	demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

	demandeurEmploi.setFuturTravail(futurTravail);
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
	ressourcesFinancieres.setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1101, 850, 6));
	AidesCAF aidesCAF = new AidesCAF();
	aidesCAF.setAllocationAAH(450f);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	// Lorsque l'on appel simulerAAH
	Map<String, Aide> aidesPourCeMois = new HashMap<>();
	int numeroMoisSimule = 1;
	allocationAdultesHandicapesUtile.simulerAide(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);

	// Alors l'AAH n'apparait pas dans les prestations du mois
	// 60% du salaire = 600
	// AAH - 60% du salaire = 450 - 600 = -150 donc AAH = 0€
	assertThat(aidesPourCeMois).isEmpty();
    }

    @Test
    void simulerAAHTest2() {

	// Si DE a déjà travaillé 6 mois sur les 6 derniers mois
	// futur travail avec salaire brut = 400€
	// AAH = 450€
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(true);
	demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

	FuturTravail futurTravail = new FuturTravail();
	Salaire salaire = new Salaire();
	salaire.setMontantMensuelNet(295);
	salaire.setMontantMensuelBrut(400);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
	ressourcesFinancieres.setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1101, 850, 6));

	AidesCAF aidesCAF = new AidesCAF();
	aidesCAF.setAllocationAAH(450f);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	// Lorsque l'on appel simulerAAH
	Map<String, Aide> aidesPourCeMois = new HashMap<>();
	int numeroMoisSimule = 1;
	allocationAdultesHandicapesUtile.simulerAide(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);

	// Alors le montant de l'AAH est de 370€
	// 20% du salaire = 80
	// AAH - 60% du salaire = 450 - 80 = 370€
	assertThat(aidesPourCeMois.get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aideAAH -> {
	    assertThat(aideAAH.getMontant()).isEqualTo(370);
	});
    }

    @Test
    void simulerAAHTest3() {

	// Si DE a déjà travaillé 6 mois sur les 6 derniers mois
	// futur travail avec salaire brut = 550€ (au dessus du seuil)
	// AAH = 450€
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(true);
	demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

	FuturTravail futurTravail = new FuturTravail();
	Salaire salaire = new Salaire();
	salaire.setMontantMensuelNet(420);
	salaire.setMontantMensuelBrut(550);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
	ressourcesFinancieres.setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1101, 850, 6));

	AidesCAF aidesCAF = new AidesCAF();
	aidesCAF.setAllocationAAH(450f);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	// Lorsque l'on appel simulerAAH
	Map<String, Aide> aidesPourCeMois = new HashMap<>();
	int numeroMoisSimule = 1;
	allocationAdultesHandicapesUtile.simulerAide(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);

	// Alors le montant de l'AAH est de 150€
	// 60% du salaire = 330
	// AAH - 60% du salaire = 450 - 330 = 120€
	assertThat(aidesPourCeMois.get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aideAAH -> {
	    assertThat(aideAAH.getMontant()).isEqualTo(120);
	});
    }

    @Test
    void simulerAAHTest4() {

	// Si DE a déjà travaillé 6 mois sur les 6 derniers mois
	// futur travail avec salaire brut = 500€ (en dessous du seuil)
	// AAH = 450€
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(true);
	demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

	FuturTravail futurTravail = new FuturTravail();
	Salaire salaire = new Salaire();
	salaire.setMontantMensuelNet(370);
	salaire.setMontantMensuelBrut(500);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
	ressourcesFinancieres.setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1101, 850, 6));

	AidesCAF aidesCAF = new AidesCAF();
	aidesCAF.setAllocationAAH(450f);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	// Lorsque l'on appel simulerAAH
	Map<String, Aide> aidesPourCeMois = new HashMap<>();
	int numeroMoisSimule = 1;
	allocationAdultesHandicapesUtile.simulerAide(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);

	// Alors le montant de l'AAH est de 150€
	// 20% du salaire = 100
	// AAH - 60% du salaire = 450 - 100 = 350€
	assertThat(aidesPourCeMois.get(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aideAAH -> {
	    assertThat(aideAAH.getMontant()).isEqualTo(350);
	});
    }

}
