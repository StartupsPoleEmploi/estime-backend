package testsunitaires.services.controleurs.demandeuremploi;

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
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class SituationFamilialeControleurTests extends CommunTests {
    
    @Autowired
    private IndividuService individuService;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void controlerDonneeesEntreeSituationFamilialeTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        
        demandeurEmploi.setSituationFamiliale(null);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "situationFamiliale"));
    }
    
    @Test
    void controlerDonneeesEntreeSituationFamilialeTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        
        SituationFamiliale situationFamiliale = creerSituationFamiliale();
        situationFamiliale.setIsEnCouple(null);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "enCouple de situationFamiliale"));
    }
    
    @Test
    void controlerDonneeesEntreeSituationFamilialeTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAidesSociales(new BeneficiaireAidesSociales());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        
        SituationFamiliale situationFamiliale = creerSituationFamiliale();
        situationFamiliale.setConjoint(null);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "conjoint de situationFamiliale"));
    }
}
