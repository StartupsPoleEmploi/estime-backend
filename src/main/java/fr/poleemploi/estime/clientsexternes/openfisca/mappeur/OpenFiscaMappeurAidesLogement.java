package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLE1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaFamille;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaPeriodes;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;

@Component
public class OpenFiscaMappeurAidesLogement {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaMappeurAidesLogement.class);

    public float getMontantAideLogement(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaFamille> openFiscaFamilles = openFiscaRoot.getFamilles();
	    OpenFiscaFamille openFiscaFamille = openFiscaFamilles.get(FAMILLE1);
	    OpenFiscaPeriodes openFiscaAideLogement = openFiscaFamille.getAideLogement();
	    String periodeFormateeAideLogement = openFiscaPeriodeMappeur.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
	    Double montantAideLogement = (Double) openFiscaAideLogement.get(periodeFormateeAideLogement);

	    return BigDecimal.valueOf(montantAideLogement).setScale(0, RoundingMode.HALF_UP).floatValue();
	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public String getTypeAideLogement(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    String typeAideLogement = "";

	    Map<String, OpenFiscaFamille> openFiscaFamilles = openFiscaRoot.getFamilles();
	    OpenFiscaFamille openFiscaFamille = openFiscaFamilles.get(FAMILLE1);
	    OpenFiscaPeriodes openFiscaAidePersonnaliseeLogement = openFiscaFamille.getAidePersonnaliseeLogement();
	    OpenFiscaPeriodes openFiscaAllocationLogementFamiliale = openFiscaFamille.getAllocationLogementFamiliale();
	    OpenFiscaPeriodes openFiscaAllocationLogementSociale = openFiscaFamille.getAllocationLogementSociale();
	    String periodeFormateeAideLogement = openFiscaPeriodeMappeur.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
	    Double montantAidePersonnaliseeLogement = (Double) openFiscaAidePersonnaliseeLogement.get(periodeFormateeAideLogement);
	    Double montantAllocationLogementFamiliale = (Double) openFiscaAllocationLogementFamiliale.get(periodeFormateeAideLogement);
	    Double montantAllocationLogementSociale = (Double) openFiscaAllocationLogementSociale.get(periodeFormateeAideLogement);

	    if (BigDecimal.valueOf(montantAidePersonnaliseeLogement).setScale(0, RoundingMode.HALF_UP).floatValue() > 0) {
		typeAideLogement = AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode();
	    } else if (BigDecimal.valueOf(montantAllocationLogementFamiliale).setScale(0, RoundingMode.HALF_UP).floatValue() > 0) {
		typeAideLogement = AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode();
	    } else if (BigDecimal.valueOf(montantAllocationLogementSociale).setScale(0, RoundingMode.HALF_UP).floatValue() > 0) {
		typeAideLogement = AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode();
	    }
	    return typeAideLogement;
	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }
}
