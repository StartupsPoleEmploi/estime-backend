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

import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.commun.utile.demandeuremploi.TypeContratUtile;
import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class FuturTravailControleurTests extends CommunTests {

    @Autowired
    private IndividuService individuService;
    
    @Autowired
    private TypeContratUtile typeContratUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setFuturTravail(null);
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "futurTravail"));
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        futurTravail.setTypeContrat(null);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "typeContrat de futurTravail")));
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        futurTravail.setTypeContrat("TOTO");
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.TYPE_CONTRAT_INCORRECT.getMessage(), typeContratUtile.getListeFormateeTypesContratPossibles()));
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        futurTravail.setNombreMoisContratCDD(null);
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nombreMoisContratCDD de futurTravail"));
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        futurTravail.setNombreHeuresTravailleesSemaine(0);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(BadRequestMessages.NOMBRE_HEURE_TRAVAILLEES_ZERO.getMessage());
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest6() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(0);
        salaire.setMontantBrut(0);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(BadRequestMessages.SALAIRE_MENSUEL_NET_ZERO.getMessage());
    }
    
    @Test
    void controlerDonneeesEntreeFuturTravailTest7() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setBeneficiaireAides(new BeneficiaireAides());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        
        FuturTravail futurTravail = creerFuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(0);
        salaire.setMontantBrut(0);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(BadRequestMessages.SALAIRE_MENSUEL_NET_ZERO.getMessage());
    }
}
