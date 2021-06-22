package testsunitaires.estime.services.controleurs.demandeuremploi;

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
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utiletests.BouchonDemandeur;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class BeneficiaireAidesSocialesControleurTests {
    
    @Autowired
    private IndividuService individuService;
    
    @Autowired
    private BouchonDemandeur bouchonDemandeur;
    
    @Configuration
    @ComponentScan({"utiletests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void controlerDonneeesEntreeBeneficiaireAidesSocialesTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(null);
        demandeurEmploi.setFuturTravail(bouchonDemandeur.creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(bouchonDemandeur.creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
               
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "beneficiaireAidesSociales"));
    }    
}
