package testsunitaires.clientsexternes.openfisca.mappeur;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
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

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeur;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurTests extends Commun {

    @Autowired
    private OpenFiscaMappeur openFiscaMappeur;

    @Autowired
    Utile testUtile;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = testUtile.getDate("01-01-2022");
    }

    @Test
    void mapDemandeurAplToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-apl.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2022"));

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieresAvantSimulation();
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
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAlfToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-alf.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2022"));

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieresAvantSimulation();
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
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAlsToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-als.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2022"));

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieresAvantSimulation();
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
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusLocatifsToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-revenus-locatifs.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2022"));

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setRevenusImmobilier3DerniersMois(3000f);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusTravailleurIndependantToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-revenus-travailleur-independant.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2022"));

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setChiffreAffairesIndependantDernierExercice(1000f);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusMicroEntrepriseToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-revenus-micro-entreprise.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2022"));

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setBeneficesMicroEntrepriseDernierExercice(600f);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPensionInvaliditeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-pension-invalidite.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2022"));

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieresAvantSimulation();
	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	ressourcesFinancieres.setAidesCPAM(aidesCPAM);
	AidesCAF aidesCAF = createAidesCAF();
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRSAToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-rsa.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.RSA.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setAllocationRSA(400f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().setProchaineDeclarationTrimestrielle(3);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAREToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-are.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ARE.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereBrute(37f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setSalaireJournalierReferenceBrut(48f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setNombreJoursRestants(65f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().setHasDegressiviteAre(false);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurASSToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-avec-ass.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2022"));

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurCDIToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-cdi.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurCDDToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-cdd.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDD.name());
	demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(4);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurInterimToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-interim.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.INTERIM.name());
	demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(4);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurIAEToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-iae.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireSansEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.IAE.name());
	demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(4);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

}
