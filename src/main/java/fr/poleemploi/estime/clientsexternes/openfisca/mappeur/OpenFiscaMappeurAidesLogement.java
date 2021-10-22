package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLE1;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AIDE_LOGEMENT;

import java.math.RoundingMode;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException; 

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
            return  aideLogementJsonObject.get(periodeFormateeAideLogement).getAsBigDecimal().setScale(0, RoundingMode.HALF_UP).floatValue();            
        } catch (NullPointerException e) {
            LOGGER.error( String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
            throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
        }
    }
    
    public JSONObject creerAideLogementJSON(LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject periode = new JSONObject();
        periode.put(openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAide(dateDebutSimulation, numeroMoisSimule), JSONObject.NULL);
        return periode;
    }
}
