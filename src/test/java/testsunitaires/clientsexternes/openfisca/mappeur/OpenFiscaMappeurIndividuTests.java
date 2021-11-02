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

import fr.poleemploi.estime.clientsexternes.openfisca.mappeur.OpenFiscaMappeurIndividu;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.Utile;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class OpenFiscaMappeurIndividuTests extends Commun {

    @Autowired
    private OpenFiscaMappeurIndividu openFiscaMappeurIndividu;

    @Autowired
    Utile utileTests;

    private static final int NUMERA_MOIS_SIMULE_PPA = 5;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void mapDemandeurTravailleurIndependantPayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = utileTests.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-travailleur-independant.json");

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getRessourcesFinancieres().setChiffreAffairesIndependantDernierExercice(12000f);
        demandeurEmploi.getRessourcesFinancieres().setAidesCAF(createAidesCAF());

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        JSONObject openFiscaPayload = openFiscaMappeurIndividu.creerDemandeurJSON(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

    @Test
    void mapDemandeurRevenusMicroEntreprisePayloadTest() throws JSONException, JsonParseException, JsonMappingException, IOException, URISyntaxException, ParseException {

        String openFiscaPayloadExpected = utileTests.getStringFromJsonFile("testsunitaires/clientsexternes.openfisca.mappeur/OpenFiscaMappeurIndividuTests/demandeur-micro-entreprise.json");

        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getRessourcesFinancieres().setBeneficesMicroEntrepriseDernierExercice(9000f);

        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-07-2020");
        JSONObject openFiscaPayload = openFiscaMappeurIndividu.creerDemandeurJSON(null, demandeurEmploi, dateDebutPeriodeSimulee, NUMERA_MOIS_SIMULE_PPA);

        assertThat(openFiscaPayload.toString()).isEqualTo(openFiscaPayloadExpected);
    }

}
