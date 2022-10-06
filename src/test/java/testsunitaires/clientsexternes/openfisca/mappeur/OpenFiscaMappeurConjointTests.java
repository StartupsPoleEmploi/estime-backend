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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tsohr.JSONException;

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeur;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurConjointTests extends Commun {

    @SpyBean
    protected DateUtile dateUtile;

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
    void mapDemandeurAvecConjointAAHToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/conjoint/demandeur-avec-conjoint-aah.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiEnCoupleSansEnfant(TypePopulationEnum.ASS);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);

	Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(true);
	conjoint.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAFConjoint = createAidesCAF();
	aidesCAFConjoint.setAllocationAAH(900f);
	ressourcesFinancieresConjoint.setAidesCAF(aidesCAFConjoint);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointASSToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/conjoint/demandeur-avec-conjoint-ass.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiEnCoupleSansEnfant(TypePopulationEnum.ASS);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);

	Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireASS(true);
	conjoint.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationMensuelleNet(900f);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	ressourcesFinancieresConjoint.setAidesPoleEmploi(aidesPoleEmploi);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointAREToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/conjoint/demandeur-avec-conjoint-are.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiEnCoupleSansEnfant(TypePopulationEnum.ASS);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);

	Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireARE(true);
	conjoint.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setAllocationMensuelleNet(900f);
	aidesPoleEmploi.setAllocationARE(allocationARE);
	ressourcesFinancieresConjoint.setAidesPoleEmploi(aidesPoleEmploi);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointPensionInvaliditeToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/conjoint/demandeur-avec-conjoint-pension-invalidite.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiEnCoupleSansEnfant(TypePopulationEnum.ASS);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);

	Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	ressourcesFinancieresConjoint.setAidesCPAM(aidesCPAM);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointSalaireToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/conjoint/demandeur-avec-conjoint-salaire.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiEnCoupleSansEnfant(TypePopulationEnum.ASS);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);

	Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();
	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	Salaire salaireConjoint = new Salaire();
	salaireConjoint.setMontantMensuelNet(1200);
	salaireConjoint.setMontantMensuelBrut(1544);
	ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
	ressourcesFinancieresConjoint.setHasTravailleAuCoursDerniersMois(true);
	ressourcesFinancieresConjoint.setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1200, 1544, 13));
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointRevenusLocatifsToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/conjoint/demandeur-avec-conjoint-revenus-locatifs.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiEnCoupleSansEnfant(TypePopulationEnum.ASS);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);

	Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();

	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setRevenusImmobilier3DerniersMois(3000f);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurAvecConjointPensionRetraiteToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/conjoint/demandeur-avec-conjoint-pension-retraite.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiEnCoupleSansEnfant(TypePopulationEnum.ASS);

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);

	Personne conjoint = demandeurEmploi.getSituationFamiliale().getConjoint();

	RessourcesFinancieresAvantSimulation ressourcesFinancieresConjoint = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresConjoint.setPensionRetraite(1000f);
	conjoint.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresConjoint);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }
}
