package testsunitaires.clientsexternes.openfisca.mappeur;

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
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.PrestationsCAF;
import fr.poleemploi.estime.services.ressources.PrestationsCPAM;
import fr.poleemploi.estime.services.ressources.PrestationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class OpenFiscaMappeurTestsConjoint {
    
    private static final int NUMERA_MOIS_SIMULE_PPA = 5;
    
    @Autowired
    private OpenFiscaMappeur openFiscaMappeur;
    
    @Autowired
    UtileTests testUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void mapDemandeurAvecConjointAAHToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-aah.json");

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
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        prestationsCAF.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieres.setPrestationsCAF(prestationsCAF);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurAvecConjointASSToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-ass.json");

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
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationMensuelleNet(900f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurAvecConjointAREToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-are.json");

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
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationARE allocationARE = new AllocationARE();
        allocationARE.setAllocationMensuelleNet(900f);
        prestationsPoleEmploi.setAllocationARE(allocationARE);        
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    
    @Test
    void mapDemandeurAvecConjointPensionInvaliditeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
     
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-pension-invalidite.json");

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
        PrestationsCPAM prestationsCPAM = new PrestationsCPAM();
        prestationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setPrestationsCPAM(prestationsCPAM);
        conjoint.setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setConjoint(conjoint);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointSalaireToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsConjoint/demandeur-avec-conjoint-salaire.json");

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
