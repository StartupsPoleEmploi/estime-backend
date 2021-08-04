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
import fr.poleemploi.estime.services.ressources.PrestationsCAF;
import fr.poleemploi.estime.services.ressources.PrestationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.UtileTests;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class RessourcesFinancieresControleurTests extends CommunTests {
    
    @Autowired
    private IndividuService individuService;
    
    @Autowired
    private UtileTests testUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "ressourcesFinancieres dans DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "prestationsPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(0f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationJournaliereNetASS"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "dateDerniereOuvertureDroitASS dans AllocationsPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurASSTest5() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        allocationASS.setDateDerniereOuvertureDroitASS(testUtile.getDate("14-04-2020"));
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "hasTravailleAuCoursDerniersMois dans RessourcesFinancieres"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest1() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "ressourcesFinancieres dans DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest2() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest3() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        prestationsCAF.setAllocationMensuelleNetAAH(0f);
        ressourcesFinancieres.setPrestationsCAF(prestationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationMensuelleNetAAH"));
    }
    
    @Test
    void controlerDonneeesEntreeRessourcesFinancieresDemandeurAAHTest4() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        demandeurEmploi.setFuturTravail(creerFuturTravail());
        demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
        demandeurEmploi.setSituationFamiliale(new SituationFamiliale());

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        prestationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setPrestationsCAF(prestationsCAF);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(null);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        
        assertThat(Assertions.assertThrows(BadRequestException.class, () -> {
            individuService.simulerAidesSociales(demandeurEmploi);
        }).getMessage()).isEqualTo(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nombreMoisTravaillesDerniersMois dans RessourcesFinancieres"));
    }
}
