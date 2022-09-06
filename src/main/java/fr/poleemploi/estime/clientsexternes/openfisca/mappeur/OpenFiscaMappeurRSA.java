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
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class OpenFiscaMappeurRSA {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaMappeurRSA.class);

    public float getMontantRSA(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaFamille> openFiscaFamilles = openFiscaRoot.getFamilles();
	    OpenFiscaFamille openFiscaFamille = openFiscaFamilles.get(FAMILLE1);
	    OpenFiscaPeriodes openFiscaRevenuSolidariteActive = openFiscaFamille.getRevenuSolidariteActive();
	    String periodeFormateeRSA = openFiscaMappeurPeriode.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
	    Double montantRSA = (Double) openFiscaRevenuSolidariteActive.get(periodeFormateeRSA);

	    return BigDecimal.valueOf(montantRSA).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public OpenFiscaFamille addRSAOpenFiscaIndividu(OpenFiscaFamille openFiscaFamille, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	openFiscaFamille.setRevenuSolidariteActive(openFiscaMappeurPeriode.creerPeriodesOpenFiscaRSA(demandeurEmploi, dateDebutSimulation));
	return openFiscaFamille;
    }
}
