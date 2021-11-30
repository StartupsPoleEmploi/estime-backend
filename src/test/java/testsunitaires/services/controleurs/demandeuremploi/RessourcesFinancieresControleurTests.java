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
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.Utile;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class RessourcesFinancieresControleurTests extends Commun {
    
    @Autowired
    private IndividuService individuService;
    
    @Autowired
    private Utile testUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "ressourcesFinancieres dans DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "aidesPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(0f);
        aidesPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationJournaliereNetASS"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        aidesPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "dateDerniereOuvertureDroitASS dans AllocationsPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        allocationASS.setDateDerniereOuvertureDroit(testUtile.getDate("14-04-2020"));
        aidesPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "hasTravailleAuCoursDerniersMois dans RessourcesFinancieres"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "ressourcesFinancieres dans DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCAF aidesCAF = new AidesCAF();
        aidesCAF.setAllocationAAH(0f);
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationMensuelleNetAAH"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCAF aidesCAF = new AidesCAF();
        aidesCAF.setAllocationAAH(900f);
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(null);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAides(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "periodeTravailleeAvantSimulation dans RessourcesFinancieres de DemandeurEmploi"));
    }
}
