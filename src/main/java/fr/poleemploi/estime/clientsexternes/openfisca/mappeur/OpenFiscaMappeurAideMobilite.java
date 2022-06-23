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
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class OpenFiscaMappeurAideMobilite {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    @Autowired
    private DateUtile dateUtile;

    private static final String CONTEXTE_REPRISE_EMPLOI = "reprise_emploi";
    private static final String CATEGORIE_DEMANDEUR_EMPLOI = "categorie_1";

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaMappeurAideMobilite.class);

    public float getMontantAideMobilite(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaIndividu> openFiscaIndividus = openFiscaRoot.getIndividus();
	    OpenFiscaIndividu openFiscaIndividu = openFiscaIndividus.get(DEMANDEUR);
	    OpenFiscaPeriodes openFiscaAideMobilite = openFiscaIndividu.getAideMobilite();
	    String periodeFormateeAideMobilite = openFiscaPeriodeMappeur.getPeriodeNumeroMoisSimule(dateDebutSimulation, numeroMoisSimule);
	    Double montantAideMobilite = (Double) openFiscaAideMobilite.get(periodeFormateeAideMobilite);

	    return BigDecimal.valueOf(montantAideMobilite).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public OpenFiscaIndividu addAideMobiliteOpenFiscaIndividu(OpenFiscaIndividu openFiscaIndividu, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	openFiscaIndividu.setTypeContratTravail(
		openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepiEtAideMobilite(demandeurEmploi.getFuturTravail().getTypeContrat().toLowerCase(), dateDebutSimulation));
	openFiscaIndividu.setContexteActivite(openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepiEtAideMobilite(CONTEXTE_REPRISE_EMPLOI, dateDebutSimulation));
	openFiscaIndividu.setCategorieDemandeurEmploi(openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepiEtAideMobilite(CATEGORIE_DEMANDEUR_EMPLOI, dateDebutSimulation));
	openFiscaIndividu.setAideMobiliteDateDemande(
		openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepiEtAideMobilite(dateUtile.convertDateToStringOpenFisca(dateDebutSimulation), dateDebutSimulation));
	openFiscaIndividu.setNombreAllersRetours(
		openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepiEtAideMobilite(demandeurEmploi.getFuturTravail().getNombreTrajetsDomicileTravail(), dateDebutSimulation));
	openFiscaIndividu.setDistanceActiviteDomicile(
		openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepiEtAideMobilite(demandeurEmploi.getFuturTravail().getDistanceKmDomicileTravail(), dateDebutSimulation));
	openFiscaIndividu.setDebutContratTravail(
		openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepiEtAideMobilite(dateUtile.convertDateToStringOpenFisca(dateDebutSimulation), dateDebutSimulation));
	if (demandeurEmploi.getFuturTravail().getNombreMoisContratCDD() != null) {
	    openFiscaIndividu.setDureeContratTravail(
		    openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepiEtAideMobilite(demandeurEmploi.getFuturTravail().getNombreMoisContratCDD(), dateDebutSimulation));
	}
	openFiscaIndividu.setAideMobilite(openFiscaPeriodeMappeur.creerPeriodesCalculeesAgepiEtAideMobiliteOpenFisca(dateDebutSimulation));

	return openFiscaIndividu;
    }
}
