package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaPeriodes;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class OpenFiscaMappeurComplementARE {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    @Autowired
    private AreUtile areUtile;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaMappeurComplementARE.class);

    public float getMontantComplementARE(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaIndividu> openFiscaIndividus = openFiscaRoot.getIndividus();
	    OpenFiscaIndividu openFiscaIndividu = openFiscaIndividus.get(DEMANDEUR);
	    OpenFiscaPeriodes openFiscaComplementARE = openFiscaIndividu.getAllocationMensuelleComplementARE();
	    String periodeFormateeComplementARE = openFiscaPeriodeMappeur.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
	    Double montantComplementARE = (Double) openFiscaComplementARE.get(periodeFormateeComplementARE);

	    return BigDecimal.valueOf(montantComplementARE).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public float getMontantDeductionsComplementARE(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaIndividu> openFiscaIndividus = openFiscaRoot.getIndividus();
	    OpenFiscaIndividu openFiscaIndividu = openFiscaIndividus.get(DEMANDEUR);
	    OpenFiscaPeriodes openFiscaDeductionsComplementARE = openFiscaIndividu.getDeductionsMensuellesComplementARE();
	    String periodeFormateeComplementARE = openFiscaPeriodeMappeur.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
	    Double deductionsMensuelles = (Double) openFiscaDeductionsComplementARE.get(periodeFormateeComplementARE);

	    return BigDecimal.valueOf(deductionsMensuelles).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public float getNombreJoursIndemnisesComplementARE(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaIndividu> openFiscaIndividus = openFiscaRoot.getIndividus();
	    OpenFiscaIndividu openFiscaIndividu = openFiscaIndividus.get(DEMANDEUR);
	    OpenFiscaPeriodes openFiscaNombreJoursIndemnisesComplementARE = openFiscaIndividu.getNombreJoursIndemnisesComplementARE();
	    String periodeFormateeComplementARE = openFiscaPeriodeMappeur.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
	    Double nombresJoursIndemnises = (Double) openFiscaNombreJoursIndemnisesComplementARE.get(periodeFormateeComplementARE);

	    return BigDecimal.valueOf(nombresJoursIndemnises).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public float getNombreJoursRestantsARE(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaIndividu> openFiscaIndividus = openFiscaRoot.getIndividus();
	    OpenFiscaIndividu openFiscaIndividu = openFiscaIndividus.get(DEMANDEUR);
	    OpenFiscaPeriodes openFiscaNombreJoursRestantsARE = openFiscaIndividu.getNombreJoursRestantsARE();
	    String periodeFormateeComplementARE = openFiscaPeriodeMappeur.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
	    Double nombreJoursRestantsARE = (Double) openFiscaNombreJoursRestantsARE.get(periodeFormateeComplementARE);

	    return BigDecimal.valueOf(nombreJoursRestantsARE).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public OpenFiscaIndividu addComplementAREOpenFiscaIndividu(OpenFiscaIndividu openFiscaIndividu, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {

	AllocationARE allocationARE = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE();

	openFiscaIndividu.setDegressiviteAre(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(allocationARE.hasDegressiviteAre(), dateDebutSimulation));
	openFiscaIndividu.setAllocationJournaliere(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(allocationARE.getAllocationJournaliereBrute(), dateDebutSimulation));
	openFiscaIndividu.setAllocationJournaliereTauxPlein(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(allocationARE.getAllocationJournaliereBrute(), dateDebutSimulation));
	openFiscaIndividu.setSalaireJournalierReference(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(allocationARE.getSalaireJournalierReferenceBrut(), dateDebutSimulation));
	openFiscaIndividu.setNombreJoursRestantsARE(
		openFiscaPeriodeMappeur.creerPeriodeUniqueOpenFisca(areUtile.getNombreJoursRestantsApresPremierMois(demandeurEmploi, dateDebutSimulation), dateDebutSimulation));

	openFiscaIndividu.setNombreJoursIndemnisesComplementARE(openFiscaPeriodeMappeur.creerPeriodesCalculeesNouvelleAideOpenFisca(dateDebutSimulation));
	openFiscaIndividu.setAllocationMensuelleComplementARE(openFiscaPeriodeMappeur.creerPeriodesCalculeesNouvelleAideOpenFisca(dateDebutSimulation));
	openFiscaIndividu.setDeductionsMensuellesComplementARE(openFiscaPeriodeMappeur.creerPeriodesCalculeesNouvelleAideOpenFisca(dateDebutSimulation));
	openFiscaIndividu.setAllocationMensuelleApresDeductionsComplementARE(openFiscaPeriodeMappeur.creerPeriodesCalculeesNouvelleAideOpenFisca(dateDebutSimulation));

	return openFiscaIndividu;
    }

    public OpenFiscaIndividu addAREAvantSimulationOpenFiscaIndividu(OpenFiscaIndividu openFiscaIndividu, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {

	openFiscaIndividu.setARE(openFiscaPeriodeMappeur.creerPeriodesAREAvantSimulation(demandeurEmploi, dateDebutSimulation));

	return openFiscaIndividu;
    }
}
