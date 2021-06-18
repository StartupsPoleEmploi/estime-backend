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
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.test.utile.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class OpenFiscaMappeurConjointTests {
    
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
    void mapDemandeurAvecConjointAAHToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/conjoint/demandeur-avec-conjoint-aah.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        conjoint.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurAvecConjointASSToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/conjoint/demandeur-avec-conjoint-ass.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        conjoint.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationMensuelleNet(900f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurAvecConjointAREToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/conjoint/demandeur-avec-conjoint-are.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        conjoint.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationMensuelleNet(900f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    
    @Test
    void mapDemandeurAvecConjointPensionInvaliditeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
     
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/conjoint/demandeur-avec-conjoint-pension-invalidite.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setAllocationsCPAM(allocationsCPAM);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurAvecConjointRSAToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
       
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/conjoint/demandeur-avec-conjoint-rsa.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        conjoint.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetRSA(900f);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurAvecConjointSalaireToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("openfisca-mappeur/conjoint/demandeur-avec-conjoint-salaire.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        futurTravail.setSalaire(salaire);
        ressourcesFinancieres.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
}
