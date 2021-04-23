package fr.poleemploi.estime.utile.externes.clients.mappeurs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tsohr.JSONException;
import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeur;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsCPAM;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.test.utile.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class OpenFiscaMappeurTests {
    
    private static final int NUMERA_MOIS_SIMULE_PPA = 5;
    
    @Autowired
    private OpenFiscaMappeur openFiscaMappeur;
    
    @Autowired
    TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    
    @Test
    void mapDemandeurAplToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/demandeur-avec-apl.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(110);
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisN(385);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(380);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(360);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(357);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(allocationsLogementMensuellesNetFoyer);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurSansFamilleToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/demandeur-sans-famille.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurRevenusLocatifsToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/demandeur-avec-revenus-locatifs.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1040);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setRevenusImmobilier3DerniersMois(3000f);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurRevenusTravailleurIndependantToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/demandeur-avec-revenus-travailleur-independant.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(1040);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setRevenusCreateurEntreprise3DerniersMois(1000f);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }   
    
    
    @Test
    void mapDemandeurPensionInvaliditeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/demandeur-avec-pension-invalidite.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(800);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setAllocationsCPAM(allocationsCPAM);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-02-2021");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurRSAToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/demandeur-avec-rsa.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelNet(900);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        informationsPersonnelles.setIsProprietaireSansPretOuLogeGratuit(false);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        situationFamiliale.setIsSeulPlusDe18Mois(true);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetRSA(400f);
        allocationsCAF.setProchaineDeclarationRSA(3);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
}
