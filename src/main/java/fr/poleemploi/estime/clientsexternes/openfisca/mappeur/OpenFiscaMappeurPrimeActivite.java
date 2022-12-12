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
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class OpenFiscaMappeurPrimeActivite {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaMappeurPrimeActivite.class);

    public float getMontantPrimeActivite(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaFamille> openFiscaFamilles = openFiscaRoot.getFamilles();
	    OpenFiscaFamille openFiscaFamille = openFiscaFamilles.get(FAMILLE1);
	    OpenFiscaPeriodes openFiscaPrimeActivite = openFiscaFamille.getPrimeActivite();
	    String periodeFormateePrimeActivite = getPeriodeARecuperer(dateDebutSimulation, numeroMoisSimule);
	    Double montantPrimeActivite = (Double) openFiscaPrimeActivite.get(periodeFormateePrimeActivite);

	    return BigDecimal.valueOf(montantPrimeActivite).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public void addPrimeActiviteOpenFisca(OpenFiscaFamille openFiscaFamille, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	if (ressourcesFinancieresUtile.hasPrimeActivite(demandeurEmploi)) {
	    openFiscaFamille.setPrimeActivite(openFiscaMappeurPeriode.creerPeriodesPrimeActiviteOpenFisca(demandeurEmploi, dateDebutSimulation));
	} else {
	    openFiscaFamille.setPrimeActivite(openFiscaMappeurPeriode.creerPeriodesCalculeesOpenFisca(dateDebutSimulation));
	}
    }

    public String getPeriodeARecuperer(LocalDate dateDebutSimulation, int numeroMoisSimule) {
	return openFiscaMappeurPeriode.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
    }
}
