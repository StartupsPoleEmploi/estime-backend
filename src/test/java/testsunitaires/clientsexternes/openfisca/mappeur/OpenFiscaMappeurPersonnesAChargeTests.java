package testsunitaires.clientsexternes.openfisca.mappeur;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

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
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
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
class OpenFiscaMappeurPersonnesAChargeTests extends Commun {

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
    void mapDemandeurPersonneAChargeAAHToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/personnesACharge/demandeur-avec-enfant-aah.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireAvecEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);


	List<Personne> personnesACharge = demandeurEmploi.getSituationFamiliale().getPersonnesACharge();
	Personne personneACharge = personnesACharge.get(0);
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(true);
	personneACharge.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresPersonneACharge = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAFPersonneACharge = new AidesCAF();
	aidesCAFPersonneACharge.setAllocationAAH(900f);
	ressourcesFinancieresPersonneACharge.setAidesCAF(aidesCAFPersonneACharge);
	personneACharge.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresPersonneACharge);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPersonneAChargeAREToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/personnesACharge/demandeur-avec-enfant-are.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireAvecEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);


	List<Personne> personnesACharge = demandeurEmploi.getSituationFamiliale().getPersonnesACharge();
	Personne personneACharge = personnesACharge.get(0);
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireARE(true);
	personneACharge.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresPersonneACharge = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploiPersonneACharge = new AidesPoleEmploi();
	AllocationARE allocationARE = new AllocationARE();
	allocationARE.setAllocationMensuelleNet(900f);
	aidesPoleEmploiPersonneACharge.setAllocationARE(allocationARE);
	ressourcesFinancieresPersonneACharge.setAidesPoleEmploi(aidesPoleEmploiPersonneACharge);
	personneACharge.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresPersonneACharge);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPersonneAChargeASSToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/personnesACharge/demandeur-avec-enfant-ass.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireAvecEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);


	List<Personne> personnesACharge = demandeurEmploi.getSituationFamiliale().getPersonnesACharge();
	Personne personneACharge = personnesACharge.get(0);
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireASS(true);
	personneACharge.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresPersonneACharge = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploiPersonneACharge = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationMensuelleNet(900f);
	aidesPoleEmploiPersonneACharge.setAllocationASS(allocationASS);
	ressourcesFinancieresPersonneACharge.setAidesPoleEmploi(aidesPoleEmploiPersonneACharge);
	personneACharge.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresPersonneACharge);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPersonneAChargePensionInvaliditeToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {
	// Si DE France Métropolitaine, en couple, conjoint 200 pension invalidite,
	// 1 enfant de 1an
	// futur contrat CDI avec salaire net 800€/mois
	String openFiscaPayloadExpected = testUtile.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/personnesACharge/demandeur-avec-enfant-pension-invalidite.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireAvecEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);


	List<Personne> personnesACharge = demandeurEmploi.getSituationFamiliale().getPersonnesACharge();
	Personne personneACharge = personnesACharge.get(0);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresPersonneACharge = new RessourcesFinancieresAvantSimulation();
	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setPensionInvalidite(200f);
	ressourcesFinancieresPersonneACharge.setAidesCPAM(aidesCPAM);
	personneACharge.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresPersonneACharge);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPersonneAChargeSalaireToOpenFiscaPayloadTest()
	    throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/personnesACharge/demandeur-avec-enfant-salaire.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireAvecEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);


	List<Personne> personnesACharge = demandeurEmploi.getSituationFamiliale().getPersonnesACharge();
	Personne personneACharge = personnesACharge.get(0);
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	personneACharge.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresPersonneACharge = new RessourcesFinancieresAvantSimulation();
	Salaire salairepersonneACharge = new Salaire();
	salairepersonneACharge.setMontantNet(900);
	salairepersonneACharge.setMontantBrut(1165);
	ressourcesFinancieresPersonneACharge.setSalaire(salairepersonneACharge);
	ressourcesFinancieresPersonneACharge.setHasTravailleAuCoursDerniersMois(true);
	ressourcesFinancieresPersonneACharge.setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(900, 1165, 13));
	personneACharge.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresPersonneACharge);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurPersonnesAChargeToOpenFiscaPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadExpected = testUtile
		.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/personnesACharge/demandeur-avec-enfants-et-af.json");

	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCelibataireAvecEnfant(TypePopulationEnum.ASS.getLibelle());

	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);


	List<Personne> personnesACharge = demandeurEmploi.getSituationFamiliale().getPersonnesACharge();
	createPersonne(personnesACharge, testUtile.getDate("05-07-2016"));
	createPersonne(personnesACharge, testUtile.getDate("05-07-2014"));
	createPersonne(personnesACharge, testUtile.getDate("05-07-2012"));
	demandeurEmploi.getSituationFamiliale().setPersonnesACharge(personnesACharge);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesCAF aidesCAF = createAidesCAF();
	AidesFamiliales aidesFamiliales = new AidesFamiliales();
	aidesFamiliales.setAllocationsFamiliales(899);
	aidesFamiliales.setAllocationSoutienFamilial(799);
	aidesFamiliales.setComplementFamilial(699);
	aidesFamiliales.setPensionsAlimentairesFoyer(150);
	aidesCAF.setAidesFamiliales(aidesFamiliales);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	OpenFiscaRoot openFiscaPayload = openFiscaMappeur.mapDemandeurEmploiToOpenFiscaPayload(demandeurEmploi, dateDebutSimulation);

	assertThat(openFiscaPayload.toString()).hasToString(openFiscaPayloadExpected);
    }

}
