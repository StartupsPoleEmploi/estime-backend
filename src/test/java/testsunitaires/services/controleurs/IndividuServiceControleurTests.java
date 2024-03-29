package testsunitaires.services.controleurs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.DemandeurEmploiService;
import fr.poleemploi.estime.services.exceptions.BadRequestException;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class IndividuServiceControleurTests {
    
    @Autowired
    private DemandeurEmploiService demandeurEmploiService;
    
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

    @Test
    void controlerDonneeesEntreeDemandeurEmploiTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            demandeurEmploiService.simulerAides(null);
        }).getMessage()).isEqualTo(BadRequestMessages.DEMANDEUR_EMPLOI_OBLIGATOIRE.getMessage());
    }
    
}
