package testsintegration.simulation.montants.openfisca.agepi;

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
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Simulation;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class EligibiliteAgepiTests extends Commun {

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void calculerEligibleTest1() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	// Si DE France Métropolitaine, célibataire, 1 enfant à charge moins de 10 ans
	boolean isEnCouple = false;
	List<Personne> personnesACharge = new ArrayList<>();
	personnesACharge.add(createEnfant(8));
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	// Lorsque je calcul le montant de l'AGEPI
	initMocks();
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAgepi(new Simulation(), demandeurEmploi, dateDebutPeriodeSimulee, 1);

	// Alors le demandeur est éligible à l'AGEPI
	assertThat(openFiscaRetourSimulation.getMontantAgepi()).isNotZero();
    }

    @Test
    void calculerEligibleTest2() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	// Si DE France Métropolitaine, célibataire, 2 enfants à charge moins de 10 ans
	boolean isEnCouple = false;
	List<Personne> personnesACharge = new ArrayList<>();
	personnesACharge.add(createEnfant(8));
	personnesACharge.add(createEnfant(5));
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	// Lorsque je calcul le montant de l'AGEPI
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAgepi(new Simulation(), demandeurEmploi, dateDebutPeriodeSimulee, 1);

	// Alors le demandeur est éligible à l'AGEPI
	assertThat(openFiscaRetourSimulation.getMontantAgepi()).isNotZero();
    }

    @Test
    void calculerEligibleTest3() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	// Si DE France Métropolitaine, célibataire, 1 enfant à charge moins de 10 ans, 1 enfant à charge plus de 10 ans
	boolean isEnCouple = false;
	List<Personne> personnesACharge = new ArrayList<>();
	personnesACharge.add(createEnfant(12));
	personnesACharge.add(createEnfant(5));
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	// Lorsque je calcul le montant de l'AGEPI
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAgepi(new Simulation(), demandeurEmploi, dateDebutPeriodeSimulee, 1);

	// Alors le demandeur est éligible à l'AGEPI
	assertThat(openFiscaRetourSimulation.getMontantAgepi()).isNotZero();
    }

    @Test
    void calculerNonEligibleTest4() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	// Si DE France Métropolitaine, célibataire, 0 enfant à charge moins de 10 ans
	boolean isEnCouple = false;
	List<Personne> personnesACharge = new ArrayList<>();
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	// Lorsque je calcul le montant de l'AGEPI
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAgepi(new Simulation(), demandeurEmploi, dateDebutPeriodeSimulee, 1);

	// Alors le demandeur est éligible à l'AGEPI
	assertThat(openFiscaRetourSimulation.getMontantAgepi()).isZero();
    }

    @Test
    void calculerNonEligibleTest5() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	// Si DE France Métropolitaine, en couple, 0 enfant à charge moins de 10 ans
	boolean isEnCouple = true;
	List<Personne> personnesACharge = new ArrayList<>();
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	// Lorsque je calcul le montant de l'AGEPI
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAgepi(new Simulation(), demandeurEmploi, dateDebutPeriodeSimulee, 1);

	// Alors le demandeur est éligible à l'AGEPI
	assertThat(openFiscaRetourSimulation.getMontantAgepi()).isZero();
    }

    @Test
    void calculerNonEligibleTest6() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	// Si DE France Métropolitaine, en couple, 1 enfant à charge moins de 10 ans
	boolean isEnCouple = true;
	List<Personne> personnesACharge = new ArrayList<>();
	personnesACharge.add(createEnfant(8));
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	// Lorsque je calcul le montant de l'AGEPI
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAgepi(new Simulation(), demandeurEmploi, dateDebutPeriodeSimulee, 1);

	// Alors le demandeur est éligible à l'AGEPI
	assertThat(openFiscaRetourSimulation.getMontantAgepi()).isZero();
    }

    @Test
    void calculerNonEligibleTest7() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	initMocks();
	// Si DE France Métropolitaine, en couple, 1 enfant à charge plus de 10 ans
	boolean isEnCouple = false;
	List<Personne> personnesACharge = new ArrayList<>();
	personnesACharge.add(createEnfant(12));
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(isEnCouple, personnesACharge);
	// Lorsque je calcul le montant de l'AGEPI
	LocalDate dateDebutPeriodeSimulee = dateUtile.getDateJour();
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAgepi(new Simulation(), demandeurEmploi, dateDebutPeriodeSimulee, 1);

	// Alors le demandeur est éligible à l'AGEPI
	assertThat(openFiscaRetourSimulation.getMontantAgepi()).isZero();
    }

}
