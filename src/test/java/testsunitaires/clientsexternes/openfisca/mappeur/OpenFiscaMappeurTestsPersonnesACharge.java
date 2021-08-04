package testsunitaires.clientsexternes.openfisca.mappeur;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import fr.poleemploi.estime.services.ressources.PrestationsFamiliales;
import fr.poleemploi.estime.services.ressources.PrestationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class OpenFiscaMappeurTestsPersonnesACharge extends CommunTests {
    
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
    void mapDemandeurPersonneAChargeAAHToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsPersonnesACharge/demandeur-avec-enfant-aah.json");

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
        List<Personne> personnesACharge = new ArrayList<Personne>();
        Personne personneACharge = createPersonne(testUtile.getDate("05-07-2000"));
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        personneACharge.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieresPersonneACharge = new RessourcesFinancieres();
        PrestationsCAF prestationsCAFPersonneACharge = new PrestationsCAF();
        prestationsCAFPersonneACharge.setAllocationMensuelleNetAAH(900f);
        ressourcesFinancieresPersonneACharge.setPrestationsCAF(prestationsCAFPersonneACharge);
        personneACharge.setRessourcesFinancieres(ressourcesFinancieresPersonneACharge);
        personnesACharge.add(personneACharge);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurPersonneAChargeAREToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsPersonnesACharge/demandeur-avec-enfant-are.json");

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
        List<Personne> personnesACharge = new ArrayList<Personne>();
        Personne personneACharge = createPersonne(testUtile.getDate("05-07-2000"));
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        personneACharge.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieresPersonneACharge = new RessourcesFinancieres();
        PrestationsPoleEmploi prestationsPoleEmploiPersonneACharge = new PrestationsPoleEmploi();
        AllocationARE allocationARE = new AllocationARE();
        allocationARE.setAllocationMensuelleNet(900f);
        prestationsPoleEmploiPersonneACharge.setAllocationARE(allocationARE);
        ressourcesFinancieresPersonneACharge.setPrestationsPoleEmploi(prestationsPoleEmploiPersonneACharge);
        personneACharge.setRessourcesFinancieres(ressourcesFinancieresPersonneACharge);
        personnesACharge.add(personneACharge);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurPersonneAChargeASSToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsPersonnesACharge/demandeur-avec-enfant-ass.json");

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
        List<Personne> personnesACharge = new ArrayList<Personne>();
        Personne personneACharge = createPersonne(testUtile.getDate("05-07-2000"));
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        personneACharge.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieresPersonneACharge = new RessourcesFinancieres();
        PrestationsPoleEmploi prestationsPoleEmploiPersonneACharge = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationMensuelleNet(900f);
        prestationsPoleEmploiPersonneACharge.setAllocationASS(allocationASS);
        ressourcesFinancieresPersonneACharge.setPrestationsPoleEmploi(prestationsPoleEmploiPersonneACharge);
        personneACharge.setRessourcesFinancieres(ressourcesFinancieresPersonneACharge);
        personnesACharge.add(personneACharge);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurPersonneAChargePensionInvaliditeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        //Si DE France Métropolitaine, en couple, conjoint 200 pension invalidite, 
        //1 enfant de 1an
        //futur contrat CDI avec salaire net 800€/mois
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsPersonnesACharge/demandeur-avec-enfant-pension-invalidite.json");

        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1165);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        createPersonne(personnesACharge, testUtile.getDate("09-09-2000"));
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        PrestationsCPAM prestationsCPAM = new PrestationsCPAM();
        prestationsCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setPrestationsCPAM(prestationsCPAM);
        personnesACharge.get(0).setRessourcesFinancieres(ressourcesFinancieres);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
  
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-02-2021");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurPersonneAChargeRSAToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsPersonnesACharge/demandeur-avec-enfant-rsa.json");

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
        List<Personne> personnesACharge = new ArrayList<Personne>();
        Personne personneACharge = createPersonne(testUtile.getDate("05-07-2000"));
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        personneACharge.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieresPersonneACharge = new RessourcesFinancieres();
        PrestationsCAF prestationsCAFPersonneACharge = new PrestationsCAF();
        prestationsCAFPersonneACharge.setAllocationMensuelleNetRSA(900f);
        ressourcesFinancieresPersonneACharge.setPrestationsCAF(prestationsCAFPersonneACharge);
        personneACharge.setRessourcesFinancieres(ressourcesFinancieresPersonneACharge);
        personnesACharge.add(personneACharge);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurPersonneAChargeSalaireToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsPersonnesACharge/demandeur-avec-enfant-salaire.json");

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
        List<Personne> personnesACharge = new ArrayList<Personne>();
        Personne personneACharge = createPersonne(testUtile.getDate("05-07-2000"));
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        personneACharge.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        RessourcesFinancieres ressourcesFinancieresPersonneACharge = new RessourcesFinancieres();
        Salaire salairepersonneACharge = new Salaire();
        salairepersonneACharge.setMontantNet(900);
        salairepersonneACharge.setMontantBrut(1165);
        ressourcesFinancieresPersonneACharge.setSalaire(salaire);
        personneACharge.setRessourcesFinancieres(ressourcesFinancieresPersonneACharge);
        personnesACharge.add(personneACharge);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
    @Test
    void mapDemandeurPersonnesAChargeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
        
        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTestsPersonnesACharge/demandeur-avec-enfants-et-af.json");

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
        List<Personne> personnesACharge = new ArrayList<Personne>();
        createPersonne(personnesACharge, testUtile.getDate("05-07-2016")); 
        createPersonne(personnesACharge, testUtile.getDate("05-07-2014"));   
        createPersonne(personnesACharge, testUtile.getDate("05-07-2012")); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(899);
        prestationsFamiliales.setAllocationSoutienFamilial(799);
        prestationsFamiliales.setComplementFamilial(699);
        prestationsFamiliales.setPensionsAlimentairesFoyer(150);
        prestationsCAF.setPrestationsFamiliales(prestationsFamiliales);
        ressourcesFinancieres.setPrestationsCAF(prestationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");
        
        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);
        
        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
    
}
