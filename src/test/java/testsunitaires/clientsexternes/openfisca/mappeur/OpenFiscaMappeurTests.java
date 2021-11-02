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
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurTests extends Commun {

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;

    @Autowired
    private OpenFiscaMappeur openFiscaMappeur;

    @Autowired
    Utile testUtile;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void mapDemandeurAplToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-apl.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

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
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCAF aidesCAF = createAidesCAF();
        AidesLogement aidesLogement = new AidesLogement();
        AllocationsLogement aidePersonnaliseeLogement = new AllocationsLogement();
        aidePersonnaliseeLogement.setMoisN(385);
        aidePersonnaliseeLogement.setMoisNMoins1(380);
        aidePersonnaliseeLogement.setMoisNMoins2(360);
        aidePersonnaliseeLogement.setMoisNMoins3(357);
        aidesLogement.setAidePersonnaliseeLogement(aidePersonnaliseeLogement);
        aidesCAF.setAidesLogement(aidesLogement);
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAlfToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-alf.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

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
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCAF aidesCAF = createAidesCAF();
        AidesLogement aidesLogement = new AidesLogement();
        AllocationsLogement allocationLogementFamiliale = new AllocationsLogement();
        allocationLogementFamiliale.setMoisN(385);
        allocationLogementFamiliale.setMoisNMoins1(380);
        allocationLogementFamiliale.setMoisNMoins2(360);
        allocationLogementFamiliale.setMoisNMoins3(357);
        aidesLogement.setAllocationLogementFamiliale(allocationLogementFamiliale);
        aidesCAF.setAidesLogement(aidesLogement);
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAlsToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-als.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

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
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCAF aidesCAF = createAidesCAF();
        AidesLogement aidesLogement = new AidesLogement();
        AllocationsLogement allocationLogementSociale = new AllocationsLogement();
        allocationLogementSociale.setMoisN(385);
        allocationLogementSociale.setMoisNMoins1(380);
        allocationLogementSociale.setMoisNMoins2(360);
        allocationLogementSociale.setMoisNMoins3(357);
        aidesLogement.setAllocationLogementSociale(allocationLogementSociale);
        aidesCAF.setAidesLogement(aidesLogement);
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurSansFamilleToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-sans-famille.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
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

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCAF aidesCAF = createAidesCAF();
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusLocatifsToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-revenus-locatifs.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1040);
        salaire.setMontantBrut(1342);
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
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setRevenusImmobilier3DerniersMois(3000f);
        AidesCAF aidesCAF = createAidesCAF();
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusTravailleurIndependantToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-revenus-travailleur-independant.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1040);
        salaire.setMontantBrut(1342);
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
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setChiffreAffairesIndependantDernierExercice(1000f);
        AidesCAF aidesCAF = createAidesCAF();
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusMicroEntrepriseToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-revenus-micro-entreprise.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1040);
        salaire.setMontantBrut(1342);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        Logement logement = new Logement();
        logement.setMontantLoyer(500f);
        logement.setMontantCharges(50f);
        logement.setCodeInsee("44109");
        logement.setDeMayotte(false);
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireNonMeuble(true);
        logement.setStatutOccupationLogement(statutOccupationLogement);
        informationsPersonnelles.setLogement(logement);
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setBeneficesMicroEntrepriseDernierExercice(600f);
        AidesCAF aidesCAF = createAidesCAF();
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPensionInvaliditeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-pension-invalidite.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
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
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCPAM aidesCPAM = new AidesCPAM();
        aidesCPAM.setPensionInvalidite(200f);
        ressourcesFinancieres.setAidesCPAM(aidesCPAM);
        AidesCAF aidesCAF = createAidesCAF();
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-02-2021");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPensionInvaliditeEtASIToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-pension-invalidite-et-asi.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(800);
        salaire.setMontantBrut(1038);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
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
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCPAM aidesCPAM = new AidesCPAM();
        aidesCPAM.setPensionInvalidite(200f);
        aidesCPAM.setAllocationSupplementaireInvalidite(200f);
        ressourcesFinancieres.setAidesCPAM(aidesCPAM);
        AidesCAF aidesCAF = createAidesCAF();
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-02-2021");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRSAToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurTests/demandeur-avec-rsa.json");

        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

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
        situationFamiliale.setIsEnCouple(false);
        situationFamiliale.setIsSeulPlusDe18Mois(true);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireRSA(true);
        demandeurEmploi.setBeneficiaireAides(beneficiaireAides);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesCAF aidesCAF = createAidesCAF();
        aidesCAF.setAllocationRSA(400f);
        aidesCAF.setProchaineDeclarationTrimestrielle(3);
        ressourcesFinancieres.setAidesCAF(aidesCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-07-2020");

        JSONObject openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }
}
