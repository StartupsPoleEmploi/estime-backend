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
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class SituationFamilialeUtileTests {

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private Utile utileTests;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void getNombreEnfantAChargeInferieurAgeTest1() throws ParseException {

	//si 1 enfant à charge de 15 ans
	boolean isEnCouple = false;
	int nbEnfants = 1;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS, isEnCouple, nbEnfants);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(15));

	//lorsque l'on appelle getNombreEnfantAChargeInferieurAgeLimite avec age limite Agepi.AGE_MAX_ENFANT
	int nombreEnfant = situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, Utile.AGE_MAX_ENFANT_AGEPI);

	//alors le nombre enfant à charge retournée est de 0
	assertThat(nombreEnfant).isZero();
    }

    @Test
    void getNombreEnfantAChargeInferieurAgeTest2() throws ParseException {

	//si 3 enfants à charge de 15 ans, 10 ans et 9 ans
	boolean isEnCouple = false;
	int nbEnfants = 3;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS, isEnCouple, nbEnfants);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(15));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(10));
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));

	//lorsque l'on appelle getNombreEnfantAChargeInferieurAgeLimite avec age limite Agepi.AGE_MAX_ENFANT
	int nombreEnfant = situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, Utile.AGE_MAX_ENFANT_AGEPI);

	//alors le nombre enfant à charge retournée est de 1
	assertThat(nombreEnfant).isEqualTo(1);
    }
}
