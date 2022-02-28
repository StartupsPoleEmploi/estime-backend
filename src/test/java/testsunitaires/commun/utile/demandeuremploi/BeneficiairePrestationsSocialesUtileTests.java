package testsunitaires.commun.utile.demandeuremploi;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.Utile;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class BeneficiaireAidesUtileTests {

    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private Utile utileTests;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void isBeneficiaireAREMiniTest1() throws ParseException {

	//si DE France Metropolitaine et montant journalier = 29,38€ (= seuil max.)
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), false, 0);
	demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().setCodePostal(CODE_POSTAL_METROPOLITAIN);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereBrute(29.56f);

	//lorsque l'on appelle isBeneficiaireAidePEouCAF
	boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);

	//alors le résultat est DE beneficiaire
	assertThat(beneficiaire).isTrue();
    }

    @Test
    void isBeneficiaireAREMiniTest2() throws ParseException {

	//si DE France Metropolitaine et montant journalier = 18,38€ (< seuil max.)
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), false, 0);
	demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().setCodePostal(CODE_POSTAL_METROPOLITAIN);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereBrute(18.38f);

	//lorsque l'on appelle isBeneficiaireAidePEouCAF
	boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);

	//alors le résultat est DE beneficiaire
	assertThat(beneficiaire).isTrue();
    }

    @Test
    void isBeneficiaireAREMiniTest3() throws ParseException {

	//si DE Mayotte et montant journalier = 14.68€ (= seuil max.)
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), false, 0);
	demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().setCodePostal(CODE_POSTAL_METROPOLITAIN);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereBrute(14.68f);

	//lorsque l'on appelle isBeneficiaireAidePEouCAF
	boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);

	//alors le résultat est DE beneficiaire
	assertThat(beneficiaire).isTrue();
    }

    @Test
    void isBeneficiaireAREMiniTest4() throws ParseException {

	//si DE Mayotte et montant journalier = 12.68€ (< seuil max.)
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), false, 0);
	demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().setCodePostal(CODE_POSTAL_MAYOTTE);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereBrute(12.68f);

	//lorsque l'on appelle isBeneficiaireAidePEouCAF
	boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);

	//alors le résultat est DE beneficiaire
	assertThat(beneficiaire).isTrue();
    }

    @Test
    void isNotBeneficiaireAREMiniTest1() throws ParseException {

	//si DE Mayotte et montant journalier = 39,38€ (> seuil max.)
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), false, 0);
	demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().setCodePostal(CODE_POSTAL_MAYOTTE);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereBrute(39.38f);

	//lorsque l'on appelle isBeneficiaireAidePEouCAF
	boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);

	//alors le résultat est DE beneficiaire
	assertThat(beneficiaire).isFalse();
    }

    @Test
    void isNotBeneficiaireAREMiniTest2() throws ParseException {

	//si DE Mayotte et montant journalier = 16.68€ (> seuil max.)
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), false, 0);
	demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().setCodePostal(CODE_POSTAL_MAYOTTE);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereBrute(16.68f);

	//lorsque l'on appelle isBeneficiaireAidePEouCAF
	boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);

	//alors le résultat est DE beneficiaire
	assertThat(beneficiaire).isFalse();
    }
}
