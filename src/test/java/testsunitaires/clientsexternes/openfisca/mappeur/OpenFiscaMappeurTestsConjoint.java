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
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
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
        Logement logement = new Logement();
        logement.setMontantLoyer(500f);
        logement.setMontantCharges(50f);
        logement.setCodeInsee("44109");
        logement.setDeMayotte(false);
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireNonMeuble(true);
        logement.setStatutOccupationLogement(statutOccupationLogement);
        informationsPersonnelles.setLogement(logement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(true);
        conjoint.setBeneficiaireAides(beneficiaireAides);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCAF aidesCAF = new AidesCAF();
        aidesCAF.setAllocationAAH(900f);
        ressourcesFinancieres.setAidesCAF(aidesCAF);
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
        Logement logement = new Logement();
        logement.setMontantLoyer(500f);
        logement.setMontantCharges(50f);
        logement.setCodeInsee("44109");
        logement.setDeMayotte(false);
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireNonMeuble(true);
        logement.setStatutOccupationLogement(statutOccupationLogement);
        informationsPersonnelles.setLogement(logement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireASS(true);
        conjoint.setBeneficiaireAides(beneficiaireAides);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationMensuelleNet(900f);
        aidesPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
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
        Logement logement = new Logement();
        logement.setMontantLoyer(500f);
        logement.setMontantCharges(50f);
        logement.setCodeInsee("44109");
        logement.setDeMayotte(false);
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireNonMeuble(true);
        logement.setStatutOccupationLogement(statutOccupationLogement);
        informationsPersonnelles.setLogement(logement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireARE(true);
        conjoint.setBeneficiaireAides(beneficiaireAides);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationARE allocationARE = new AllocationARE();
        allocationARE.setAllocationMensuelleNet(900f);
        aidesPoleEmploi.setAllocationARE(allocationARE);        
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
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
        Logement logement = new Logement();
        logement.setMontantLoyer(500f);
        logement.setMontantCharges(50f);
        logement.setCodeInsee("44109");
        logement.setDeMayotte(false);
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireNonMeuble(true);
        logement.setStatutOccupationLogement(statutOccupationLogement);
        informationsPersonnelles.setLogement(logement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCPAM aidesCPAM = new AidesCPAM();
        aidesCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setAidesCPAM(aidesCPAM);
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
        Logement logement = new Logement();
        logement.setMontantLoyer(500f);
        logement.setMontantCharges(50f);
        logement.setCodeInsee("44109");
        logement.setDeMayotte(false);
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireNonMeuble(true);
        logement.setStatutOccupationLogement(statutOccupationLogement);
        informationsPersonnelles.setLogement(logement);
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
