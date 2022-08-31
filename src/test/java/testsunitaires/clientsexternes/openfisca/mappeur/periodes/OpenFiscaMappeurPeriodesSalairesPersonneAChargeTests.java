package testsunitaires.clientsexternes.openfisca.mappeur.periodes;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
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

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurPeriode;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurPeriodesSalairesPersonneAChargeTests extends Commun {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    @Autowired
    Utile testUtile;

    private LocalDate dateDebutSimulation;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    /*****************
     * TESTS SUR creerPeriodesSalaireDemandeur
     ********************************************************/

    /**
     * CONTEXTE DES TESTS 
     * 
     * date demande simulation : 01-06-2020
     * 
     * avant simulation
     * M-2 05/2020       
     * M-1 06/2020 
     * 
     * période de la simulation sur 6 mois
     * M1 07/2020       
     * M2 08/2020   
     * M3 09/2020    
     * M4 10/2020
     * M5 11/2020
     * M6 12/2020
     *      
     *  
     ********************************************************************************/

    @BeforeEach
    void initBeforeTest() throws ParseException {
	dateDebutSimulation = utileTests.getDate("01-01-2022");
    }

    /**
     * Demandeur ASS sans cumul ASS + salaire dans les 3 mois avant la simulation
     * Création de la période salaire pour une simulation au mois M5
     * 
     */
    @Test
    void mapPeriodeSalaireTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

	String openFiscaPayloadSalaireImposableExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesSalairesTests/personne-a-charge/salaire-imposable-8-mois-salaire-avant-simulation.json");
	String openFiscaPayloadSalaireBaseExpected = utileTests.getStringFromJsonFile(
		"testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurPeriodesSalairesTests/personne-a-charge/salaire-base-8-mois-salaire-avant-simulation.json");

	DemandeurEmploi demandeurEmploi = creerDemandeurEmploiSalairesTests();

	OpenFiscaIndividu personneOpenFisca = new OpenFiscaIndividu();
	openFiscaMappeurPeriode.creerPeriodesSalairePersonne(personneOpenFisca, demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0), dateDebutSimulation);

	assertThat(personneOpenFisca.getSalaireDeBase().toString()).hasToString(openFiscaPayloadSalaireBaseExpected);
	assertThat(personneOpenFisca.getSalaireImposable().toString()).hasToString(openFiscaPayloadSalaireImposableExpected);
    }

    private DemandeurEmploi creerDemandeurEmploiSalairesTests() throws ParseException {

	boolean isEnCouple = false;
	int nbEnfant = 0;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1291);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(1000);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(false);

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	List<Personne> personnesACharge = new ArrayList<Personne>();
	Personne personneACharge = createPersonne(testUtile.getDate("05-07-2000"));
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	personneACharge.setBeneficiaireAides(beneficiaireAides);
	RessourcesFinancieresAvantSimulation ressourcesFinancieresPersonneACharge = new RessourcesFinancieresAvantSimulation();
	Salaire salairepersonneACharge = new Salaire();
	salairepersonneACharge.setMontantMensuelNet(1200);
	salairepersonneACharge.setMontantMensuelBrut(1544);
	ressourcesFinancieresPersonneACharge.setSalaire(salairepersonneACharge);
	ressourcesFinancieresPersonneACharge.setHasTravailleAuCoursDerniersMois(true);
	ressourcesFinancieresPersonneACharge.setHasTravailleAuCoursDerniersMois(true);
	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = utileTests.creerSalaires(0, 0, 12);
	Salaire salaireMoisMoins1 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins3 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins4 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins5 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins7 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins9 = utileTests.creerSalaire(850, 1101);
	Salaire salaireMoisMoins11 = utileTests.creerSalaire(850, 1101);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins1, 1);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins3, 3);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins4, 4);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins5, 5);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins7, 7);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins9, 9);
	salaires = utileTests.ajouterSalaire(salaires, salaireMoisMoins11, 11);
	periodeTravailleeAvantSimulation.setMois(utileTests.createMoisTravaillesAvantSimulation(salaires));
	ressourcesFinancieresPersonneACharge.setPeriodeTravailleeAvantSimulation(periodeTravailleeAvantSimulation);
	personneACharge.setRessourcesFinancieresAvantSimulation(ressourcesFinancieresPersonneACharge);
	personnesACharge.add(personneACharge);
	situationFamiliale.setPersonnesACharge(personnesACharge);
	situationFamiliale.setIsEnCouple(false);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	return demandeurEmploi;
    }
}
