package testsintegration.services.individuservice;

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

import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.DemandeurEmploiService;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class IndividuServiceControlerInformationsPersonnellesTests extends CommunTests {

    @Autowired
    private DemandeurEmploiService demandeurEmploiService;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void controlerDonneeesEntreeTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            demandeurEmploiService.simulerAides(null);
        }).getMessage()).isEqualTo(BadRequestMessages.DEMANDEUR_EMPLOI_OBLIGATOIRE.getMessage());
    }
    
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        demandeurEmploi.setInformationsPersonnelles(null);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            demandeurEmploiService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "informationsPersonnelles"));
    }
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(null);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            demandeurEmploiService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "dateNaissance de informationsPersonnelles"));
    }
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
        informationsPersonnelles.setCodePostal(null);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            demandeurEmploiService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "codePostal de informationsPersonnelles"));
    }
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
        informationsPersonnelles.setNationalite(null);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            demandeurEmploiService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nationalite de informationsPersonnelles"));
    }
    
    @Test
    void controlerDonneeesEntreeInformationsPersonnellesTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
        informationsPersonnelles.setNationalite(NationaliteEnum.AUTRE.getValeur());
        informationsPersonnelles.setTitreSejourEnFranceValide(null);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            demandeurEmploiService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "titreSejourEnFranceValide de informationsPersonnelles"));
    }
    
}
