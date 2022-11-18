package testsunitaires.services.controleurs.aideservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.AidesService;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.Aide;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class AideServiceTests {

    @Autowired
    private AidesService aideService;

    @Autowired
    private AideUtile aideUtile;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void controlerDonneeesEntreeTest1() {
	assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
	    aideService.getAideByCode(null);
	}).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "codeAide"));

    }

    @Test
    void controlerDonneeesEntreeTest2() {
	assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
	    aideService.getAideByCode("Toto");
	}).getMessage()).isEqualTo(String.format(BadRequestMessages.CODE_AIDE_INCORRECT.getMessage(), aideUtile.getListeFormateeCodesAidePossibles()));

    }

    @Test
    void getAideByCodeTest1() {
	Aide agepi = aideService.getAideByCode(AideEnum.AGEPI.getCode());
	assertThat(agepi.getCode()).isEqualTo(AideEnum.AGEPI.getCode());
    }
}
