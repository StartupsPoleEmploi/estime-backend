package testsunitaires.logique.simulateur.prestationssociales.poleemploi.utile;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AideMobiliteUtile;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class AideMobiliteUtileTestsHorsMayotte {

	public static final String CODE_POSTAL_METROPOLITAIN = "72300";
	public static final int PREMIER_MOIS_SIMULATION = 1;

	@Autowired
	private AideMobiliteUtile aideMobiliteUtile;

	@Autowired
	Utile testUtile;

	@Configuration
	@ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
	public static class SpringConfig {

	}

	/**************************************  département hors Mayotte  *****************************************************/

	@Test
	void isEligibleTest1() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms  et allocation journalière net = 29.38€
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		futurContratTravail.setDistanceKmDomicileTravail(31);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireASS(false);
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(true);
		beneficiaireAides.setBeneficiaireRSA(false);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationARE allocationARE = new AllocationARE();
		allocationARE.setAllocationJournaliereNet(29.38f);
		aidesPoleEmploi.setAllocationARE(allocationARE);
		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE est éligible à l'aideMobiliteUtile
		assertThat(isEligible).isTrue();
	}

	@Test
	void isEligibleTest2() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et allocation journalière net = 14.38€
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		futurContratTravail.setDistanceKmDomicileTravail(31);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireASS(false);
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(true);
		beneficiaireAides.setBeneficiaireRSA(false);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationARE allocationARE = new AllocationARE();
		allocationARE.setAllocationJournaliereNet(14.38f);
		aidesPoleEmploi.setAllocationARE(allocationARE);
		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE est éligible à l'aideMobiliteUtile
		assertThat(isEligible).isTrue();
	}

	@Test
	void isEligibleTest3() throws ParseException {

		//Si DE France Métropolitaine, futur contrat CDD de 3 mois, kilométrage domicile -> taf = 31kms et allocation journalière net = 29.38€
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setDistanceKmDomicileTravail(31);
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDD.name());
		futurContratTravail.setNombreMoisContratCDD(3);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireASS(false);
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(true);
		beneficiaireAides.setBeneficiaireRSA(false);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationARE allocationARE = new AllocationARE();
		allocationARE.setAllocationJournaliereNet(29.38f);
		aidesPoleEmploi.setAllocationARE(allocationARE);
		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE est éligible à l'aideMobiliteUtile
		assertThat(isEligible).isTrue();
	}

	@Test
	void isEligibleTest4() throws ParseException {

		//Si DE France Métropolitaine, futur contrat CDD de 4 mois, kilométrage domicile -> taf = 31kms et allocation journalière net = 29.38€
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setDistanceKmDomicileTravail(31);
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDD.name());
		futurContratTravail.setNombreMoisContratCDD(4);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireASS(false);
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(true);
		beneficiaireAides.setBeneficiaireRSA(false);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationARE allocationARE = new AllocationARE();
		allocationARE.setAllocationJournaliereNet(29.38f);
		aidesPoleEmploi.setAllocationARE(allocationARE);
		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE est éligible à l'aideMobiliteUtile
		assertThat(isEligible).isTrue();
	}

	@Test
	void isEligibleTest5() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et sans ressources
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		futurContratTravail.setDistanceKmDomicileTravail(31);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireASS(false);
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(false);
		beneficiaireAides.setBeneficiaireRSA(false);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE est éligible à l'aideMobiliteUtile
		assertThat(isEligible).isTrue();
	}

	@Test
	void isEligibleTest6() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et bénéficiaire RSA
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		futurContratTravail.setDistanceKmDomicileTravail(31);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireASS(false);
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(false);
		beneficiaireAides.setBeneficiaireRSA(true);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE est éligible à l'aideMobiliteUtile
		assertThat(isEligible).isTrue();
	}

	@Test
	void isEligibleTest7() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et bénéficiaire AAH
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		futurContratTravail.setDistanceKmDomicileTravail(31);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireASS(false);
		beneficiaireAides.setBeneficiaireAAH(true);
		beneficiaireAides.setBeneficiaireARE(false);
		beneficiaireAides.setBeneficiaireRSA(false);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE est éligible à l'aideMobiliteUtile
		assertThat(isEligible).isTrue();
	}

	@Test
	void isEligibleTest8() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et bénéficiaire ASS
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		futurContratTravail.setDistanceKmDomicileTravail(31);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireASS(true);
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(false);
		beneficiaireAides.setBeneficiaireRSA(false);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE est éligible à l'aideMobiliteUtile
		assertThat(isEligible).isTrue();
	}

	@Test
	void isNotEligibleTest1() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 30kms et allocation journalière net = 14.38€
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setDistanceKmDomicileTravail(30);
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireARE(true);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationARE allocationARE = new AllocationARE();
		allocationARE.setAllocationJournaliereNet(14.38f);
		aidesPoleEmploi.setAllocationARE(allocationARE);
		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE n'est pas éligible à l'aideMobiliteUtile
		assertThat(isEligible).isFalse();
	}

	@Test
	void isNotEligibleTest2() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 25kms et allocation journalière net = 14.38€
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setDistanceKmDomicileTravail(25);
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireARE(true);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationARE allocationARE = new AllocationARE();
		allocationARE.setAllocationJournaliereNet(14.38f);
		aidesPoleEmploi.setAllocationARE(allocationARE);
		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE n'est pas éligible à l'aideMobiliteUtile
		assertThat(isEligible).isFalse();
	}

	@Test
	void isNotEligibleTest3() {

		//Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et allocation journalière net = 30.28€ 
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setDistanceKmDomicileTravail(30);
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireARE(true);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationARE allocationARE = new AllocationARE();
		allocationARE.setAllocationJournaliereNet(30.28f);
		aidesPoleEmploi.setAllocationARE(allocationARE);
		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE n'est pas éligible à l'aideMobiliteUtile
		assertThat(isEligible).isFalse();
	}

	@Test
	void isNotEligibleTest4() throws ParseException {

		//Si DE France Métropolitaine, futur contrat CDD de 2 mois, kilométrage domicile -> taf = 31kms et allocation journalière net = 14.28€
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

		FuturTravail futurContratTravail = new FuturTravail();
		futurContratTravail.setDistanceKmDomicileTravail(31);
		futurContratTravail.setTypeContrat(TypeContratTravailEnum.CDD.name());
		futurContratTravail.setNombreMoisContratCDD(2);
		demandeurEmploi.setFuturTravail(futurContratTravail);

		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireARE(true);
		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationARE allocationARE = new AllocationARE();
		allocationARE.setAllocationJournaliereNet(14.28f);
		aidesPoleEmploi.setAllocationARE(allocationARE);
		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

		//Lorsque l'on vérifie son éligibilité
		boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);

		//Alors le DE n'est pas éligible à l'aideMobiliteUtile
		assertThat(isEligible).isFalse();
	}

	/******************************************* tests calculerMontantAideMobiliteTest1 *********************************************************************************************/

