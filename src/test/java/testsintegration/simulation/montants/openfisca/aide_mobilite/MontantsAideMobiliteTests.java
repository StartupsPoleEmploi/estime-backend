package testsintegration.simulation.montants.openfisca.aide_mobilite;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class MontantsAideMobiliteTests extends Commun {

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void calculerEligibleTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, ASS, célibataire, 65km trajet travail-domicile
	boolean isEnCouple = false;
	List<Personne> personnesACharge = new ArrayList<>();
	personnesACharge.add(createEnfant(8));
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(65);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(10);
	// Lorsque je calcul le montant de l'Aide Mobilité
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutPeriodeSimulee, false);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAideMobilite(openFiscaRoot, dateDebutPeriodeSimulee);

	// Alors le demandeur est éligible à l'Aide Mobilité
	assertThat(openFiscaRetourSimulation.getMontantAideMobilite()).isEqualTo(0.20f * 65.0f * 10.0f);
    }

    @Test
    void calculerEligibleTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	// Si DE DOM, ASS, célibataire, 30km trajet travail-domicile
	boolean isEnCouple = false;
	List<Personne> personnesACharge = new ArrayList<>();
	personnesACharge.add(createEnfant(8));
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().setCodePostal("97600");
	demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().setCodeInsee("97611");
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(30);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(10);
	// Lorsque je calcul le montant de l'Aide Mobilité
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutPeriodeSimulee, false);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAideMobilite(openFiscaRoot, dateDebutPeriodeSimulee);

	// Alors le demandeur est éligible à l'Aide Mobilité
	assertThat(openFiscaRetourSimulation.getMontantAideMobilite()).isEqualTo(0.20f * 30.0f * 10.0f);
    }

    @Test
    void calculerEligibleTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	// Si DE France Métropolitaine, ASS, célibataire, 65km trajet travail-domicile, CDD 3 mois
	boolean isEnCouple = false;
	List<Personne> personnesACharge = new ArrayList<>();
	personnesACharge.add(createEnfant(8));
	DemandeurEmploi demandeurEmploi = createDemandeurEmploiCDD(3, isEnCouple, personnesACharge);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(65);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(10);
	// Lorsque je calcul le montant de l'Aide Mobilité
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutPeriodeSimulee, false);
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAideMobilite(openFiscaRoot, dateDebutPeriodeSimulee);

	// Alors le demandeur est éligible à l'Aide Mobilité
	assertThat(openFiscaRetourSimulation.getMontantAideMobilite()).isEqualTo(0.20f * 65.0f * 10.0f);
    }
}