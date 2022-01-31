package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AIDE_LOGEMENT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ALF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ALS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.APL;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLE1;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLES;

import java.math.RoundingMode;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;

@Component
public class OpenFiscaMappeurAidesLogement {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaMappeurAidesLogement.class);

    public float getMontantAideLogement(String jsonResponse, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
	    JsonObject famillesJsonObject = jsonObject.get(FAMILLES).getAsJsonObject();
	    JsonObject famille1JsonObject = famillesJsonObject.get(FAMILLE1).getAsJsonObject();
	    JsonObject aideLogementJsonObject = famille1JsonObject.get(AIDE_LOGEMENT).getAsJsonObject();
	    String periodeFormateeAideLogement = openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAide(dateDebutSimulation, numeroMoisSimule);
	    return aideLogementJsonObject.get(periodeFormateeAideLogement).getAsBigDecimal().setScale(0, RoundingMode.HALF_UP).floatValue();
	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public String getTypeAideLogement(String jsonResponse, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    String typeAideLogement = "";
	    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
	    JsonObject famillesJsonObject = jsonObject.get(FAMILLES).getAsJsonObject();
	    JsonObject famille1JsonObject = famillesJsonObject.get(FAMILLE1).getAsJsonObject();
	    JsonObject aplJsonObject = famille1JsonObject.get(APL).getAsJsonObject();
	    JsonObject alfJsonObject = famille1JsonObject.get(ALF).getAsJsonObject();
	    JsonObject alsJsonObject = famille1JsonObject.get(ALS).getAsJsonObject();
	    String periodeFormateeAideLogement = openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAide(dateDebutSimulation, numeroMoisSimule);
	    if (aplJsonObject.get(periodeFormateeAideLogement).getAsBigDecimal().setScale(0, RoundingMode.HALF_UP).floatValue() > 0) {
		typeAideLogement = AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode();
	    }
	    if (alfJsonObject.get(periodeFormateeAideLogement).getAsBigDecimal().setScale(0, RoundingMode.HALF_UP).floatValue() > 0) {
		typeAideLogement = AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode();
	    }
	    if (alsJsonObject.get(periodeFormateeAideLogement).getAsBigDecimal().setScale(0, RoundingMode.HALF_UP).floatValue() > 0) {
		typeAideLogement = AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode();
	    }
	    return typeAideLogement;
	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public JSONObject creerAllocationLogementJSON(AllocationsLogement allocationsLogement, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	return openFiscaPeriodeMappeur.creerPeriodesAllocationsLogement(allocationsLogement, dateDebutSimulation, numeroMoisSimule);
    }

    public JSONObject creerAideLogementJSON(LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject periode = new JSONObject();
	periode.put(openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAide(dateDebutSimulation, numeroMoisSimule), JSONObject.NULL);
	return periode;
    }
}