//	@Test
//	void calculerMontantAideMobiliteTest1() {
//
//		//Si kilométrage domicile -> taf = 32kms + 20 trajets
//		//et nbr heures hebdo travaillées = 10h
//		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
//
//		FuturTravail futurTravail = new FuturTravail();
//		futurTravail.setTypeContrat(TypesContratTravail.CDI.name());
//		futurTravail.setNombreHeuresTravailleesSemaine(10);
//		futurTravail.setDistanceKmDomicileTravail(32);
//		futurTravail.setNombreTrajetsDomicileTravail(20);
//		demandeurEmploi.setFuturTravail(futurTravail);
//
//		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
//		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
//		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
//
//		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
//		beneficiaireAides.setBeneficiaireARE(true);
//		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
//
//		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
//		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
//		AllocationARE allocationARE = new AllocationARE();
//		allocationARE.setAllocationJournaliereNet(34.38f);
//		aidesPoleEmploi.setAllocationARE(allocationARE);
//		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
//		demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
//
//		//Lorsque l'on calcul le montant de l'aide à la mobilité
//		Aide aideMobilite = aideMobiliteUtile.simulerAide(demandeurEmploi);
//
//		//alors le montant retourné est de 286€
//		assertThat(aideMobilite.getMontant()).isEqualTo(286f);
//	}
//
//	@Test
//	void calculerMontantAideMobiliteTest2() {
//
//		//Si kilométrage domicile -> taf = 0kms + 0 trajet
//		//et nbr heures hebdo travaillées = 10h
//		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
//
//		FuturTravail futurTravail = new FuturTravail();
//		futurTravail.setTypeContrat(TypesContratTravail.CDI.name());
//		futurTravail.setNombreHeuresTravailleesSemaine(10);
//		demandeurEmploi.setFuturTravail(futurTravail);
//
//		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
//		informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
//		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
//
//		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
//		beneficiaireAides.setBeneficiaireARE(true);
//		demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
//
//		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
//		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
//		AllocationARE allocationARE = new AllocationARE();
//		allocationARE.setAllocationJournaliereNet(34.38f);
//		aidesPoleEmploi.setAllocationARE(allocationARE);
//		ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
//		ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
//
//		//Lorsque l'on calcul le montant de l'aide à la mobilité
//		Aide aideMobilite = aideMobiliteUtile.simulerAide(demandeurEmploi);
//
//		//alors le montant retourné est de 30€
//		assertThat(aideMobilite.getMontant()).isEqualTo(30f);
//	}
}
